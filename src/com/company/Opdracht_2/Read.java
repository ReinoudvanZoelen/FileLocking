package com.company.Opdracht_2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;

public class Read extends Thread {

    private FileChannel fc;
    private MappedByteBuffer mbb;


    public Read(FileChannel fileChannel, MappedByteBuffer mbb) {
        this.fc = fileChannel;
        this.mbb = mbb;
    }

    @Override
    public void run() {
        boolean finished = false;

        while (!finished) {

            int index = readIndex();
            int clustersize = readClustersize();

            System.out.println("Index: " + index);
            System.out.println("Clustersize: " + clustersize);

            Byte[] readFromBytes = readBytes(index, clustersize);

            String entry = "";

            for (Byte b : readFromBytes) {
                entry += b;
            }

            System.out.println("Reader has read the entry: " + entry);

            if (true) {
                finished = true;
            }
        }
    }

    private int readIndex() {
        FileLock fl = attemptGetIndexLock();

        int index = -1;

        System.out.println("Locked the first byte (index)");

        mbb.position(0);
        //mbb.limit(1);
        //ByteBuffer buff;
        //buff = mbb.slice();

        index = (int) mbb.get();

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (index == -1) {
            index = 3;
            System.out.println("Index could not be read. Setting it to " + index + ".");
        }


        return index;
    }

    private FileLock attemptGetIndexLock() {
        FileLock fl = null;
        while (fl == null) {
            try {
                fl = fc.tryLock(0, 1, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fl;
    }

    private int readClustersize() {
        FileLock fl = attemptGetClustersizeLock();

        int clustersize = -1;

        System.out.println("Locked the second byte (cluster).");

        mbb.position(1);
        //mbb.limit(2);
        //ByteBuffer buff;
        //buff = mbb.slice();

        clustersize = (int) mbb.get();

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (clustersize == -1) {
            clustersize = 8;
            System.out.println("Clustersize could not be read. Setting it to " + clustersize + ".");
        }

        return clustersize;
    }

    private FileLock attemptGetClustersizeLock() {
        FileLock fl = null;
        while (fl == null) {
            try {
                fl = fc.tryLock(1, 1, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fl;
    }

    private Byte[] readBytes(int index, int clusterSize) {
        FileLock fl = attemptGetBytesLock(index, clusterSize);

        System.out.println("Locked the byte " + index + " to " + (clusterSize + index) + " for reading.");

        mbb.position(1);
        mbb.limit(2);

        ByteBuffer buff;
        buff = mbb.slice();

        ArrayList<Byte> items = new ArrayList<>();

        while (buff.position() < buff.limit()) {
            items.add(buff.get());
        }

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("The lock has been released.");

        if (items.size() == 0) {
            System.out.println("Entries could not be found.");
        }

        Byte[] bytes = items.toArray(new Byte[0]);

        return bytes;
    }

    private FileLock attemptGetBytesLock(int position, int size) {
        FileLock fl = null;
        while (fl == null) {
            try {
                fl = fc.tryLock(position, size, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fl;
    }
}

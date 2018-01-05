package com.company.Opdracht_2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

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

            System.out.println("Clustersize: " + clustersize);

            readBytes(index, 8);

            if (true) {
                finished = true;
            }
        }
    }

    private int readIndex() {
        FileLock fl = attemptGetIndexLock();

        System.out.println("Locked: " + 0 + " to " + 1);

        mbb.position(0);
        mbb.limit(1);

        ByteBuffer buff;
        buff = mbb.slice();

        while (buff.position() < buff.limit()) {
            byte b = buff.get();
            System.out.println(b);
        }

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
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

        System.out.println("Locked: " + 1 + " to " + 2);

        mbb.position(1);
        mbb.limit(2);

        ByteBuffer buff;
        buff = mbb.slice();

        while (buff.position() < buff.limit()) {
            System.out.println(buff.get());
        }

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private FileLock attemptGetClustersizeLock() {
        FileLock fl = null;
        while (fl == null) {
            try {
                fl = fc.tryLock(1, 2, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fl;
    }

    private byte[] readBytes(int index, int length) {
        attemptGetBytesLock();
        return null;
    }

    private void attemptGetBytesLock() {

    }
}

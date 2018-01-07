package com.company.Opdracht_2;

import java.io.*;
import java.nio.channels.FileLock;

public class Read extends Thread {

    @Override
    public void run() {
        boolean finished = false;

        System.out.println("Starting to read...");

        while (!finished) {
            int index = LockTools.readIndex();
            int clustersize = LockTools.readClustersize();

            byte[] readFromBytes = readBytes(index, clustersize);

            try {
                String entry = new String(readFromBytes, "UTF-8");

                System.out.println("Read: " + entry);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (true) {
                finished = true;
            }
        }
    }


    private byte[] readBytes(int position, int clusterSize) {
        FileLock fl = LockTools.AttemptGetLock(position, clusterSize);

        System.out.println("Locked the byte " + position + " to " + (clusterSize + position) + " for reading.");

        byte[] bytes = LockTools.ReadMultipleFromFileChannel(position, clusterSize);

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bytes;
    }
}

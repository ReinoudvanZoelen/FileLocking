package com.company.Opdracht_2;

import java.io.*;
import java.nio.channels.FileLock;

public class Read implements Runnable {

    @Override
    public void run() {
        boolean finished = false;

        System.out.println("R: Starting to read...");

        while (!finished) {
            int index = LockTools.readIndex();
            int clustersize = LockTools.readClustersize();

            System.out.println("R: Index " + index + ", Clustersize " + clustersize);

            byte[] readFromBytes = readBytes(index, clustersize);

            try {
                String entry = new String(readFromBytes, "UTF-8");

                System.out.println("R: " + entry);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (true) {
                //finished = true;
            }
        }
    }


    private byte[] readBytes(int position, int clusterSize) {
        FileLock fl = LockTools.AttemptGetLock(position, clusterSize);

        System.out.println("R: Locked the byte " + position + " to " + (clusterSize + position) + " for reading.");

        byte[] bytes = LockTools.ReadMultipleFromFileChannel(position, clusterSize);

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bytes;
    }
}


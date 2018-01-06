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
//
            System.out.println("Index: " + index);
            System.out.println("Clustersize: " + clustersize);
            //            System.out.println("Clustersize: " + clustersize);
//
//            byte[] readFromBytes = readBytes(index, clustersize);
//
//            String entry = "";
//
//            for (byte b : readFromBytes) {
//                entry += b;
//            }

//            System.out.println("Reader has read the entry: " + entry);

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
            System.out.println("The lock has been released.");
        } catch (IOException e) {
            e.printStackTrace();
        }


        return bytes;
    }
}

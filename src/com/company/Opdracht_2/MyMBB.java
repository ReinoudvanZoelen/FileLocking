package com.company.Opdracht_2;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class MyMBB {
    static final int LENGTH = 0x8FFFFFF; // 128 Mb
    static RandomAccessFile raf;
    static FileChannel fc;

    public static void main(String[] args) {
        try {
            raf = new RandomAccessFile("test.dat", "rw");
            fc = raf.getChannel();

            MappedByteBuffer out = fc.map(FileChannel.MapMode.READ_WRITE, 0, LENGTH);


            for (int i = 0; i < LENGTH; i++) {
                out.put((byte) i);
            }

            new Read(fc, out).run();

            new LockAndModify(out, 0, 0 + LENGTH / 3);
            new LockAndModify(out, LENGTH / 2, LENGTH / 2 + LENGTH / 4);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static class LockAndModify extends Thread {
        private ByteBuffer buff;
        private int start, end;

        LockAndModify(ByteBuffer mbb, int start, int end) {
            this.start = start;
            this.end = end;
            mbb.limit(end);
            mbb.position(start);
            buff = mbb.slice();
            start();
        }

        public void run() {

            FileLock fl = null;

            while (fl == null) {
                try {
                    System.out.println("Attempting to get lock...");
                    fl = fc.tryLock(start, end, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Received my lock");

            try {
                // Exclusive lock with no overlap:
                System.out.println("Locked: " + start + " to " + end);
                // Perform modification:
                while (buff.position() < buff.limit() - 1) {
                    buff.put((byte) 'R');
                }
                fl.release();
                System.out.println("Released: " + start + " to " + end);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
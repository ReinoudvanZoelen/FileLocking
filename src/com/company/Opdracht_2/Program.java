package com.company.Opdracht_2;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class Program {
    static final int LENGTH = 128;//0x8FFFFFF; // 128 Mb
    public static RandomAccessFile raf;
    public static FileChannel fc;
    public static MappedByteBuffer mbb;

    public static void main(String[] args) {
        try {
            System.out.println("Starting applicaton...");
            raf = new RandomAccessFile("test.dat", "rw");
            fc = raf.getChannel();

            mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, LENGTH);

            for (int i = 0; i < LENGTH; i++) {
                mbb.put(i, (byte) 'x');
            }

            new Write().run();

            Thread.sleep(1000);

            new Read().run();

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
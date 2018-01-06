package com.company.Opdracht_2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.util.ArrayList;

import static com.company.Opdracht_2.Program.mbb;
import static com.company.Opdracht_2.Program.fc;

public class LockTools {

    public static int convertFourBytesToInt(int startIndex) {
        mbb.position(startIndex);

        byte[] receivedByte = new byte[4];

        for (int i = 0; i < 4; i++) {
            receivedByte[i] = mbb.get(startIndex + i);
        }

        int totalValue = 0;

        for (int i = 0; i < 4; i++) {

            int multiplier = (receivedByte.length - 1 - i);

            // https://stackoverflow.com/a/9609447 regarding 0xff
            int value = (receivedByte[i] & 0xff);

            int power = (int) Math.pow(256, multiplier);

            if (multiplier != 0) {
                totalValue += value * power;
            } else {
                totalValue += value;
            }
        }

        return totalValue;
    }

    public static byte[] ReadMultipleFromFileChannel(int position, int length) {
        mbb.position(position);
        mbb.limit(position + length);

        ByteBuffer buff = mbb.slice();

        byte[] buffArrayOutput = new byte[length];

        int counter = 0;
        while (buff.hasRemaining()) {
            byte b = buff.get();
            buffArrayOutput[counter] = b;
            counter++;

            System.out.print((char) b);
        }

        System.out.println("");

        return buffArrayOutput;
    }

    public static byte[] splitIntToByteArray(int integer) {
        ByteBuffer bytebuffer = ByteBuffer.allocate(4);
        bytebuffer.putInt(integer);
        return bytebuffer.array();
    }

    public static FileLock AttemptGetLock(int position, int size) {
        FileLock fl = null;
        while (fl == null) {
            try {
                fl = fc.tryLock(position, size, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("A lock has been given. Position " + position + " and size " + size);

        return fl;
    }

    public static boolean Write(int position, byte[] content) {
        if ((content.length + position) > Program.LENGTH) {
            System.err.print("Data was attempted to be written outside of the admitted bounds");
            return false;
        }

        System.out.println("Starting to write " + content.length + " bytes.");

        try {
            mbb.position(position);
            for (int i = 0; i < content.length; i++) {
                System.out.println("Cursor position: " + mbb.position());
                mbb.put(content[i]);
            }

            System.out.println("All bytes have been written successfully.");
            return true;
        } catch (Exception ex) {
            System.err.print(ex);
            return false;
        }
    }

    public static int readIndex() {
        FileLock fl = LockTools.AttemptGetLock(0, 4);

        int indexValue = LockTools.convertFourBytesToInt(0);

        System.out.println("Read index value: " + indexValue);

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return indexValue;
    }

    public static int readClustersize() {
        FileLock fl = LockTools.AttemptGetLock(4, 4);

        int clustersize = LockTools.convertFourBytesToInt(4);

        try {
            fl.release();
            System.out.println("The lock has been released.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clustersize;
    }
}

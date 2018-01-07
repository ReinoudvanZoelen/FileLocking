package com.company.Opdracht_2;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Random;

public class Write extends Thread {
    private ArrayList<String> names = new ArrayList<>();
    private String lastWrittenName = "";

    public Write() {
        // region Adding names to the names arraylist...
        names.add("Reinoud van Zoelen");
        names.add("Niels Werkman");
        names.add("Bjorn Hamels");
        names.add("Bas van Wijk");
        names.add("Bono IJpelaar");
        names.add("Taro van Erp");
        names.add("Xian Dumoulin");
        names.add("Satish Rouwenhorst");
        names.add("Boban de Weijer");
        names.add("Faysal Gritter");
        names.add("Willemina van Aerle");
        names.add("Obbe Zaaijer");
        names.add("Jutta Eltink");
        names.add("Esra Schutte");
        names.add("Eser Averesch");
        names.add("Ulrich van der Zaag");
        names.add("Wang Frenken");
        names.add("Nilesh Trienekens");
        //endregion
    }

    @Override
    public void run() {
        boolean finished = false;

        // clean up the first 16 bytes, could be left over from last run
        writeIndex(8);
        writeClustersize(0);

        System.out.println("Starting to write...");

        while (!finished) {
            String name = getRandomName();

            int startingIndex = LockTools.readIndex();

            System.out.println("Name to be written: " + name + " at index " + startingIndex);

            writeIndex(startingIndex + lastWrittenName.length());
            writeClustersize(name.length());
            writeContent(name, startingIndex);

            this.lastWrittenName = name;

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

    public void writeIndex(int index) {
        FileLock fl = LockTools.AttemptGetLock(0, 4);

        byte[] splitInteger = LockTools.splitIntToByteArray(index);

        LockTools.Write(0, splitInteger);

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeClustersize(int clustersize) {
        FileLock fl = LockTools.AttemptGetLock(4, 4);

        byte[] splitInteger = LockTools.splitIntToByteArray(clustersize);

        LockTools.Write(4, splitInteger);

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void writeContent(String name, int index) {
        FileLock fl = LockTools.AttemptGetLock(index, name.length());

        LockTools.Write(index, name.getBytes());

        try {
            fl.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRandomName() {
        return this.names.get(new Random().nextInt(names.size()));
    }
}

package com.company.Opdracht_2;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Write extends Thread {

    private FileChannel fc;
    private MappedByteBuffer mbb;

    public Write(FileChannel fileChannel, MappedByteBuffer mbb) {
        this.fc = fileChannel;
        this.mbb = mbb;
    }

    @Override
    public void run() {

    }
}

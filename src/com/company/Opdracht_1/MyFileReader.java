package com.company.Opdracht_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MyFileReader implements Runnable {

    private String path;

    public MyFileReader(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        PrintLines(ExtractLines());
    }

    private List<String> ExtractLines() {
        System.out.println("- Extracting file");
        List<String> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
            return records;
        } catch (Exception e) {
            System.err.format("- Exception occurred trying to read '%s'.", path);
            e.printStackTrace();
            return null;
        }
    }

    private void PrintLines(List<String> records) {
        System.out.println("- Reading file");
        for (String record : records) {
            System.out.println(record);
        }
    }

}

package org.example;

import java.io.*;

public class FileManager {
    private final File file;

    public FileManager(String fileName) {
        this.file = new File(fileName);
    }

    public void write(String str) throws IOException {
        FileWriter writer = new FileWriter(file, true);
        writer.write(str + "\n");
        writer.close();
    }

    public void clear() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.write("X,Y,R,hit,start,time\n");
        writer.close();
    }
}

package org.example;

import java.io.*;

public class FileManager {
    private final File file;

    public FileManager(String fileName) {
        this.file = new File(fileName);
    }

    public void write(String str) {
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(str + "\n");
            writer.close();
        } catch (SecurityException | IOException e) {
            Main.logger.warning("Не удалось записать данные в файл");
        }
    }

    public void clear() {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.write("X,Y,R,hit,start,time\n");
            writer.close();
        } catch (SecurityException | FileNotFoundException e) {
            Main.logger.warning("Не удалось очистить файл");
        }
    }
}

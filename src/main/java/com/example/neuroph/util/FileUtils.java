package com.example.neuroph.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public FileUtils() {
    }

    public static void writeStringToFile(File file, String xml) throws IOException {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(xml);
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        } catch (IOException var14) {
            writer.close();
            var14.printStackTrace();
            throw var14;
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

    }

    public static String readStringFromFile(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";

            while((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }

            String var4 = stringBuffer.toString();
            return var4;
        } catch (FileNotFoundException var9) {
            throw var9;
        } catch (IOException var10) {
            throw var10;
        } finally {
            if (reader != null) {
                reader.close();
            }

        }
    }
}
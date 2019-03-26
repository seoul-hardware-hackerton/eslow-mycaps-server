package com.seoulhackerton.mycaps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Util {

    public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;

        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            System.out.println(file.getName());
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }
}

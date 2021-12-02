package com.uet.dbms.Process;

import java.io.*;

public class CopyImage {
    public static void copy(String filePath, String folderPath) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            FileOutputStream fos = new FileOutputStream(folderPath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bit = 0;
            while (bit != -1) {
                bit = bis.read();
                bos.write(bit);
            }
            bis.close();
            bos.close();
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + e.getMessage());
        }
    }
}

package com.glee;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class GLEngineConnection {
    public static String readData = "";
    public static final String filePath = System.getenv("APPDATA") + "/GLEngine";

    public static void initializeConnection(){
        checkConnectionDirectory();
        startReadingFile();
    }

    private static void startReadingFile() {
        Thread runThread = new Thread(() -> {
            try {
                while(true){
                    Thread.sleep(1000);
                    readFile();
                }
            }catch (Exception e){
                System.out.println(e);
            }
        });
        runThread.start();
    }

    private static void readFile() {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath + "/tmp", "r");
            FileChannel channel = file.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            StringBuilder sb = new StringBuilder();

            while (buffer.hasRemaining()) {
                sb.append((char) buffer.get());
            }
            readData = sb.toString();

            file.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void checkConnectionDirectory() {
        if(!new File(filePath).exists()){
            System.out.println("Creating directory...");
            new File(filePath).mkdir();
        }
        if(!new File(filePath + "/tmp").exists()){
            System.out.println("Creating connection file...");
            try {
                new File(filePath + "/tmp").createNewFile();
            }catch (Exception e){
                System.out.println("Error creating connection file:" + e.getMessage());
            }
        }
    }
}

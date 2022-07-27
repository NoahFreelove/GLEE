package com.glee;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class GLEngineConnection {
    public static String readData = "";
    public static final String filePath = System.getenv("APPDATA") + "/GLEngine";

    public static void initializeConnection(){
        checkConnectionDirectory();
    }

    public static void startReadingFile() {
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
            RandomAccessFile file = new RandomAccessFile(filePath + "/from", "r");
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

    public static void writeFile(String text, String ToOrFrom){
        try {
            FileWriter fw = new FileWriter(filePath + "/"+ToOrFrom);
            fw.write(text);
            fw.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static String readFile(String ToOrFrom){
        try {
            FileReader fr = new FileReader(filePath + "/"+ToOrFrom);
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = fr.read()) != -1) {
                sb.append((char) c);
            }
            fr.close();
            return sb.toString();
        }catch (Exception e){
            System.out.println(e);
        }
        return "";
    }

    private static void checkConnectionDirectory() {
        if(!new File(filePath).exists()){
            System.out.println("Creating directory...");
            new File(filePath).mkdir();
        }
        if(!new File(filePath + "/to").exists()){
            System.out.println("Creating connection file...");
            try {
                new File(filePath + "/to").createNewFile();
            }catch (Exception e){
                System.out.println("Error creating connection file:" + e.getMessage());
            }
        }
        if(!new File(filePath + "/from").exists()){
            System.out.println("Creating connection file...");
            try {
                new File(filePath + "/from").createNewFile();
            }catch (Exception e){
                System.out.println("Error creating connection file:" + e.getMessage());
            }
        }
    }
}

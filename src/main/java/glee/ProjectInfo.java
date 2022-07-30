package glee;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

public class ProjectInfo {
    public String name = "";
    public String sourcePath = "";
    public String binPath = "";

    private File projectFile;

    public ProjectInfo(File currentFile) {
        projectFile = currentFile;

        String path = Paths.get(currentFile.getAbsolutePath()).getParent().toString();

        try{
            FileReader reader = new FileReader(currentFile);
            char[] chars = new char[(int) currentFile.length()];
            reader.read(chars);
            reader.close();
            String fileContents = new String(chars);
            String[] lines = fileContents.split("\n");
            for(String line : lines){
                String[] formatted = line.split(":");
                if(formatted[0].equals("name")){
                    name = formatted[1];
                }
                if(formatted[0].equals("source")){
                    sourcePath = path + "\\" + formatted[1].replace("\n", "");
                }
                if(formatted[0].equals("bin")){
                    binPath = path + "\\" + formatted[1].replace("\n", "");
                }
            }
        }catch (Exception ignore){}
    }
}

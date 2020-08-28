package Loaders;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Loader {

    private File file;

    public abstract String getFileName();

    protected void loadFile() {
        this.file = new File(getFileName());

        if (!this.file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileContents() {
        try {
            return new String(Files.readAllBytes(Paths.get(file.getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

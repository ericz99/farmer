package Loaders;

import Models.Config;
import com.google.gson.Gson;

import lombok.Getter;

@Getter
public class ConfigLoader extends Loader {

    private Config config;

    public void loadConfig() {
        // # load config file
        loadFile();
        // # apply config
        this.config =  new Gson().fromJson(getFileContents(), Config.class);
    }

    @Override
    public String getFileName() {
        return System.getProperty("user.dir") + "/config.json";
    }
}

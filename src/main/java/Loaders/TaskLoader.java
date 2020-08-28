package Loaders;

import Models.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TaskLoader extends Loader {

    @Getter
    List<Task> tasks;

    public void loadTasks() {
        this.tasks = new ArrayList<>();
        // # load file
        loadFile();
        // # apply tasks
        this.tasks = new Gson().fromJson(getFileContents(), new TypeToken<List<Task>>(){}.getType());
    }

    @Override
    public String getFileName() {
        return System.getProperty("user.dir") + "/tasks.json";
    }
}

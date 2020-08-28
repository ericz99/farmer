import Loaders.ConfigLoader;
import Loaders.TaskLoader;
import Models.Task;
import lombok.Getter;

@Getter
public class MainBot {

    private static MainBot instance;

    private TaskLoader taskLoader;
    private ConfigLoader configLoader;

    public MainBot() {
        instance = this;
        // # register loaders
        registerLoaders();
        // # setup shutdownhooks
        setupShutdownHook();
        // # init all tasks
        initTasks();
    }

    public void registerLoaders() {
        (taskLoader = new TaskLoader()).loadTasks();
        (configLoader = new ConfigLoader()).loadConfig();
    }

    public void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(200);
                System.out.println("Shutting down ...");
                // some cleaning up code...
                // todo...
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }));
    }

    public void initTasks() {
        // # init tasks
        for (Task task : this.taskLoader.getTasks()) {
            // # start each task thread
            new BrowserTask(task).start();
        }
    }

    public static MainBot getInstance() {
        return instance;
    }
}

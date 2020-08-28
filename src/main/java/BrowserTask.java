import Models.Browser;
import Models.Config;
import Models.Task;

public class BrowserTask extends Thread {

    private Browser browser;
    private Task task;
    private Config config;

    public BrowserTask(Task task) {
        this.task = task;
        this.config = MainBot.getInstance().getConfigLoader().getConfig();
        // # setup browser
        this.browser = new Browser(this.task, this.config);
    }

    @Override
    public void run() {
        System.out.println("STARTING " + this.task.getId());
        // # initialize browser task
        this.browser.initBrowser();
    }
}

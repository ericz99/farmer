package Models;

import Constants.Default;
import lombok.Data;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Data
public class Browser {

    // # ALL DRIVER STUFF
    private ChromeOptions options = new ChromeOptions();
    private WebDriver driver;
    // # LIST OF OPTIONS/ARGS FOR THE DRIVER
    private List<String> arguments = Arrays.asList(
//            "--headless",
            "--disable-gpu",
            "--window-size=1920,1200",
            "--ignore-certificate-errors",
            "--silent",
            "--disable-infobars",
            "--enable-javascript",
            "--disable-web-security",
            "--allow-running-insecure-content"
    );

    // # OTHER STUFF
    private boolean isLoggedIn;
    private boolean shouldExit = false;
    private boolean shouldBreak = true;
    private ArrayList<String> words;
    private Config config;
    private Task task;

    public Browser(Task task, Config config) {
        // # only apply if chrome path is used
        if (!config.getChromePath().isEmpty()) {
            // # setup chrome driver
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");
            // # remove output when starting up
            System.setProperty("webdriver.chrome.silentOutput", "true");
        }

        this.task = task;
        this.config = config;

        // # cache the words
        getSearchWords();

        try {
            // # add all arguments to options
            this.options.addArguments(this.arguments);
            // # apply arguments to driver
            this.driver = new ChromeDriver(this.options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initBrowser() {
        System.out.println("Launching browser!");

        if (!this.isLoggedIn) {
            System.out.println("Trying to log into gmail account!");
            // # login to gmail
            loginToGmail();
        }
    }

    public void loginToGmail() {
        this.driver.get(Default.GMAIL_URL);
        // # wait until page load
        sleep(2000);
        // # click on login as google
        this.driver.findElement(By.cssSelector("button[data-provider=google]")).click();
        // # wait until page load
        sleep(2000);
        // # find element email input
        this.driver.findElement(By.id("identifierId")).sendKeys(this.task.getEmail().getEmail());
        // # click next
        this.driver.findElement(By.id("identifierNext")).click();
        // # wait until page load
        sleep(2000);
        // # find element password input
        this.driver.findElement(By.cssSelector("input[type=password]")).sendKeys(this.task.getEmail().getPassword());
        // # click next
        this.driver.findElement(By.id("passwordNext")).click();
        // # wait until page load
        sleep(2000);
        // # redirect to gmail
        this.driver.get("https://myaccount.google.com/");

        // # start module after logging into gmail
        if (this.driver.getCurrentUrl().contains("myaccount.google.com")) {
            this.isLoggedIn = true;
            System.out.println("Successfully logged into gmail account!");
            // # select the right module that user specify
            switch (task.getType()) {
                case BROWSING:
                    browseWeb();
                    break;
                case DOCS:
                    writeDocs();
                    break;
                default:
                    watchYoutube();
                    break;
            }
        }
    }

    /**
     *
     * @param ms - Wait in millsecond
     */
    public void sleep(int ms)  {
        try {
            // # wait until page load
            Thread.sleep(ms);
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSearchWords() {
        this.words = new ArrayList<>();
        String next;

        try {
            BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/search.txt"));

            while ((next = br.readLine()) != null) {
                this.words.add(next);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.words;
    }

    public void typeSimulate(WebElement element, String word) {
        String[] letter = word.split("");

        for (String c : letter) {
            element.sendKeys(c);
            // # sleep then type again
            sleep(150);
        }

        // # delay before going to next state
        sleep(1000);
    }

    public void scrollToBottom() {
        JavascriptExecutor js = ((JavascriptExecutor) this.driver);
        boolean isBottom = false;
        int distance = 100;
        int delay = 100;

        while (!isBottom) {
            js.executeScript("document.scrollingElement.scrollBy(0," + distance + ");");
            long currentPos = (long) js.executeScript("return document.scrollingElement.scrollTop + window.innerHeight");
            long endPos =  (long) js.executeScript("return document.scrollingElement.scrollHeight");

            if (currentPos >= endPos) {
                isBottom = true;
            }

            sleep(delay);
        }

        sleep(2500);
    }

    /**
     *
     * Watches youtube from a set time
     * @author Eric Zhang
     */
    public void watchYoutube() {
        if (this.isLoggedIn) {
            System.out.println("Now watching youtube!");

            // # we shouldn't exit the program until
            // # a certain constraint happens
            while (!this.shouldExit) {
                // # go to youtube
                this.driver.get(Default.YOUTUBE_URL);
                // # click on random video
                List<WebElement> elements = this.driver.findElements(By.id("thumbnail"));
                WebElement randomVideo = elements.get((int) Math.floor(Math.random() * elements.size()));
                // # just apply the random video
                this.driver.get(randomVideo.getAttribute("href"));

                try {
                    System.out.println("SLEEPING FOR " + this.config.getDuration() + "Minute!");
                    // # let program run for xx min
                    Thread.sleep(TimeUnit.MINUTES.toMillis(this.config.getDuration()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // # only go on break after duration is completed
                if (this.shouldBreak) {
                    // # redirect to google for short break
                    this.driver.get("https://www.google.com/");

                    try {
                        System.out.println("ON BREAK FOR " + this.config.getBreakInterval() + "MS!");
                        // # let program sleep for xx interval
                        Thread.sleep(this.config.getBreakInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // # check if we should close after break
                    if (this.config.isCloseAfterBreakInterval()) {
                        this.shouldExit = true;
                        // # close driver
                        this.driver.close();
                    }
                }
            }
        }
    }

    /**
     *
     * Browse Google Search, and click on links + human like activity
     * @author Eric Zhang
     */
    public void browseWeb() {
        long futureTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(this.config.getDuration());

        if (this.isLoggedIn) {
            System.out.println("Now browsing google!");
            // # we shouldn't exit the program until
            // # a certain constraint happens
            while (!this.shouldExit) {
                this.driver.get(Default.GOOGLE_URL);
                sleep(2500);
                String randomWord = this.words.get((int) Math.floor(Math.random() * this.words.size()));
                WebElement searchInput = this.driver.findElement(By.cssSelector("input[title=Search]"));
                // # SIMULATE TYPING
                typeSimulate(searchInput, randomWord);
                searchInput.submit();
                sleep(2500);
                // # click on random link
                List<WebElement> links = this.driver.findElements(By.tagName("a"));
                String link = links.get((int) Math.floor(Math.random() * links.size())).getAttribute("href");

                System.out.println(link);

                if (link != null && link.startsWith("https://")) {
                    this.driver.get(link);
                }

                sleep(1000);
                // # scroll bottom of the page
                scrollToBottom();

                // # we should take a break
                if (System.currentTimeMillis() >= futureTime) {
                    if (this.shouldBreak) {
                        try {
                            System.out.println("ON BREAK FOR " + this.config.getBreakInterval() + "MS!");
                            // # let program sleep for xx interval
                            Thread.sleep(this.config.getBreakInterval());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // # update back future time
                        futureTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(this.config.getDuration());

                        // # check if we should close after break
                        if (this.config.isCloseAfterBreakInterval()) {
                            this.shouldExit = true;
                            // # close driver
                            this.driver.close();
                        }
                    }
                }

            }
        }
    }

    /**
     *
     * Write to google docs
     * @author Eric Zhang
     */
    public void writeDocs() {
        long futureTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(this.config.getDuration());

        if (this.isLoggedIn) {
            System.out.println("Now writing docs!");

            // # TODO: IN PROGRESS | DO LATER

            // # we shouldn't exit the program until
            // # a certain constraint happens

            this.driver.get("https://docs.google.com/document/u/0/");
            sleep(2000);
            this.driver.findElement(By.id(":1g")).click();
            sleep(5000);

            while (!this.shouldExit) {
                String randomWord = this.words.get((int) Math.floor(Math.random() * this.words.size()));
                WebElement textArea = this.driver.findElement(By.cssSelector("input[title=Search]"));

            }
        }
    }

}

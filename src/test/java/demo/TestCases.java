package demo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.time.Duration;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestCases {
        ChromeDriver driver;
        WebScraperWrapper wrapper;
        SoftAssert softAssert;

        @BeforeClass(alwaysRun = true)
        public void initialize_driver() {
                driver = new ChromeDriver();
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                wrapper = new WebScraperWrapper(driver);
                softAssert = new SoftAssert();
        }

        @AfterClass(alwaysRun = true)
        public void close_and_quit_driver() {
                driver.close();
                driver.quit();
        }

    //Testcase 01: Fetch Hockey Teams data and store in JSON
    @Test(priority = 1, description = "Fetch Hockey Teams data and store in JSON")
    public void fetchHockeyTeamsData() throws IOException {
        driver.get("https://www.scrapethissite.com/pages/");
        wrapper.clickElement(By.linkText("Hockey Teams: Forms, Searching and Pagination"));

        List<HashMap<String, Object>> hockeyData = new ArrayList<>();

        //Fetch first four page data

        for (int i = 0; i < 4; i++) {

            wrapper.clickElement(By.xpath("//a[@aria-label='Next']"));

            //Get data of all 25 rows in a page
            for (int j = 1; j < 26; j++) {
                String teamName = wrapper.getElementText(By.xpath("(//table/tbody/tr/td[1])["+j+"]"));
                String year = wrapper.getElementText(By.xpath("(//table/tbody/tr/td[2])["+j+"]"));
                String winPercentageStr = wrapper.getElementText(By.xpath("(//table/tbody/tr/td[6])["+j+"]"));
                double winPercentage = Double.parseDouble(winPercentageStr);

                if (winPercentage < 0.40) {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("EpochTime", Instant.now().getEpochSecond());
                    data.put("TeamName", teamName);
                    data.put("Year", year);
                    data.put("WinPercentage", winPercentage);
                    hockeyData.add(data);
                }
            }
        }

        //Save as JSON File
        wrapper.saveDataAsJSON("hockey-team-data.json", hockeyData);

        String fileLocation = System.getProperty("user.dir")+"/src/test/output/hockey-team-data.json";        
        File file = new File(fileLocation);

        softAssert.assertTrue(file.exists(), "JSON file does not exist");
        softAssert.assertTrue(file.length() > 0, "JSON file is empty");
        softAssert.assertAll();
    }

    //Testcase 02: Fetch Oscar Winning Films data and store in JSON
    @Test(priority = 2, description = "Fetch Oscar Winning Films data and store in JSON")
    public void fetchOscarWinningFilmsData() throws IOException, InterruptedException {
        driver.get("https://www.scrapethissite.com/pages/");
        wrapper.clickElement(By.partialLinkText("Oscar Winning Films"));

        List<HashMap<String, Object>> oscarData = new ArrayList<>();

        //Get all years in the list
        List<WebElement> years = wrapper.findElements(By.xpath("//section[@id='oscars']/div/div[4]/div/a"));

        for (WebElement yearElement : years) {
            String year = yearElement.getText();
            wrapper.clickElement(By.linkText(year));
            Thread.sleep(3000);

            //Fetch first 5 movie in the list
            for (int j = 1; j < 6; j++) {
                boolean isWinner = false;
                String title = wrapper.getElementText(By.xpath("(//table/tbody/tr/td[1])["+j+"]"));
                String nomination = wrapper.getElementText(By.xpath("(//table/tbody/tr/td[2])["+j+"]"));
                String awards = wrapper.getElementText(By.xpath("(//table/tbody/tr/td[3])["+j+"]"));
                try {
                    WebElement Winner = driver.findElement(By.xpath("(//table/tbody/tr/td[4])["+j+"]/i"));
                    if (Winner.isDisplayed()) {
                        isWinner = true;
                    }
                } catch (NoSuchElementException e) {
                    isWinner = false;
                }

                HashMap<String, Object> data = new HashMap<>();
                data.put("EpochTime", Instant.now().getEpochSecond());
                data.put("Year", year);
                data.put("Title", title);
                data.put("Nomination", nomination);
                data.put("Awards", awards);
                data.put("isWinner", isWinner);
                oscarData.add(data);
            }

        }

        //Save as JSON File
        wrapper.saveDataAsJSON("oscar-winner-data.json", oscarData);

        String fileLocation = System.getProperty("user.dir")+"/src/test/output/oscar-winner-data.json";        
        File file = new File(fileLocation);

        softAssert.assertTrue(file.exists(), "JSON file does not exist");
        softAssert.assertTrue(file.length() > 0, "JSON file is empty");
        softAssert.assertAll();
    }
}

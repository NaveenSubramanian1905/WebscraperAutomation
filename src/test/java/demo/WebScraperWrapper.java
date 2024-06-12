package demo;

import java.time.Duration;
import java.util.List;
import java.io.File;
import java.util.HashMap;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebScraperWrapper {

    ChromeDriver driver;
    WebDriverWait wait;

    public WebScraperWrapper(ChromeDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    public WebElement findElement(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public List<WebElement> findElements(By by) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    public void clickElement(By by) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.click();
    }

    public String getElementText(By by) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        return element.getText();
    }

    public void saveDataAsJSON(String fileName, List<HashMap<String, Object>> Data) throws StreamWriteException, DatabindException, IOException {        
        ObjectMapper mapper = new ObjectMapper();
        String fileLocation = System.getProperty("user.dir")+"/src/test/output/"+fileName;
        
        File file = new File(fileLocation);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        mapper.writeValue(new File(fileLocation), Data);
    }
    
}

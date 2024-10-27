import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToDoTest {

    private static AndroidDriver driver;

    @BeforeAll
    public static void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appium:platformVersion", "14.0");
        capabilities.setCapability("appium:deviceName", "Pixel 8");
        capabilities.setCapability("appium:undid", "emulator-5554");
        driver = new AndroidDriver(new URI("http://127.0.0.1:4723/wd/hub").toURL(), capabilities);
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @Test
    @Tag("ToDo")
    @DisplayName("Новое задание")
    public void newTask() {
        driver.findElement(AppiumBy.accessibilityId("New Task")).click();
        driver.findElements(By.className("android.widget.EditText")).getFirst().sendKeys("Тест");
        driver.findElements(By.className("android.widget.EditText")).getLast().sendKeys("Создать автотест для данного приложения");
        driver.hideKeyboard();
        driver.findElement(AppiumBy.accessibilityId("Save task")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//android.widget.TextView[@text='Task added']")));
        String result = driver.findElements(By.className("android.widget.TextView")).getLast().getText();
        assertEquals("Тест", result, "Задание не создано");
    }

    @Test
    @Tag("ToDo")
    @DisplayName("Статистика")
    public void statistics() {
        driver.findElement(AppiumBy.accessibilityId("Open Drawer")).click();
        driver.findElements(By.className("android.widget.Button")).getLast().click();
        String result = driver.findElements(By.className("android.widget.TextView")).getLast().getText();
        assertEquals("You have no tasks.", result, "Статистика не отображается");
    }

    @AfterEach
    public void cleanUp() {
        int X = (int) (driver.manage().window().getSize().getWidth() * 0.9);
        int Y = driver.manage().window().getSize().getHeight() / 2;
        PointerInput pointerInput = new PointerInput(PointerInput.Kind.TOUCH, "touch");
        Sequence sequence = new Sequence(pointerInput, 0);
        sequence.addAction(pointerInput.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), X, Y))
                .addAction(pointerInput.createPointerDown(0))
                .addAction(pointerInput.createPointerUp(0));
        driver.perform(List.of(sequence));

        String pageTitle = driver.findElements(By.className("android.widget.TextView")).getFirst().getText();
        switch (pageTitle) {
            case "Todo": break;
            case "New Task":
                driver.findElement(AppiumBy.accessibilityId("Back")).click();
                break;
            case "Statistics":
                driver.findElement(AppiumBy.accessibilityId("Open Drawer")).click();
                driver.findElements(By.className("android.widget.Button")).getFirst().click();
                break;
        }

        List<WebElement> checkboxes = driver.findElements(By.className("android.widget.CheckBox"));
        if (checkboxes.isEmpty()) return;
        for (WebElement checkbox : checkboxes) {if (!checkbox.isSelected()) checkbox.click();}
        driver.findElement(AppiumBy.accessibilityId("More")).click();
        driver.findElement(By.xpath("//android.widget.TextView[@text='Clear completed']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver -> driver.findElement(By.className("android.widget.CheckBox")).isDisplayed());
        driver.findElement(AppiumBy.accessibilityId("More")).click();
        driver.findElement(By.xpath("//android.widget.TextView[@text='Refresh']")).click();
    }

    @AfterAll
    public static void tearDown() {driver.quit();}

}
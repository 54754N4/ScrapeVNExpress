package scrape;

import java.io.Closeable;
import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/* Creates a browser using Selenium that follows the builder pattern
 * to ease with chained calls/actions. Also uses singleton pattern 
 * to reuse default instance, but still allows further browsers to be
 * created (e.g. public constructors).
 * Docs: https://www.selenium.dev/documentation/en/webdriver/   
 */
public class Browser implements Closeable {
	public static final long DEFAULT_TIMEOUT = 15, DEFAULT_POLLING = 5;	// in seconds
	private static final boolean DEFAULT_HEADLESS = true;
	private static Browser INSTANCE;			// lazy-loaded through Browser::getInstance
	
	static {
		// Makes sure firefox driver exists or downloads it
		WebDriverManager.firefoxdriver().arch64().setup();
	}

	private WebDriver driver;
	
	public Browser() {
		this(DEFAULT_HEADLESS);
	}
	
	public Browser(boolean headless) {
		driver = new FirefoxDriver(
				new FirefoxOptions()
					.setHeadless(headless)
					.setAcceptInsecureCerts(true)
					.addArguments( 
							"--disable-gpu", 
							"--window-size=1920,1200"
					));
	}
	
	@Override
	public void close() {
		kill();
	}
	
	public void kill() {
		driver.quit();
	}
	
	public String getTitle() {
		return driver.getTitle();
	}
	
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}
	
	public Dimension getSize() {
		return driver.manage().window().getSize();
	}
	
	public Browser setSize(int width, int height) {
		driver.manage().window().setSize(new Dimension(width, height));
		return this;
	}
	
	public Browser back() {
		driver.navigate().back();
		return this;
	}
	
	public Browser forward() {
		driver.navigate().forward();
		return this;
	}
	
	public Browser refresh() {
		driver.navigate().refresh();
		return this;
	}
	
	public Browser then(Consumer<WebDriver> consumer) {
		consumer.accept(driver);
		return this;
	}
	
	public <R> R get(Function<WebDriver, R> mapper) {
		return mapper.apply(driver);
	}
	
	public Browser visit(String url) {
		driver.navigate().to(url);
		return this;
	}
	
	public <T> T screenshotFullAs(OutputType<T> type) {
		return screenshotOf(By.tagName("body"), type);
	}
	
	public File screenshotAsFile() {
		return screenshotAs(OutputType.FILE);
	}
	
	public File screenshotFullAsFile() {
		return screenshotFullAs(OutputType.FILE);
	}
	
	public File screenshotFileOf(By by) {
		return screenshotOf(by, OutputType.FILE);
	}
	
	public <T> T screenshotAs(OutputType<T> type) {
		return ((TakesScreenshot) driver).getScreenshotAs(type);
	}
	
	public <T> T screenshotOf(By by, OutputType<T> type) {
		return ((TakesScreenshot) waitFor(by))
				.getScreenshotAs(type);
	}
	
	public WebElement waitFor(By by, long timeout) {
		return new WebDriverWait(driver, timeout)
				.until(driver -> driver.findElement(by));
	}
	
	public WebElement waitFor(By by, long timeout, long polling, Collection<Class<? extends Throwable>> exceptions) {
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeout))
				.pollingEvery(Duration.ofSeconds(polling))
				.ignoreAll(exceptions)
				.until(driver -> driver.findElement(by));
	}
	
	public WebElement waitFor(By by, long timeout, long polling, Class<? extends Throwable> exception) {
		return waitFor(by, timeout, polling, Arrays.asList(exception));
	}
	
	public WebElement waitFor(By by, long timeout, long polling) {
		return waitFor(by, timeout, polling, NoSuchElementException.class);
	}
	
	public WebElement waitFor(By by) {
		return waitFor(by, DEFAULT_TIMEOUT);
	}
	
	/* Static methods to act on default singleton instance */
	
	public static Browser restart() {
		if (INSTANCE != null) 
			INSTANCE.kill();
		return INSTANCE = new Browser();
	}
	
	public static Browser getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Browser();
		return INSTANCE;
	}
}

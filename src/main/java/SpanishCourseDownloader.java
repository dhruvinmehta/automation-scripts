import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SpanishCourseDownloader {
	private static final String DOWNLOAD_LOCATION = "/Users/dhruvinmehta/Languages/Spanish/";

	private static final int WAIT_TIME = 5000; // Increase this for slow internet connection;
	private static final int THREAD_POOL_SIZE = 10;

	private static final String COMPLETE_DOWNLOAD = "Download is complete, exiting......";
	private static final String INCOMPLETE_DOWNLOAD = "Still downloading, exiting.......";

	private final ExecutorService executor;
	private final List<Future> futures;

	SpanishCourseDownloader() {
		executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		futures = new ArrayList<>();
	}

	public static void main(String[] args) {
		SpanishCourseDownloader spanishCourseDownloader = new SpanishCourseDownloader();
		spanishCourseDownloader.downloadSpanishCourse();
	}

	private void downloadSpanishCourse() {
		String url = "https://www.languagetransfer.org/free-courses-1#complete-spanish";

		System.setProperty("webdriver.gecko.driver","src/main/resources/geckodriver");
		WebDriver driver = new FirefoxDriver();

		try {
			openWebPageAndWait(driver, url);
			downloadAudioLinksFromFrame(driver);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			teardown(driver);
		}
	}

	private void openWebPageAndWait(WebDriver driver, String url) throws InterruptedException {
		driver.get(url);
		Thread.sleep(WAIT_TIME);
	}

	private void downloadAudioLinksFromFrame(WebDriver driver) throws Exception {
		driver.switchTo().frame(0);
		for(int number = 1; number <= 90; number++) {
			List<WebElement> elements = driver.findElements(By.xpath("//div[@class='sc-font-light sc-truncate g-text-shadow'][contains(.,'Language Transfer - Complete Spanish, Track " + number + " - Language Transfer, The Thinking Method')]"));
			for (WebElement element : elements) {
				String location = getAudioLinkFromElement(driver, element);
				download(location, number);
			}
			Thread.sleep(WAIT_TIME);
		}
	}


	private String getAudioLinkFromElement(WebDriver driver, WebElement element) throws Exception {
		element.click();
		highlightElement(driver, element);
		Thread.sleep(WAIT_TIME / 5);
		return driver.findElement(By.linkText("Download")).getAttribute("href");
	}

	private void highlightElement(WebDriver driver, WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].setAttribute('style', 'border: 2px solid red;');", element);
	}

	private void download(String location, int number) throws Exception {
		futures.add(executor.submit(new DownloadThread(location, DOWNLOAD_LOCATION, number)));
	}

	private void waitForDownloadToComplete() {
		try {
			for (Future future : futures) {
				future.get();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void checkDownloadStatus() {
		boolean allTasksCompleted = true;
		for (Future future : futures) {
			allTasksCompleted &= future.isDone();
		}

		if(allTasksCompleted)
			System.out.println(COMPLETE_DOWNLOAD);
		else
			System.out.println(INCOMPLETE_DOWNLOAD);
	}

	private void teardown(WebDriver driver) {
		driver.close();
		waitForDownloadToComplete();
		checkDownloadStatus();
		executor.shutdown();
	}
}
package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.DriverFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Hooks {

    private static final Path[] EVIDENCE_DIRECTORIES = {
            Path.of("target/cucumber-reports"),
            Path.of("target/extent-reports"),
            Path.of("target/surefire-reports"),
            Path.of("target/videos")
    };
    private static final AtomicInteger SCENARIO_NUMBER = new AtomicInteger();
    private Process videoRecorder;
    private Path videoPath;

    @BeforeAll
    public static void cleanPreviousEvidence() {
        for (Path directory : EVIDENCE_DIRECTORIES) {
            deleteDirectory(directory);
        }
    }

    @Before
    public void setUp(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        if (isChrome() && driver != null) {
            startVideo(scenario);
        }
        System.out.println(">>> Starting scenario: " + scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String label = scenario.isFailed() ? "Failure screenshot" : "Scenario screenshot";
                scenario.attach(screenshot, "image/png", label);
            }
        } finally {
            stopVideo();
            DriverFactory.quitDriver();
        }

        System.out.println(">>> Finished scenario: " + scenario.getName() + " | Status: " + scenario.getStatus());
    }

    private void startVideo(Scenario scenario) {
        Path videoDirectory = Path.of("target/videos");
        try {
            Files.createDirectories(videoDirectory);
            String fileName = String.format("%03d-%s.mov",
                    SCENARIO_NUMBER.incrementAndGet(), sanitize(scenario.getName()));
            videoPath = videoDirectory.resolve(fileName);
            videoRecorder = new ProcessBuilder(
                    "screencapture", "-v", "-x", videoPath.toString())
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            if (!videoRecorder.isAlive()) {
                throw new IllegalStateException("macOS screen recorder exited before recording started");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start Chrome scenario recording at " + videoPath, exception);
        }
    }

    private void stopVideo() {
        if (videoRecorder == null) {
            return;
        }

        try {
            Process stopSignal = new ProcessBuilder(
                    "kill", "-INT", Long.toString(videoRecorder.pid()))
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            stopSignal.waitFor(5, TimeUnit.SECONDS);
            if (!videoRecorder.waitFor(5, TimeUnit.SECONDS)) {
                videoRecorder.destroyForcibly();
                videoRecorder.waitFor(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException exception) {
            videoRecorder.destroyForcibly();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while stopping scenario recording", exception);
        } catch (IOException exception) {
            videoRecorder.destroyForcibly();
            throw new IllegalStateException("Unable to stop scenario recording", exception);
        } finally {
            videoRecorder = null;
            videoPath = null;
        }
    }

    private static boolean isChrome() {
        return "chrome".equalsIgnoreCase(System.getProperty("browser", "chrome"));
    }

    private static String sanitize(String scenarioName) {
        return scenarioName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private static void deleteDirectory(Path directory) {
        if (!Files.exists(directory)) {
            return;
        }
        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exception) {
                    throw new IllegalStateException("Unable to clean old evidence: " + path, exception);
                }
            });
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to inspect old evidence directory: " + directory, exception);
        }
    }
}

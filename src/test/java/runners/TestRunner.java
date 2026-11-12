package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepdefinitions", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "junit:target/cucumber-reports/cucumber.xml"
        },
        monochrome = true
        // Tag filtering is driven from the CLI/CI, e.g.:
        //   mvn test -Dcucumber.filter.tags="@smoke"
        //   mvn test -Dcucumber.filter.tags="@regression and not @wip"
)
public class TestRunner extends AbstractTestNGCucumberTests {

    // Screen recording is desktop-wide, so scenarios must run one at a time.
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}

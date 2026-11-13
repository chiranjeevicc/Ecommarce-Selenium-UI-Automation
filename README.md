# Selenium + Cucumber + TestNG  Framework

A real-project-shaped automation framework, built on the SauceDemo (`h
`)
practice site, using Page Object Model + Cucumber BDD + TestNG + GitHub Actions CI/CD.

## Stack
- Java 25
- Selenium 4 (Selenium Manager — no manual driver binaries needed)
- Cucumber-JVM 7 (Gherkin feature files)
- TestNG (runner + parallel execution)
- Extent Reports (Spark HTML) + Cucumber HTML/JSON reports
- Maven
- GitHub Actions (headless Chrome CI)

## Structure
```
src/test/java/
  pages/            Page Object classes (one per screen/component)
  stepdefinitions/  Cucumber step definitions, call into page objects only
  hooks/            @Before/@After lifecycle (driver start/stop, screenshots on failure)
  runners/          TestNG-Cucumber runner + report plugin wiring
  utils/            DriverFactory (browser/headless resolution via -D flags)
src/test/resources/features/   .feature files (Gherkin)
testng.xml          suite definition, parallel execution config
.github/workflows/ci.yml       CI pipeline
```

## Run locally
```bash
# default: chrome, visible browser
mvn clean test

# headless
mvn clean test -Dheadless=true

# firefox
mvn clean test -Dbrowser=firefox

# only smoke tests
mvn clean test -Dcucumber.filter.tags="@smoke"

# smoke or regression, excluding anything tagged @wip
mvn clean test -Dcucumber.filter.tags="(@smoke or @regression) and not @wip"
```

Reports after a run:
- `target/cucumber-reports/cucumber.html` — plain Cucumber HTML report
- `target/extent-reports/SparkReport.html` — richer Extent Spark report with embedded failure screenshots

## How to add a new test case (daily practice loop)
1. Add/extend a `.feature` file under `src/test/resources/features/` in Gherkin.
2. If it needs a new screen, add a Page Object under `pages/` extending `BasePage`.
3. Implement any new step wording in a step definition class under `stepdefinitions/`.
4. Run locally: `mvn clean test -Dcucumber.filter.tags="@yourNewTag"`.
5. Commit + push to a feature branch, open a PR — GitHub Actions runs the suite automatically.
6. Merge to `main` — the scheduled nightly job will pick it up in the regression run too.

## Tagging convention used here
- `@smoke` — fast, critical-path checks, run on every PR
- `@regression` — fuller coverage, run nightly + on demand
- `@negative` — invalid-input / error-path scenarios
- `@wip` — not yet stable; excluded from CI by convention (add `and not @wip` to your filter)

## CI/CD (GitHub Actions)
`.github/workflows/ci.yml` runs on:
- every push/PR to `main` (fast feedback)
- a nightly cron (full regression)
- manual `workflow_dispatch` with a `tags` input, so you can trigger an ad-hoc run for just
  the tag expression you're currently working on from the Actions tab

Artifacts (Cucumber report, Extent report, Surefire XML) are uploaded on every run, pass or fail,
so a failure in CI gives you the same screenshot-attached report you'd get locally.

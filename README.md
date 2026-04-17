# OrangeHRM Hybrid Test Automation Framework

A **production-grade, interview-ready** Selenium + TestNG + POM + Data-Driven + Keyword-Driven
Hybrid Automation Framework for [OrangeHRM](https://opensource-demo.orangehrmlive.com/).

---

## Tech Stack

| Component         | Technology                     |
|-------------------|--------------------------------|
| Language          | Java 11                        |
| Automation Tool   | Selenium WebDriver 4.x         |
| Build Tool        | Maven 3.9+                     |
| Test Framework    | TestNG 7.x                     |
| Design Pattern    | POM + Data-Driven + Keyword-Driven (Hybrid) |
| Reporting         | Extent Reports 5.x (HTML, DARK theme) |
| Logging           | Log4j2 (console + rolling file)|
| Data Sources      | Excel (Apache POI) + JSON (Jackson) |
| CI/CD             | Jenkins (Declarative Pipeline) |
| Version Control   | Git                            |

---

## Folder Structure

```
OrangeHRM/
├── pom.xml                                         ← Maven build config + dependencies
├── Jenkinsfile                                     ← Jenkins declarative pipeline
├── .gitignore
│
├── src/
│   ├── main/
│   │   ├── java/com/orangehrm/
│   │   │   ├── base/
│   │   │   │   ├── BaseTest.java                  ← @BeforeMethod/@AfterMethod setup
│   │   │   │   └── BasePage.java                  ← Generic Selenium wrappers + Extent logging
│   │   │   ├── config/
│   │   │   │   └── ConfigReader.java              ← Singleton properties reader
│   │   │   ├── constants/
│   │   │   │   └── AppConstants.java              ← Application-wide magic string elimination
│   │   │   ├── driver/
│   │   │   │   ├── DriverFactory.java             ← Creates WebDriver (local/remote/headless)
│   │   │   │   └── DriverManager.java             ← ThreadLocal driver store (parallel-safe)
│   │   │   ├── enums/
│   │   │   │   ├── BrowserType.java               ← Type-safe browser selection
│   │   │   │   └── WaitStrategy.java              ← VISIBLE | CLICKABLE | PRESENCE | NONE
│   │   │   ├── keywords/
│   │   │   │   └── KeywordExecutor.java           ← Keyword-Driven engine
│   │   │   ├── listeners/
│   │   │   │   ├── TestListener.java              ← TestNG ↔ Extent Reports bridge
│   │   │   │   ├── RetryAnalyzer.java             ← Auto retry for flaky tests
│   │   │   │   └── AnnotationTransformer.java     ← Global retry without per-test annotation
│   │   │   ├── pages/
│   │   │   │   ├── LoginPage.java                 ← Login POM
│   │   │   │   ├── DashboardPage.java             ← Dashboard POM
│   │   │   │   └── ForgotPasswordPage.java        ← Forgot-password POM
│   │   │   ├── reporting/
│   │   │   │   ├── ExtentManager.java             ← Singleton ExtentReports instance
│   │   │   │   └── ExtentTestManager.java         ← ThreadLocal ExtentTest node
│   │   │   └── utils/
│   │   │       ├── WaitUtils.java                 ← Explicit / Fluent waits
│   │   │       ├── ScreenshotUtils.java           ← File + Base64 screenshots
│   │   │       ├── ExcelUtils.java                ← Apache POI Excel reader
│   │   │       ├── JsonUtils.java                 ← Jackson JSON reader
│   │   │       ├── JavaScriptUtils.java           ← JS executor helpers
│   │   │       └── DateTimeUtils.java             ← Timestamp helpers
│   │   │
│   │   └── resources/
│   │       ├── config/
│   │       │   └── config.properties              ← URL, browser, timeouts, env config
│   │       └── log4j2.xml                         ← Logging config
│   │
│   └── test/
│       ├── java/com/orangehrm/
│       │   ├── dataproviders/
│       │   │   └── LoginDataProvider.java         ← Excel + JSON DataProviders
│       │   └── tests/
│       │       ├── LoginTest.java                 ← 7 login test scenarios
│       │       └── KeywordDrivenTest.java         ← Keyword-driven scenarios
│       │
│       └── resources/
│           ├── testdata/
│           │   ├── login_data.json                ← JSON test data (5 records)
│           │   └── LoginTestData.xlsx             ← Excel test data (create manually)
│           └── testng-suites/
│               ├── smoke.xml                      ← Smoke suite (fast, on every commit)
│               ├── regression.xml                 ← Full regression
│               └── full.xml                       ← All tests incl. keyword-driven
│
└── test-output/                                   ← Generated at runtime (git-ignored)
    ├── ExtentReports/                             ← HTML reports
    ├── screenshots/                               ← Failure screenshots
    └── logs/                                      ← Log4j2 rolling logs
```

---

## Quick Start

### Prerequisites
- Java 11+
- Maven 3.9+
- Chrome / Firefox / Edge browser installed
- Git

### 1. Clone and install
```bash
git clone https://github.com/your-org/OrangeHRM-Framework.git
cd OrangeHRM-Framework
mvn clean install -DskipTests
```

### 2. Run Smoke Suite (Chrome, default)
```bash
mvn test -Dsuite=smoke -P smoke
```

### 3. Run Regression with Firefox
```bash
mvn test -Dsuite=regression -Dbrowser=firefox -P regression
```

### 4. Run headless (CI environments)
```bash
mvn test -Dsuite=smoke -Dbrowser=chrome-headless -P smoke
```

### 5. Run against Selenium Grid
```bash
mvn test -Dsuite=regression -Drun.remote=true -Dgrid.url=http://grid-host:4444/wd/hub
```

### 6. Override retry count
```bash
mvn test -Dsuite=smoke -Dmax.retry=3
```

---

## Creating the Excel Test Data File

Create `src/test/resources/testdata/LoginTestData.xlsx` with two sheets:

### Sheet 1: `LoginData`
| username     | password     | expectedResult | testCase   |
|-------------|-------------|----------------|------------|
| Admin        | admin123     | pass           | TC_XL_001  |
| Admin        | wrongPass    | fail           | TC_XL_002  |
| invalidUser  | admin123     | fail           | TC_XL_003  |

### Sheet 2: `KeywordData`
| testCase   | keyword   | locatorType | locatorValue          | testData                |
|-----------|-----------|-------------|----------------------|------------------------|
| TC_KD_001 | openUrl   |             |                      | https://...login        |
| TC_KD_001 | enterText | name        | username             | Admin                   |
| TC_KD_001 | enterText | name        | password             | admin123                |
| TC_KD_001 | click     | css         | button[type='submit']|                         |
| TC_KD_001 | assertUrl |             |                      | dashboard               |

---

## Jenkins Setup

### Required Jenkins Plugins
- **Pipeline** (built-in)
- **TestNG Results** plugin
- **HTML Publisher** plugin
- **Email Extension** plugin

### Jenkins Pipeline Configuration
1. Create a new **Pipeline** job in Jenkins
2. Point SCM to your GitHub repo
3. Script Path: `Jenkinsfile`
4. The pipeline exposes these parameters at build time:
   - **BROWSER**: chrome | firefox | chrome-headless | firefox-headless | edge
   - **SUITE**: smoke | regression
   - **ENV**: qa | staging | prod
   - **RUN_REMOTE**: false | true
   - **GRID_URL**: Selenium Grid hub URL

---

## CLI Commands Reference

```bash
# Default (chrome, smoke)
mvn test

# Specific suite
mvn test -Dsuite=smoke
mvn test -Dsuite=regression
mvn test -Dsuite=full

# Browser override
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
mvn test -Dbrowser=chrome-headless

# Environment override
mvn test -Denv=qa
mvn test -Denv=staging

# Combined
mvn test -Dsuite=regression -Dbrowser=firefox -Denv=staging -P regression

# Parallel with thread count
mvn test -Dsuite=regression -Pheadless
```

---

## Key Design Patterns Explained

### 1. Page Object Model (POM)
Each page has its own class extending `BasePage`. Locators and actions are encapsulated.
Tests never touch Selenium directly.

### 2. Data-Driven Testing
`@DataProvider` feeds test data from Excel or JSON. One test method covers multiple data sets.

### 3. Keyword-Driven Testing
Keywords like `enterText`, `click`, `assertUrl` are resolved by `KeywordExecutor`.
Non-technical stakeholders can add scenarios by editing Excel without touching Java.

### 4. ThreadLocal WebDriver
`DriverManager` stores each thread's WebDriver in a `ThreadLocal<WebDriver>`.  
This makes the framework safe for parallel execution — zero cross-thread contamination.

### 5. Fluent Page API
Page action methods return `this` or a new page object (e.g., `clickLoginButton()` returns `DashboardPage`).
Tests read like English: `new LoginPage().enterUsername("Admin").enterPassword("admin123").clickLoginButton()`.

---

## Interview Talking Points

| Question | Answer |
|----------|--------|
| Why ThreadLocal? | Ensures each parallel test thread has its own WebDriver; prevents data races |
| Why BasePage wrapper methods? | Centralises wait logic and Extent logging; no raw Selenium in tests |
| Why enums (BrowserType, WaitStrategy)? | Type safety; IDE auto-complete; eliminates typos |
| Why singleton ExtentReports? | One HTML report per run; thread-safe via synchronized |
| Why @BeforeMethod not @BeforeClass? | Fresh browser per test — no state bleed between tests |
| How do you handle flaky tests? | RetryAnalyzer + AnnotationTransformer (global retry without annotation) |
| How does Jenkins override config? | JVM -D flags override config.properties; ConfigReader checks System.getProperty first |
| How do you run headless in CI? | -Dbrowser=chrome-headless → DriverFactory adds `--headless=new` Chrome flag |

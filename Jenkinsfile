pipeline {
    agent any

    // ── Tool references (configure these names in Jenkins → Global Tool Config) ──
    tools {
        maven 'Maven-3.9'
        jdk   'JDK-11'
    }

    // ── Parameters — testers/leads can override from the Jenkins UI ──────────────
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'chrome-headless', 'firefox-headless', 'edge'],
            description: 'Target browser for test execution'
        )
        choice(
            name: 'SUITE',
            choices: ['smoke', 'regression'],
            description: 'TestNG suite to execute'
        )
        choice(
            name: 'ENV',
            choices: ['qa', 'staging', 'prod'],
            description: 'Target environment'
        )
        booleanParam(
            name: 'RUN_REMOTE',
            defaultValue: false,
            description: 'Run tests on Selenium Grid instead of locally'
        )
        string(
            name: 'GRID_URL',
            defaultValue: 'http://localhost:4444/wd/hub',
            description: 'Selenium Grid hub URL (relevant only when RUN_REMOTE=true)'
        )
    }

    environment {
        REPORT_DIR = 'test-output/ExtentReports'
        LOG_DIR    = 'test-output/logs'
        TIMESTAMP  = sh(script: 'date +%Y%m%d_%H%M%S', returnStdout: true).trim()
    }

    stages {

        // ── 1. Checkout ─────────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "🔄 Checking out source code..."
                checkout scm
            }
        }

        // ── 2. Build Verification ───────────────────────────────────────────────
        stage('Build') {
            steps {
                echo "🔨 Compiling source..."
                sh 'mvn clean compile test-compile -q'
            }
        }

        // ── 3. Test Execution ───────────────────────────────────────────────────
        stage('Run Tests') {
            steps {
                echo "🧪 Running ${params.SUITE} suite on ${params.BROWSER}..."
                sh """
                    mvn test \
                      -Dsuite=${params.SUITE} \
                      -Dbrowser=${params.BROWSER} \
                      -Denv=${params.ENV} \
                      -Drun.remote=${params.RUN_REMOTE} \
                      -Dgrid.url=${params.GRID_URL} \
                      -P${params.SUITE}
                """
            }
            post {
                failure {
                    echo "❌ Tests failed — check Extent Reports for details"
                }
            }
        }
    }

    // ── Post-execution actions ──────────────────────────────────────────────────
    post {

        always {
            echo "📊 Publishing test results..."

            // Publish TestNG results
            testNG(
                reportFilenamePattern: '**/surefire-reports/testng-results.xml',
                escapeTestDescp: false
            )

            // Archive Extent HTML reports and screenshots
            archiveArtifacts(
                artifacts: 'test-output/**/*',
                allowEmptyArchive: true,
                fingerprint: true
            )

            // Publish HTML Extent Report (requires HTML Publisher plugin)
            publishHTML(target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : "${REPORT_DIR}",
                reportFiles          : '*.html',
                reportName           : "Extent Report — ${params.SUITE} [${TIMESTAMP}]"
            ])
        }

        success {
            echo "✅ All tests PASSED! Great work!"
            emailext(
                subject: "✅ OrangeHRM ${params.SUITE} Suite PASSED — Build #${BUILD_NUMBER}",
                body: """
                    <b>Suite:</b> ${params.SUITE}<br/>
                    <b>Browser:</b> ${params.BROWSER}<br/>
                    <b>Environment:</b> ${params.ENV}<br/>
                    <b>Build:</b> <a href="${BUILD_URL}">${BUILD_URL}</a><br/>
                    <b>Report:</b> Available in build artifacts.
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }

        failure {
            echo "💥 Build FAILED — sending notification"
            emailext(
                subject: "❌ OrangeHRM ${params.SUITE} Suite FAILED — Build #${BUILD_NUMBER}",
                body: """
                    <b>Suite:</b> ${params.SUITE}<br/>
                    <b>Browser:</b> ${params.BROWSER}<br/>
                    <b>Environment:</b> ${params.ENV}<br/>
                    <b>Build:</b> <a href="${BUILD_URL}">${BUILD_URL}</a><br/>
                    <b>Logs:</b> Check the archived artifacts.
                """,
                to: '${DEFAULT_RECIPIENTS}',
                mimeType: 'text/html'
            )
        }

        cleanup {
            echo "🧹 Cleaning workspace..."
            cleanWs()
        }
    }
}

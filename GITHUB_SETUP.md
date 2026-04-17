# 🚀 Guide: Adding your OrangeHRM Project to GitHub

This guide walks you through the steps to initialize a Git repository locally and push it to GitHub.

## 1. Prerequisites
- [Git](https://git-scm.com/downloads) installed on your machine.
- A GitHub account.
- A new **Empty Repository** created on GitHub (e.g., named `OrangeHRM-Hybrid-Framework`).

---

## 2. Initialize Local Repository

Open your terminal in the project root (`e:\OrangeHRM`) and run:

```bash
# Initialize the git repository
git init

# Verify that the .gitignore is working (should see allure-results/target etc. ignored)
git status
```

---

## 3. Commit Your Files

```bash
# Add all files (respecting .gitignore)
git add .

# Create your first commit
git commit -m "Initial commit: OrangeHRM Hybrid Test Automation Framework"
```

---

## 4. Connect to GitHub

Go to your empty GitHub repository page and copy the **Remote URL**. Then run:

```bash
# Link your local repo to GitHub (Replace <URL> with your copied link)
git remote add origin <YOUR_GITHUB_REPO_URL>

# Set the main branch
git branch -M main

# Push the code to GitHub
git push -u origin main
```

---

## 5. Summary of what is Ignored
The project is already configured with a `.gitignore` file that ensures the following are **NOT** uploaded to GitHub:
- `target/` (Build artifacts)
- `test-output/` (Extent Reports)
- `allure-results/` (Raw test data)
- `allure-report/` (Generated report)
- `.idea/` & `.vscode/` (IDE settings)
- `drivers/` (If any)

---

## 6. Maintenance
Whenever you make changes, run:
```bash
git add .
git commit -m "Description of your changes"
git push
```

@echo off
echo ==========================================
echo   SecureTest - Auto GitHub Push Script
echo ==========================================
echo.

:: Check if git is installed
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Git is not installed!
    echo Please download from: https://git-scm.com/download/win
    pause
    exit /b
)

echo [1/5] Navigating to project folder...
cd /d "%~dp0"

echo [2/5] Initializing git...
git init

echo [3/5] Staging all files...
git add .

echo [4/5] Creating commit...
git commit -m "feat: SecureTest - Complete Spring Boot Backend (Auth + Exam + Proctoring + Docker)"

echo [5/5] Pushing to GitHub...
git branch -M main
git remote remove origin 2>nul
git remote add origin https://github.com/abhee2305/clg-Project.git
git push -u origin main --force

echo.
echo ==========================================
if %errorlevel% equ 0 (
    echo   SUCCESS! Code pushed to GitHub!
    echo   Check: https://github.com/abhee2305/clg-Project
) else (
    echo   ERROR: Push failed. See message above.
    echo   Most likely you need to login to GitHub.
    echo   Run: git config --global user.email "your@email.com"
    echo   Run: git config --global user.name "abhee2305"
)
echo ==========================================
pause

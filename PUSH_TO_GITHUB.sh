#!/bin/bash

echo "=========================================="
echo "  SecureTest - Auto GitHub Push Script"
echo "=========================================="
echo ""

# Check if git is installed
if ! command -v git &> /dev/null; then
    echo "ERROR: Git is not installed!"
    echo "Install it from: https://git-scm.com"
    exit 1
fi

echo "[1/5] Navigating to project folder..."
cd "$(dirname "$0")"

echo "[2/5] Initializing git..."
git init

echo "[3/5] Staging all files..."
git add .

echo "[4/5] Creating commit..."
git commit -m "feat: SecureTest - Complete Spring Boot Backend (Auth + Exam + Proctoring + Docker)"

echo "[5/5] Pushing to GitHub..."
git branch -M main
git remote remove origin 2>/dev/null
git remote add origin https://github.com/abhee2305/clg-Project.git
git push -u origin main --force

echo ""
echo "=========================================="
if [ $? -eq 0 ]; then
    echo "  SUCCESS! Code pushed to GitHub!"
    echo "  Check: https://github.com/abhee2305/clg-Project"
else
    echo "  ERROR: Push failed. See message above."
    echo "  Run these first:"
    echo "  git config --global user.email 'your@email.com'"
    echo "  git config --global user.name 'abhee2305'"
fi
echo "=========================================="

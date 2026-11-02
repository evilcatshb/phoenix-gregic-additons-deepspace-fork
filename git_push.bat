@echo off
setlocal
cd /d "%~dp0"

echo ================================================
echo Phoenix Gregic Additions - DeepSpace GitHub sync
echo ================================================

where git >nul 2>&1
if errorlevel 1 (
  echo ERROR: Git was not found in PATH.
  exit /b 1
)

git switch main
if errorlevel 1 exit /b 1

git add -A
if errorlevel 1 exit /b 1

rem GTCEu is a local build dependency and is intentionally force-added.
git add -f "libs/gtceu-1.20.1-8.0.0-SNAPSHOT+20260826-220412-18dafb2.jar"
if errorlevel 1 exit /b 1

git diff --cached --quiet
if errorlevel 1 (
  git commit -m "Sync Phoenix Gregic Additions Sky of Grind DeepSpace changes"
  if errorlevel 1 exit /b 1
) else (
  echo No local changes to commit.
)

git push -u deepspace main
if errorlevel 1 (
  echo ERROR: Push failed. Local commits are preserved and can be pushed again.
  exit /b 1
)

echo.
echo Successfully synced to https://github.com/evilcatshb/phoenix-gregic-additons-deepspace-fork
exit /b 0

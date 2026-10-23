@echo off
cd /d "%~dp0"
echo ===============================================
echo   COLLEGE SAINTE FAMILLE - APPLICATION DESKTOP
echo ===============================================
echo.
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven n'est pas disponible dans le PATH.
  echo Ouvrez simplement ce dossier comme projet Maven dans NetBeans,
  echo puis utilisez Clean and Build et Run.
  pause
  exit /b 1
)
mvn clean javafx:run
pause

@echo off

if not exist "minicraft\" mkdir minicraft\

del /S /q minicraft\*

for /f "tokens=1* delims=\" %%A in ( 'forfiles /p b /s /m * /c "cmd /c echo @relpath"'
) do for %%F in (^"%%B) do (
    if not exist b\%%~F\* (
        echo %%~F
        @REM git diff -w -u --no-prefix --output=minicraft\%%~F.patch --no-index a\%%~F b\%%~F
        diff.exe -w -u a\%%~F b\%%~F > minicraft\%%~F.patch
    ) else if not exist minicraft\%%~F\ mkdir minicraft\%%~F
)

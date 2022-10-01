@echo off

if not exist "a\" mkdir a\

del /S /q a\*

for /f "tokens=1* delims=\" %%A in ( 'forfiles /p b /s /m * /c "cmd /c echo @relpath"'
) do for %%F in (^"%%B) do (
    if not exist b\%%~F\* (
        echo %%~F
        copy ..\source\src\main\java\%%~F a\%%~F
    ) else if not exist a\%%~F\ mkdir a\%%~F
)

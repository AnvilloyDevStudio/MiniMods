@echo off

set /p shadowJar=The executable MiniMods shadow jar name=

if not exist "build\libs\%shadowJar%" (
    echo The shadow jar %shadowJar% does not exist. Returning...
    exit
)

if not exist "build\final\" mkdir build\final\

del /S /q build\final\*

xcopy /s build-files build\final
copy build\libs\%shadowJar% build\final

echo Finished copying files.

@echo off

set shadowJar="%~1"
IF NOT [%shadowJar%] == [] GOTO Skip0

set /p shadowJar=The executable MiniMods shadow jar name=
IF [%shadowJar%] == [] (
    ECHO The input is empty; returning...
    EXIT
)

:Skip0

if not exist "build\libs\%shadowJar%" (
    echo The shadow jar %shadowJar% does not exist; returning...
    exit
)

set gameJar="%~2"
IF NOT [%gameJar%] == [] GOTO Skip1

set /p gameJar=The executable Minicraft+ shadow jar name=
IF [%gameJar%] == [] (
    ECHO The input is empty; returning...
    EXIT
)

:Skip1

if not exist "minicraft\build\libs\%gameJar%" (
    echo The shadow jar %gameJar% does not exist; returning...
    exit
)

if not exist "build\final\" mkdir build\final\

del /S /q build\final\*
FOR /D %%p IN ("build\final\*.*") DO rmdir "%%p" /s /q

xcopy /s build-files build\final
copy build\libs\%shadowJar% build\final
copy minicraft\build\libs\%gameJar% build\final

if not exist "build\final\lib\" mkdir build\final\lib\
xcopy /s build\libs\lib build\final\lib

echo Finished copying files.

@echo off
setlocal enabledelayedexpansion
SET "arg="
for %%x in (%*) do (
    SET "arg=!arg!%%x "
)
CALL "%cd%/minicraft/Elevate" exit
%cd%/gradlew %arg%

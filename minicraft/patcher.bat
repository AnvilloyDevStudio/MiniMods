@echo off
REM Reference: https://superuser.com/a/981202
cmd /min /C "set __COMPAT_LAYER=RUNASINVOKER && start /b patch -l -p0 --forward -i %1 %2 "

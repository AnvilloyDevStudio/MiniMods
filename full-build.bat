call "gradlew.bat" :build
call "gradlew.bat" :mod-core-ores:build
call "gradlew.bat" :mod-core-redstone:build
call "gradlew.bat" :mod-debug-test:build
call "gradlew.bat" :minicraft-base:build
call "final-build.bat" minimods-2.0.7-0.4.0.jar minicraft_plus-2.0.7.jar

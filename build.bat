@echo OFF
mkdir \build
cd src
dir /s /B *.java > ..\sources.txt
javac -d ..\build @..\sources.txt
@DEL ..\sources.txt
cd ..\build
jar -cvfm Project.jar ..\manifest.txt *.class collection\*.class thermometer\*.class
@echo ON
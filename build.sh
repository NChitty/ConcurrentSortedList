mkdir /build
cd src
find -name "*.java" > ../sources.txt
javac -d ../build @../sources.txt
rm ../sources.txt
mkdir /build
cd ../build
jar -cvfm Project.jar ../manifest.txt *.class collection/*.class thermometer/*.class

call mvn clean -Dstyle.color=never install -T 4 -am -DskipTests --projects MicroStreamTester
move /Y MicroStreamTester\target\microStreamTester-1.0.jar MicroStreamTester\microStreamTester.jar
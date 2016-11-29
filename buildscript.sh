./gradlew clean
./gradlew build
scp -P20022 build/libs/server-1.0.jar lhruser@server:/home/lhruser

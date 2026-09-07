This project needs the MySQL Connector/J JDBC driver JAR here, named
mysql-connector-j.jar (or update the classpath/build config to match
whatever filename you download).

This sandbox has no internet access, so the real .jar could not be
downloaded and bundled into this zip. To get it:

1. Download "MySQL Connector/J" (a platform-independent .jar, ~2-2.5 MB)
   from: https://dev.mysql.com/downloads/connector/j/
   (choose "Platform Independent", download the ZIP/TAR, and pull out
   the .jar file inside), OR get it from Maven Central:
   https://mvnrepository.com/artifact/com.mysql/mysql-connector-j

2. Place the downloaded .jar file in this lib/ folder and rename it to
   mysql-connector-j.jar (or keep its name and update SmartHire.iml /
   your IDE's module dependencies to point at it).

3. Add it to your project's build path / classpath:
   - IntelliJ IDEA: File > Project Structure > Modules > Dependencies >
     "+" > JARs or Directories > select lib/mysql-connector-j.jar
   - Eclipse: right-click project > Build Path > Configure Build Path >
     Libraries > Add JARs > select lib/mysql-connector-j.jar
   - Command line: javac -cp "lib/mysql-connector-j.jar" -d out $(find src -name "*.java")
                    java  -cp "out:lib/mysql-connector-j.jar" Main   (use ; instead of : on Windows)

@echo off

java -Dfile.encoding=UTF-8 ^
     -XX:TieredStopAtLevel=1 ^
     -Dspring.main.lazy-initialization=true ^
     -Dspring.main.web-application-type=servlet ^
     -Dlogging.level.root=INFO ^
     -jar groovy-script-executor-1.0.0-RELEASE.jar ^
     %*

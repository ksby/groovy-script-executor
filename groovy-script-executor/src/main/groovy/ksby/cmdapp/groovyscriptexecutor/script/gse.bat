@echo off

java -Dfile.encoding=UTF-8 ^
     -XX:TieredStopAtLevel=1 ^
     -Dspring.main.lazy-initialization=true ^
     -jar groovy-script-executor-1.0.0-RELEASE.jar ^
     %*

@rem     -Dlogging.file.name=%~n1.log ^

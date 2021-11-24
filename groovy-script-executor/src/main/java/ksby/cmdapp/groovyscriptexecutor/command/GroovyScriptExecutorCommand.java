package ksby.cmdapp.groovyscriptexecutor.command;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Slf4j
@Component
@Command(name = "groovy-script-executor", mixinStandardHelpOptions = true,
        versionProvider = GroovyScriptExecutorCommand.class,
        description = "Groovyスクリプトを実行するコマンド")
public class GroovyScriptExecutorCommand
        implements Callable<Integer>, IExitCodeExceptionMapper, IVersionProvider {

    @Autowired
    private BuildProperties buildProperties;

    @Parameters(index = "0", paramLabel = "Groovyスクリプト",
            description = "実行する Groovyスクリプトを指定する")
    private File groovyScript;

    @Parameters(index = "1..*", paramLabel = "引数",
            description = "Groovyスクリプトに渡す引数を指定する")
    private String[] args;

    @Unmatched
    private String[] unmatched;

    @Override
    public Integer call() throws IOException {
        try {
            Binding binding = new Binding();
            GroovyShell shell = new GroovyShell(binding);
            shell.run(groovyScript, ArrayUtils.addAll(args, unmatched));
        } catch (Exception e) {
            log.error("Groovyスクリプトでエラーが発生しました。", e);
        }

        return ExitCode.OK;
    }

    @Override
    public int getExitCode(Throwable exception) {
        if (exception instanceof Exception) {
            return 1;
        }

        return ExitCode.OK;
    }

    @Override
    public String[] getVersion() {
        return new String[]{buildProperties.getVersion()};
    }

}

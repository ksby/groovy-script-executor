package ksby.cmdapp.groovyscriptexecutor;

import ksby.cmdapp.groovyscriptexecutor.command.GroovyScriptExecutorCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

import static picocli.CommandLine.IFactory;

@SpringBootApplication
public class Application implements CommandLineRunner, ExitCodeGenerator {

    private int exitCode;

    private final GroovyScriptExecutorCommand groovyScriptExecutorCommand;

    private final IFactory factory;

    public Application(GroovyScriptExecutorCommand groovyScriptExecutorCommand,
                       IFactory factory) {
        this.groovyScriptExecutorCommand = groovyScriptExecutorCommand;
        this.factory = factory;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Application.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        exitCode = new CommandLine(groovyScriptExecutorCommand, factory)
                .setExitCodeExceptionMapper(groovyScriptExecutorCommand)
                .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

}

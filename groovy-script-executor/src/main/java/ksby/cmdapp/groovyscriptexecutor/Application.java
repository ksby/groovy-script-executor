package ksby.cmdapp.groovyscriptexecutor;

import ksby.cmdapp.groovyscriptexecutor.command.GroovyScriptExecutorCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
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
        String springMainWebApplicationType = System.getProperty("spring.main.web-application-type");
        ApplicationContext context = SpringApplication.run(Application.class, args);
        if (StringUtils.equals(springMainWebApplicationType, "none")) {
            System.exit(SpringApplication.exit(context));
        }
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

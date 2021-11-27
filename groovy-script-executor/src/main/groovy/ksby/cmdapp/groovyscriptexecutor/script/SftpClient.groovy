package ksby.cmdapp.groovyscriptexecutor.script

import groovy.util.logging.Slf4j
import ksby.cmdapp.groovyscriptexecutor.script.helper.sftp.SftpHelper
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate
import org.springframework.stereotype.Component
import picocli.CommandLine
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Command
import picocli.CommandLine.ExitCode
import picocli.CommandLine.IExitCodeExceptionMapper
import picocli.CommandLine.IFactory
import picocli.CommandLine.Option

import java.util.concurrent.Callable

@Slf4j
@SpringBootApplication
class SftpClient implements CommandLineRunner, ExitCodeGenerator {

    private int exitCode

    private final SftpClientCommand sftpClientCommand

    private final IFactory factory

    SftpClient(SftpClientCommand sftpClientCommand, IFactory factory) {
        this.sftpClientCommand = sftpClientCommand
        this.factory = factory
    }

    static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SftpClient.class, args)));
    }

    @Override
    void run(String... args) throws Exception {
        exitCode = new CommandLine(sftpClientCommand, factory)
                .setExitCodeExceptionMapper(sftpClientCommand)
                .execute(args)
    }

    @Override
    int getExitCode() {
        return exitCode
    }

    @Component
    @Command(name = "SftpClient",
            mixinStandardHelpOptions = true,
            description = "SFTPサーバにファイルをアップロード・ダウンロードするコマンド")
    static class SftpClientCommand
            implements Callable<Integer>, IExitCodeExceptionMapper {

        @Option(names = "--host", required = false, description = "ホスト名")
        String host = "localhost"

        @Option(names = "--port", required = false, description = "ポート番号")
        int port = 22

        @Option(names = ["-u", "--user"], description = "ユーザ名")
        String user

        @Option(names = ["-p", "--password"], description = "パスワード")
        String password

        @ArgGroup(exclusive = true, multiplicity = "1")
        SftpClientOperation sftpClientOperation

        static class SftpClientOperation {

            @ArgGroup(exclusive = false, multiplicity = "1")
            UploadOption uploadOption

            @ArgGroup(exclusive = false, multiplicity = "1")
            DownloadOption downloadOption

        }

        static class UploadOption {

            @Option(names = "--upload-dir", required = true, description = "アップロード先ディレクトリ")
            String uploadDir

            @Option(names = "--upload-file", required = true, description = "アップロードするファイル")
            File uploadFile

        }

        static class DownloadOption {

            @Option(names = "--download-src", required = true, description = "ダウンロード元ファイル")
            String downlaodSrc

            @Option(names = "--download-dst", required = true, description = "ダウンロード先ファイル")
            String downloadDst

        }

        private final SftpHelper sftpHelper

        SftpClientCommand(SftpHelper sftpHelper) {
            this.sftpHelper = sftpHelper
        }

        @Override
        Integer call() throws Exception {
            SftpRemoteFileTemplate sftpRemoteFileTemplate =
                    sftpHelper.createSftpRemoteFileTemplate(host, port, user, password)

            if (sftpClientOperation.uploadOption != null) {
                log.info("{} へ {} をアップロードします",
                        sftpClientOperation.uploadOption.uploadDir,
                        sftpClientOperation.uploadOption.uploadFile)
                sftpHelper.upload(sftpRemoteFileTemplate,
                        sftpClientOperation.uploadOption.uploadDir,
                        sftpClientOperation.uploadOption.uploadFile)
            } else {
                log.info("{} を {} へダウンロードします",
                        sftpClientOperation.downloadOption.downlaodSrc,
                        sftpClientOperation.downloadOption.downloadDst)
                sftpHelper.download(sftpRemoteFileTemplate,
                        sftpClientOperation.downloadOption.downlaodSrc,
                        sftpClientOperation.downloadOption.downloadDst)
            }

            return ExitCode.OK
        }

        @Override
        int getExitCode(Throwable exception) {
            if (exception instanceof RuntimeException) {
                return 101
            }

            return ExitCode.OK
        }
    }

}

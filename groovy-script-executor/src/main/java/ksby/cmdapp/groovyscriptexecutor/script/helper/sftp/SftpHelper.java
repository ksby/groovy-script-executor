package ksby.cmdapp.groovyscriptexecutor.script.helper.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.file.remote.ClientCallbackWithoutResult;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SftpHelper {

    public SftpRemoteFileTemplate createSftpRemoteFileTemplate(
            String host,
            int port,
            String user,
            String password) {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(false);
        factory.setHost(host);
        factory.setPort(port);
        factory.setUser(user);
        factory.setPassword(password);
        factory.setAllowUnknownKeys(true);

        return new SftpRemoteFileTemplate(factory);
    }

    public void upload(SftpRemoteFileTemplate sftpRemoteFileTemplate,
                       String uploadDir, File uploadFile) {
        sftpRemoteFileTemplate.setRemoteDirectoryExpression(new LiteralExpression(uploadDir));
        Message<File> message = MessageBuilder.withPayload(uploadFile).build();
        sftpRemoteFileTemplate.send(message, FileExistsMode.REPLACE);
    }

    public void download(SftpRemoteFileTemplate sftpRemoteFileTemplate,
                         String downlaodSrc, String downloadDst) {
        sftpRemoteFileTemplate.executeWithClient(
                (ClientCallbackWithoutResult<ChannelSftp>) client -> {
                    try {
                        client.get(downlaodSrc, downloadDst);
                    } catch (SftpException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}

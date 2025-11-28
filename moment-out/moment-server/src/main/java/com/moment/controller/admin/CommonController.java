package com.moment.controller.admin;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.moment.constant.MessageConstant;
import com.moment.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
@Slf4j
public class CommonController {
    /*
    * 文件上传
    * */
    @Value("${moment.storage.public-url}")
    private String storagePublicUrl;

    @Value("${moment.storage.sftp.host}")
    private String sftpHost;

    @Value("${moment.storage.sftp.port:22}")
    private Integer sftpPort;

    @Value("${moment.storage.sftp.username}")
    private String sftpUsername;

    @Value("${moment.storage.sftp.password}")
    private String sftpPassword;

    @Value("${moment.storage.sftp.remote-dir}")
    private String sftpRemoteDir;

    @PostMapping("/upload")
    @Operation(summary = "文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);

        Session session = null;
        ChannelSftp sftp = null;
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = UUID.randomUUID().toString() + extension;

            JSch jsch = new JSch();
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort == null ? 22 : sftpPort);
            session.setPassword(sftpPassword);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            ensureRemoteDirectory(sftp, sftpRemoteDir);
            try (InputStream inputStream = file.getInputStream()) {
                String targetDir = normalizeRemoteDir(sftpRemoteDir);
                sftp.put(inputStream, targetDir + objectName, ChannelSftp.OVERWRITE);
            }
            String baseUrl = storagePublicUrl.endsWith("/") ? storagePublicUrl : storagePublicUrl + "/";
            return Result.success(baseUrl + objectName);
        } catch (IOException | JSchException | SftpException e) {
            log.error("文件上传失败", e);
        } finally {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

    private void ensureRemoteDirectory(ChannelSftp sftp, String remoteDir) throws SftpException {
        String normalized = normalizeRemoteDir(remoteDir);
        String[] folders = normalized.split("/");
        StringBuilder path = new StringBuilder();
        if (normalized.startsWith("/")) {
            sftp.cd("/");
        }
        for (String folder : folders) {
            if (folder == null || folder.trim().isEmpty()) {
                continue;
            }
            path.append(folder).append('/');
            try {
                sftp.cd(folder);
            } catch (SftpException e) {
                sftp.mkdir(folder);
                sftp.cd(folder);
            }
        }
    }

    private String normalizeRemoteDir(String remoteDir) {
        if (remoteDir == null || remoteDir.isEmpty()) {
            return "/";
        }
        return remoteDir.endsWith("/") ? remoteDir : remoteDir + "/";
    }
}

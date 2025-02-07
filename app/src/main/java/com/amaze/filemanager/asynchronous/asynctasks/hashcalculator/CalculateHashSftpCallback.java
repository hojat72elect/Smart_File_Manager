package com.amaze.filemanager.asynchronous.asynctasks.hashcalculator;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.amaze.filemanager.filesystem.HybridFileParcelable;
import com.amaze.filemanager.filesystem.ftp.NetCopyClientUtils;
import com.amaze.filemanager.filesystem.ssh.SshClientSessionTemplate;
import com.amaze.filemanager.filesystem.ssh.SshClientUtils;

import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;

public class CalculateHashSftpCallback implements Callable<Hash> {
    private final HybridFileParcelable file;

    public CalculateHashSftpCallback(HybridFileParcelable file) {
        if (!file.isSftp()) {
            throw new IllegalArgumentException("Use CalculateHashCallback");
        }

        this.file = file;
    }

    @WorkerThread
    @Override
    public Hash call() throws Exception {
        String md5Command = "md5sum -b \"%s\" | cut -c -32";
        String shaCommand = "sha256sum -b \"%s\" | cut -c -64";

        String md5 = SshClientUtils.execute(getHash(md5Command));
        String sha256 = SshClientUtils.execute(getHash(shaCommand));

        Objects.requireNonNull(md5);
        Objects.requireNonNull(sha256);

        return new Hash(md5, sha256);
    }

    private SshClientSessionTemplate<String> getHash(String command) {
        return new SshClientSessionTemplate<>(file.getPath()) {
            @Override
            public String execute(@NonNull Session session) throws IOException {
                String path = NetCopyClientUtils.extractRemotePathFrom(file.getPath());
                String fullCommand = String.format(command, path);
                Session.Command cmd = session.exec(fullCommand);
                String result = IOUtils.readFully(cmd.getInputStream()).toString();
                cmd.close();
                if (cmd.getExitStatus() == 0) {
                    return result;
                } else {
                    return null;
                }
            }
        };
    }
}

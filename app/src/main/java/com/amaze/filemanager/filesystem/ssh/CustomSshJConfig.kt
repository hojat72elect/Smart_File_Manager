package com.amaze.filemanager.filesystem.ssh

import net.schmizz.sshj.DefaultConfig
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

/**
 * sshj [net.schmizz.sshj.Config] for our own use.
 *
 *
 * Borrowed from original AndroidConfig, but also use vanilla BouncyCastle from the start
 * altogether.
 *
 * @see net.schmizz.sshj.Config
 *
 * @see net.schmizz.sshj.AndroidConfig
 */
class CustomSshJConfig : DefaultConfig() {
    companion object {
        /**
         * This is where we different from the original AndroidConfig. Found it only work if we remove
         * BouncyCastle bundled with Android before registering our BouncyCastle provider
         */
        @JvmStatic
        fun init() {
            Security.removeProvider("BC")
            Security.insertProviderAt(BouncyCastleProvider(), 0)
        }
    }
}

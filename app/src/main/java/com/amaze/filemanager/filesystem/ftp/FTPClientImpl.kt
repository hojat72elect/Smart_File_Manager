package com.amaze.filemanager.filesystem.ftp

import org.apache.commons.net.ftp.FTPClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.random.Random

class FTPClientImpl(private val ftpClient: FTPClient) : NetCopyClient<FTPClient> {
    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(FTPClientImpl::class.java)

        const val ANONYMOUS = "anonymous"

        const val ARG_TLS = "tls"

        const val TLS_EXPLICIT = "explicit"

        private const val ALPHABET = "abcdefghijklmnopqrstuvwxyz1234567890"

        @JvmStatic
        private fun randomString(strlen: Int) =
            (1..strlen)
                .map { Random.nextInt(0, ALPHABET.length) }
                .map(ALPHABET::get)
                .joinToString("")

        /**
         * Generate random email address for anonymous FTP login.
         */
        @JvmStatic
        fun generateRandomEmailAddressForLogin(
            usernameLen: Int = 8,
            domainPrefixLen: Int = 5,
            domainSuffixLen: Int = 3,
        ): String {
            val username = randomString(usernameLen)
            val domainPrefix = randomString(domainPrefixLen)
            val domainSuffix = randomString(domainSuffixLen)

            return "$username@$domainPrefix.$domainSuffix"
        }

        /**
         * Wraps an an temporary [File] returned by [FTPClient.retrieveFileStream].
         * Most important part is to do [File.delete] when the reading is done.
         */
        @JvmStatic
        fun wrap(inputFile: File) =
            object : InputStream() {
                private val inputStream = FileInputStream(inputFile)

                override fun read() = inputStream.read()

                override fun read(b: ByteArray?): Int = inputStream.read(b)

                override fun read(
                    b: ByteArray?,
                    off: Int,
                    len: Int,
                ): Int = inputStream.read(b, off, len)

                override fun reset() = inputStream.reset()

                override fun available(): Int = inputStream.available()

                override fun close() {
                    inputStream.close()
                    inputFile.delete()
                }

                override fun markSupported(): Boolean = inputStream.markSupported()

                override fun mark(readlimit: Int) = inputStream.mark(readlimit)

                override fun skip(n: Long): Long = inputStream.skip(n)
            }

        /**
         * Wraps an [OutputStream] returned by [FTPClient.storeFileStream].
         * Most important part is to do [FTPClient.completePendingCommand] on [OutputStream.close].
         */
        @JvmStatic
        fun wrap(
            outputStream: OutputStream,
            ftpClient: FTPClient,
        ) = object : OutputStream() {
            override fun write(b: Int) = outputStream.write(b)

            override fun write(b: ByteArray?) = outputStream.write(b)

            override fun write(
                b: ByteArray?,
                off: Int,
                len: Int,
            ) = outputStream.write(b, off, len)

            override fun flush() = outputStream.flush()

            override fun close() {
                outputStream.close()
                ftpClient.completePendingCommand()
            }
        }
    }

    override fun getClientImpl() = ftpClient

    override fun isConnectionValid(): Boolean = ftpClient.isAvailable

    override fun isRequireThreadSafety(): Boolean = true

    override fun expire() {
        ftpClient.disconnect()
    }
}

package com.amaze.filemanager.play.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.amaze.filemanager.application.AmazeFileManagerApplication
import com.amaze.filemanager.filesystem.files.FileUtils
import java.io.File

/**
 * Additional checks for package validity for installation via Amaze.
 *
 */
object PackageInstallValidation {
    /**
     * Perform validation by getting [PackageInfo] of file at specified path, then see if it's us.
     * If yes, throw [PackageCannotBeInstalledException] and [FileUtils.installApk] should exit
     * with Toast.
     */
    @JvmStatic
    @Throws(PackageCannotBeInstalledException::class, IllegalStateException::class)
    fun validatePackageInstallability(f: File) {
        AmazeFileManagerApplication.getInstance().run {
            val packageInfo: PackageInfo? =
                packageManager.getPackageArchiveInfo(
                    f.absolutePath,
                    PackageManager.GET_ACTIVITIES,
                )
            if (packageInfo == null) {
                throw IllegalStateException("Cannot get package info")
            } else {
                if (packageInfo.packageName == this.packageName) {
                    throw PackageCannotBeInstalledException(
                        "Cannot update myself per Google Play policy",
                    )
                }
            }
        }
    }

    /**
     * Exception indicating specified package cannot be installed
     */
    class PackageCannotBeInstalledException(reason: String) : Exception(reason)
}

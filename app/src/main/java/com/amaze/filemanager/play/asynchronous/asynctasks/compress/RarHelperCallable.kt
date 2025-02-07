package com.amaze.filemanager.play.asynchronous.asynctasks.compress

import com.amaze.filemanager.adapters.data.CompressedObjectParcelable
import com.amaze.filemanager.asynchronous.asynctasks.compress.CompressedHelperCallable
import com.amaze.filemanager.filesystem.compressed.CompressedHelper
import com.amaze.filemanager.play.filesystem.compressed.showcontents.helpers.RarDecompressor.Companion.convertName
import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import com.github.junrar.exception.UnsupportedRarV5Exception
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import org.apache.commons.compress.archivers.ArchiveException

/**
 * AsyncTask to load RAR file items.
 *
 * @param fileLocation the location of the zip file
 * @param relativeDirectory relativeDirectory to access inside the zip file
 */
class RarHelperCallable(
    private val fileLocation: String,
    private val relativeDirectory: String,
    goBack: Boolean,
) :
    CompressedHelperCallable(goBack) {
    @Throws(ArchiveException::class)
    override fun addElements(elements: ArrayList<CompressedObjectParcelable>) {
        try {
            val rarFile = Archive(File(fileLocation))
            val relativeDirDiffSeparator =
                relativeDirectory.replace(
                    CompressedHelper.SEPARATOR,
                    "\\",
                )
            for (rarArchive in rarFile.fileHeaders) {
                val name = rarArchive.fileName
                if (!CompressedHelper.isEntryPathValid(name)) {
                    continue
                }
                val isInBaseDir = (
                        (relativeDirDiffSeparator == "") &&
                                !name.contains("\\")
                        )
                val isInRelativeDir = name.contains("\\") && (name.substring(0, name.lastIndexOf("\\")) == relativeDirDiffSeparator)
                if (isInBaseDir || isInRelativeDir) {
                    elements.add(
                        CompressedObjectParcelable(
                            convertName(rarArchive),
                            rarArchive.mTime.time,
                            rarArchive.fullUnpackSize,
                            rarArchive.isDirectory,
                        ),
                    )
                }
            }
        } catch (e: UnsupportedRarV5Exception) {
            throw ArchiveException("RAR v5 archives are not supported", e)
        } catch (e: FileNotFoundException) {
            throw ArchiveException("First part of multipart archive not found", e)
        } catch (e: RarException) {
            throw ArchiveException(String.format("RAR archive %s is corrupt", fileLocation))
        } catch (e: IOException) {
            throw ArchiveException(String.format("RAR archive %s is corrupt", fileLocation))
        }
    }
}

package com.amaze.filemanager.ui.fragments.preferencefragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceManager
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.activities.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader

class BackupPrefsFragment : BasePrefsFragment() {

    private val log: Logger = LoggerFactory.getLogger(BackupPrefsFragment::class.java)

    companion object {
        private const val IMPORT_BACKUP_FILE = 2
        private const val TAG = "BasePrefsFragment"
    }

    override val title = R.string.backup

    /** Export app settings to a JSON file */
    private fun exportPrefs() {
        val map: Map<String?, *> =
            PreferenceManager
                .getDefaultSharedPreferences(requireActivity()).all

        val gsonString: String = Gson().toJson(map)

        try {
            val file = File(context?.cacheDir?.absolutePath + File.separator + "amaze_backup.json")

            val fileWriter = FileWriter(file)

            fileWriter.append(gsonString)

            Log.i(TAG, "wrote export to: ${file.absolutePath}")

            fileWriter.flush()
            fileWriter.close()

            Toast.makeText(
                context,
                getString(R.string.select_save_location),
                Toast.LENGTH_SHORT,
            ).show()

            val intent = Intent(context, MainActivity::class.java)

            intent.action = Intent.ACTION_SEND
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))

            startActivity(intent)
        } catch (e: IOException) {
            Toast.makeText(context, getString(R.string.exporting_failed), Toast.LENGTH_SHORT).show()
            log.error(getString(R.string.exporting_failed), e)
        }
    }


    /**
     * Import app settings from a JSON file
     */
    private fun importPrefs() {

        startActivityForResult(
            Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("*/*")
                .putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf("application/json"),
                ),
            IMPORT_BACKUP_FILE,
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        val nonNull = data != null && data.data != null

        if (requestCode == IMPORT_BACKUP_FILE &&
            resultCode == Activity.RESULT_OK &&
            nonNull
        ) {
            val uri = data!!.data

            Log.i(TAG, "read import file: $uri")

            try {
                val inputStream =
                    uri?.let {
                        context?.contentResolver?.openInputStream(it)
                    }

                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()

                var line: String?
                while (bufferedReader.readLine().also { line = it } != null)
                    stringBuilder.append(line).append('\n')

                val type = object : TypeToken<Map<String?, Any>>() {}.type

                val map: Map<String?, Any> =
                    Gson().fromJson(
                        stringBuilder.toString(),
                        type,
                    )

                val editor: SharedPreferences.Editor? =
                    PreferenceManager.getDefaultSharedPreferences(requireActivity()).edit()

                for ((key, value) in map)
                    storePreference(editor, key, value)

                editor?.apply()

                Toast.makeText(
                    context,
                    getString(R.string.importing_completed),
                    Toast.LENGTH_SHORT,
                ).show()

                startActivity(
                    Intent(
                        context,
                        MainActivity::class.java,
                    ),
                ) // restart Amaze for changes to take effect
            } catch (e: IOException) {
                Toast.makeText(
                    context,
                    getString(R.string.importing_failed),
                    Toast.LENGTH_SHORT,
                ).show()
                log.error(getString(R.string.importing_failed), e)
            }
        } else {
            Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun storePreference(
        editor: SharedPreferences.Editor?,
        key: String?,
        value: Any,
    ) {
        try {
            when (value::class.simpleName) {
                "Boolean" -> editor?.putBoolean(key, value as Boolean)
                "Float" -> editor?.putFloat(key, value as Float)
                "Int" -> editor?.putInt(key, value as Int)
                "Long" -> editor?.putLong(key, value as Long)
                "String" -> editor?.putString(key, value.toString())
                "Set<*>" -> editor?.putStringSet(key, value as Set<String>)
            }
        } catch (e: java.lang.ClassCastException) {
            Toast.makeText(
                context,
                "${getString(R.string.import_failed_for)} $key",
                Toast.LENGTH_SHORT,
            ).show()
            log.error("${getString(R.string.import_failed_for)} $key", e)
        }
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.backup_prefs, rootKey)

        findPreference<Preference>(
            PreferencesConstants.PREFERENCE_EXPORT_SETTINGS,
        )?.onPreferenceClickListener =
            OnPreferenceClickListener {
                exportPrefs()
                true
            }

        findPreference<Preference>(
            PreferencesConstants.PREFERENCE_IMPORT_SETTINGS,
        )?.onPreferenceClickListener =
            OnPreferenceClickListener {
                importPrefs()
                true
            }
    }
}

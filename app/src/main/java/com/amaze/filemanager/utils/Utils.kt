package com.amaze.filemanager.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import android.os.Handler
import android.os.storage.StorageVolume
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.amaze.filemanager.BuildConfig
import com.amaze.filemanager.R
import com.amaze.filemanager.adapters.data.LayoutElementParcelable
import com.amaze.filemanager.fileoperations.filesystem.OpenMode
import com.amaze.filemanager.filesystem.HybridFileParcelable
import com.amaze.filemanager.ui.activities.MainActivity
import com.amaze.filemanager.ui.theme.AppTheme
import com.amaze.filemanager.utils.OTGUtil.getDocumentFile
import com.google.android.material.snackbar.Snackbar
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Contains useful functions and methods (NOTHING HERE DEALS WITH FILES).
 */
object Utils {

    private const val INPUT_INTENT_BLACKLIST_PIPE = "\\|"
    private const val INPUT_INTENT_BLACKLIST_AMP = "&&"
    private const val INPUT_INTENT_BLACKLIST_DOTS = "\\.\\.\\."
    private const val INPUT_INTENT_BLACKLIST_COLON = ";"
    const val EMAIL_SUPPORT = "support@teamamaze.xyz"
    private const val URL_TELEGRAM = "https://t.me/hojat72elect"

    private val log: Logger = LoggerFactory.getLogger(Utils::class.java)
    private var isToastShowing = false
    private const val INDEX_NOT_FOUND = -1


    private const val EMAIL_EMMANUEL = "emmanuelbendavid@gmail.com"
    private const val EMAIL_RAYMOND = "airwave209gt@gmail.com"
    private const val EMAIL_VISHNU = "t.v.s10123@gmail.com"
    private const val EMAIL_VISHAL = "vishalmeham2@gmail.com"

    private const val DATE_TIME_FORMAT = "%s | %s"

    // methods for fastscroller
    @JvmStatic
    fun clamp(min: Float, max: Float, value: Float): Float {
        assert(min < max)
        return if (value < min) min else if (value > max) max else value
    }

    /**
     * Zoom in or out of a view.
     */
    @JvmStatic
    fun zoom(scaleX: Float, scaleY: Float, pivot: PointF, view: View) {
        view.pivotX = pivot.x
        view.pivotY = pivot.y
        view.scaleX = scaleX
        view.scaleY = scaleY
    }

    @JvmStatic
    fun getViewRawY(view: View): Float {
        val location = IntArray(2)
        location[0] = 0
        location[1] = view.y.toInt()
        (view.parent as View).getLocationInWindow(location)
        return location[1].toFloat()
    }

    /**
     * @param c : The context of the application calling this function.
     * @param f : Input date in form of milliseconds.
     * @return : The string equivalent of date.
     */
    @JvmStatic
    fun getDate(c: Context, f: Long): String {
        return String.format(
            DATE_TIME_FORMAT,
            DateUtils.formatDateTime(c, f, DateUtils.FORMAT_ABBREV_MONTH),
            DateUtils.formatDateTime(c, f, DateUtils.FORMAT_SHOW_TIME)
        )
    }

    /**
     * Returns an approximate equivalent of input DP as PX.
     */
    @JvmStatic
    fun dpToPx(c: Context, dp: Int): Int {
        val displayMetrics = c.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    /**
     * Extract the color from a resource ID.
     */
    @JvmStatic
    fun getColor(c: Context?, @ColorRes color: Int) = c!!.getColor(color)

    @JvmStatic
    fun hideKeyboard(mainActivity: MainActivity) {

        // get the view that currently has the focus (it could be null)
        val view = mainActivity.currentFocus
        if (view != null) {
            (mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Open our telegram in device's browser or Telegram app.
     */
    @JvmStatic
    fun openTelegramURL(context: Context) {
        openURL(URL_TELEGRAM, context)
    }

    /**
     * Open a url in the browser.
     */
    @JvmStatic
    fun openURL(url: String, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))

        val packageManager = context.packageManager
        val webViews = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        if (webViews.isNotEmpty()) {
            context.startActivity(intent)
        } else {
            log.warn("A browser is not available")
            if (!isToastShowing) {
                isToastShowing = true
                Toast.makeText(context, R.string.not_found_enabled_webview, Toast.LENGTH_SHORT).show()
                // Prevents a myriad of duplicates
                Handler().postDelayed({ isToastShowing = false }, 2200)
            }
        }
    }

    /**
     * Checks if the input String is null or empty.
     */
    @JvmStatic
    fun isNullOrEmpty(string: String?): Boolean {
        return string.isNullOrEmpty()
    }

    /**
     * Gets position of nth to last char in String. nthToLastCharIndex(1, "a.tar.gz") = 1
     * nthToLastCharIndex(0, "a.tar.gz") = 5
     */
    @JvmStatic
    fun nthToLastCharIndex(elementNumber: Int, str: String, element: Char): Int {
        require(elementNumber > 0)

        var occurencies = 0
        for (i in str.length - 1 downTo 0) {
            if (str[i] == element && ++occurencies == elementNumber) {
                return i
            }
        }
        return -1
    }

    /**
     * Formats input to plain mm:ss format
     *
     * @param timerInSeconds duration in seconds
     * @return time in mm:ss format
     */
    @JvmStatic
    fun formatTimer(timerInSeconds: Long): String {
        val min = TimeUnit.SECONDS.toMinutes(timerInSeconds)
        val sec = TimeUnit.SECONDS.toSeconds(timerInSeconds - TimeUnit.MINUTES.toSeconds(min))
        return String.format("%02d:%02d", min, sec)
    }

    @JvmStatic
    fun isNullOrEmpty(list: Collection<*>?): Boolean {
        return list == null || list.isEmpty()
    }

    fun addShortcut(
        context: Context, componentName: ComponentName, path: LayoutElementParcelable
    ) {
        // Adding shortcut for MainActivity
        // on Home screen

        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            Toast.makeText(
                context,
                context.getString(R.string.add_shortcut_not_supported_by_launcher),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        val shortcutIntent = Intent(context, MainActivity::class.java)
        shortcutIntent.putExtra("path", path.desc)
        shortcutIntent.setAction(Intent.ACTION_MAIN)
        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        // Using file path as shortcut id.
        val info =
            ShortcutInfoCompat.Builder(context, path.desc)
                .setActivity(componentName)
                .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_launcher))
                .setIntent(shortcutIntent)
                .setLongLabel(path.title)
                .setShortLabel(path.title)
                .build()

        ShortcutManagerCompat.requestPinShortcut(context, info, null)
    }

    @JvmStatic
    fun getVolumeDirectory(volume: StorageVolume): File {
        try {
            val f = StorageVolume::class.java.getDeclaredField("mPath")
            f.isAccessible = true
            return f[volume] as File
        } catch (e: Exception) {
            log.error(e.message)
            // This shouldn't fail, as mPath has been there in every version
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun showThemedSnackbar(
        mainActivity: MainActivity,
        text: CharSequence,
        length: Int,
        @StringRes actionTextId: Int,
        actionCallback: Runnable
    ): Snackbar {
        val snackbar =
            Snackbar.make(mainActivity.findViewById(R.id.content_frame), text, length)
                .setAction(actionTextId) { _: View? -> actionCallback.run() }
        if (mainActivity.getAppTheme() == AppTheme.LIGHT) {
            snackbar
                .view
                .setBackgroundColor(mainActivity.resources.getColor(android.R.color.white))
            snackbar.setTextColor(mainActivity.resources.getColor(android.R.color.black))
        }
        snackbar.show()
        return snackbar
    }

    private fun indexOfDifferenceStrings(cs1: CharSequence?, cs2: CharSequence?): Int {
        if (cs1 === cs2) return INDEX_NOT_FOUND
        if (cs1 == null || cs2 == null) return 0
        var i = 0
        while (i < cs1.length && i < cs2.length) {
            if (cs1[i] != cs2[i]) break
            ++i
        }

        if (i < cs2.length || i < cs1.length) return i

        return INDEX_NOT_FOUND
    }

    /**
     * Compares two Strings, and returns the portion where they differ. (More precisely, return the
     * remainder of the second String, starting from where it's different from the first.)
     *
     *
     * For example, difference("i am a machine", "i am a robot") -> "robot".
     *
     *
     * StringUtils.difference(null, null) = null StringUtils.difference("", "") = ""
     * StringUtils.difference("", "abc") = "abc" StringUtils.difference("abc", "") = ""
     * StringUtils.difference("abc", "abc") = "" StringUtils.difference("ab", "abxyz") = "xyz"
     * StringUtils.difference("abcde", "abxyz") = "xyz" StringUtils.difference("abcde", "xyz") = "xyz"
     *
     * @param str1 - the first String, may be null.
     * @param str2 - the second String, may be null.
     * @return the portion of str2 where it differs from str1; returns an empty String if they are
     * equal. Returns null if both of the input strings are null.
     */
    @JvmStatic
    fun differenceStrings(str1: String?, str2: String?): String? {
        if (str1 == null) return str2
        if (str2 == null) return str1

        val at = indexOfDifferenceStrings(str1, str2)

        if (at == INDEX_NOT_FOUND) return ""

        return str2.substring(at)
    }

    @JvmStatic
    fun sanitizeInputOnce(input: String): String {
        return input
            .replace(INPUT_INTENT_BLACKLIST_PIPE.toRegex(), "")
            .replace(INPUT_INTENT_BLACKLIST_AMP.toRegex(), "")
            .replace(INPUT_INTENT_BLACKLIST_DOTS.toRegex(), "")
            .replace(INPUT_INTENT_BLACKLIST_COLON.toRegex(), "")
    }

    /**
     * Builds a email intent for amaze feedback.
     *
     * @param text        email content
     * @param supportMail support mail for given intent
     * @return intent
     */
    fun buildEmailIntent(context: Context, text: String?, supportMail: String): Intent {
        val emailIntent = Intent(Intent.ACTION_SEND)
        val aEmailList = arrayOf(supportMail)
        val aEmailCCList = arrayOf(EMAIL_VISHAL, EMAIL_EMMANUEL, EMAIL_RAYMOND, EMAIL_VISHNU)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList)
        emailIntent.putExtra(Intent.EXTRA_CC, aEmailCCList)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback : Amaze File Manager for " + BuildConfig.VERSION_NAME)
        val logUri =
            FileProvider.getUriForFile(
                context,
                context.packageName,
                File(String.format("/data/data/%s/cache/logs.txt", context.packageName))
            )
        emailIntent.putExtra(Intent.EXTRA_STREAM, logUri)
        if (!isNullOrEmpty(text)) {
            emailIntent.putExtra(Intent.EXTRA_TEXT, text)
        }
        emailIntent.setType("message/rfc822")
        return emailIntent
    }

    @JvmStatic
    fun enableScreenRotation(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    @SuppressLint("RestrictedApi")
    @JvmStatic
    fun showCutCopySnackBar(
        mainActivity: MainActivity,
        text: CharSequence,
        length: Int,
        @StringRes actionTextId: Int,
        actionCallback: Runnable,
        cancelCallback: Runnable
    ): Snackbar {

        val snackbar = Snackbar.make(mainActivity.findViewById(R.id.content_frame), "", length)

        val customSnackView = View.inflate(mainActivity.applicationContext, R.layout.snackbar_view, null)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)

        val snackBarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackBarLayout.setPadding(0, 0, 0, 0)

        val actionButton = customSnackView.findViewById<Button>(R.id.snackBarActionButton)
        val cancelButton = customSnackView.findViewById<Button>(R.id.snackBarCancelButton)
        val textView = customSnackView.findViewById<AppCompatTextView>(R.id.snackBarTextTV)

        actionButton.setText(actionTextId)
        textView.text = text

        actionButton.setOnClickListener { actionCallback.run() }
        cancelButton.setOnClickListener { cancelCallback.run() }

        snackBarLayout.addView(customSnackView, 0)

        snackBarLayout.findViewById<CardView>(R.id.snackBarCardView).setCardBackgroundColor(mainActivity.accent)

        snackbar.show()
        return snackbar
    }

    /**
     * Force disables screen rotation. Useful when we're temporarily in activity because of external
     * intent, and don't have to really deal much with filesystem.
     */
    @JvmStatic
    fun disableScreenRotation(activity: Activity) {
        val screenOrientation = activity.resources.configuration.orientation

        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * Sanitizes input from external application to avoid any attempt of command injection
     */
    @JvmStatic
    fun sanitizeInput(input: String): String {
        // iterate through input and keep sanitizing until it's fully injection proof
        var sanitizedInput: String
        var sanitizedInputTemp = input

        while (true) {
            sanitizedInput = sanitizeInputOnce(sanitizedInputTemp)
            if (sanitizedInput == sanitizedInputTemp) break
            sanitizedInputTemp = sanitizedInput
        }

        return sanitizedInput
    }

    /**
     * Returns uri associated to specific basefile
     */
    @JvmStatic
    fun getUriForBaseFile(
        context: Context, baseFile: HybridFileParcelable
    ): Uri? {
        when (baseFile.mode) {
            OpenMode.FILE,
            OpenMode.ROOT -> return FileProvider.getUriForFile(context, context.packageName, File(baseFile.path))

            OpenMode.OTG -> return getDocumentFile(baseFile.path, context, true)?.uri

            OpenMode.SMB,
            OpenMode.DROPBOX,
            OpenMode.GDRIVE,
            OpenMode.ONEDRIVE,
            OpenMode.BOX -> {
                Toast.makeText(context, context.getString(R.string.smb_launch_error), Toast.LENGTH_LONG).show()
                return null
            }

            else -> return null
        }
    }
}
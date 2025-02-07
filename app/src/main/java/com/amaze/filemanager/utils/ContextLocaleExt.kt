package com.amaze.filemanager.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.amaze.filemanager.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.Locale

/**
 * [Context] extension to return app's available locales, from locales_config.xml.
 */
fun Context.getLocaleListFromXml(): LocaleListCompat {
    val tagsList = mutableListOf<CharSequence>()
    try {
        val xpp: XmlPullParser = resources.getXml(R.xml.locales_config)
        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
            if (xpp.eventType == XmlPullParser.START_TAG) {
                if (xpp.name == "locale") {
                    tagsList.add(xpp.getAttributeValue(0))
                }
            }
            xpp.next()
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    // Remove locale tags that would produce same locale on Android N or above
    tagsList.remove("id")
    tagsList.remove("he")


    return LocaleListCompat.forLanguageTags(tagsList.joinToString(","))
}

/**
 * [Context] extension to return a [Map] of [Locale] with its display name as key.
 *
 * For preference drop down convenience.
 */
fun Context.getLangPreferenceDropdownEntries(): Map<String, Locale> {
    val localeList = getLocaleListFromXml()
    val currentLocaleList: List<Locale> =
        (
                if (!AppCompatDelegate.getApplicationLocales().isEmpty) {
                    AppCompatDelegate.getApplicationLocales()
                } else {
                    LocaleListCompat.getDefault()
                }
                ).let { appLocales ->
                ArrayList<Locale>().apply {
                    for (x in 0 until appLocales.size()) {
                        appLocales.get(x)?.let {
                            this.add(it)
                        }
                    }
                }
            }
    val map = mutableMapOf<String, Locale>()

    for (a in 0 until localeList.size()) {
        localeList[a].let {
            it?.run {
                val displayName: String =
                    if (currentLocaleList.isEmpty()) {
                        this.getDisplayName(Locale.getDefault())
                    } else {
                        this.getDisplayName(
                            currentLocaleList.first { locale ->
                                this.getDisplayName(locale).isNotEmpty()
                            },
                        )
                    }
                map.put(displayName, this)
            }
        }
    }
    return map
}


package io.github.cloudburst.spotifyex.patches

import android.content.ClipData
import android.net.Uri
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge


fun cleanTrackingUrls() {
    XposedBridge.hookMethod(Uri.Builder::class.java.getDeclaredMethod("appendQueryParameter", String::class.java, String::class.java), object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val key = param.args[0] as String
            if ("si" == key || key.startsWith("utm_")) param.result = param.thisObject
        }
    })
    XposedBridge.hookMethod(ClipData::class.java.getDeclaredMethod("newPlainText", CharSequence::class.java, CharSequence::class.java), object : XC_MethodHook() {
        @Throws(Throwable::class)
        override fun beforeHookedMethod(param: MethodHookParam) {
            val text = param.args[1] as String
            if (text.startsWith("https://open")) {
                param.args[1] = text.replaceFirst("\\?(si|utm_.+?)=.+".toRegex(), "")
            }
        }
    })
}
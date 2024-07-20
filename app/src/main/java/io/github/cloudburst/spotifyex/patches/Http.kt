package io.github.cloudburst.spotifyex.patches

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

private val bannedPrefixes = arrayOf(
    "https://spclient.wg.spotify.com/ad-logic/prefetch",
    "https://spclient.wg.spotify.com/ads/v3/ads?slots=",
)

fun blockRequests(cl: ClassLoader) {
    val httpconnectionImpl = cl.loadClass("com.spotify.core.http.NativeHttpConnection")
    val httpRequest = cl.loadClass("com.spotify.core.http.HttpRequest")
    val urlField = httpRequest.getDeclaredField("url")
    urlField.isAccessible = true
    XposedBridge.hookAllMethods(httpconnectionImpl, "send", object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val req = param.args[0]
            val url = urlField.get(req) as String
            if (bannedPrefixes.any { url.startsWith(it) })
                param.result = null
        }
    })
}
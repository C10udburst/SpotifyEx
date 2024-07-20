package io.github.cloudburst.spotifyex.patches

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

private val flagPatches = mapOf(
    "ads" to false,
    "on-demand" to true,
    "shuffle_restricted" to false,
    "nft-disabled" to true,
    "premium-tab-lock" to 1,
    "type" to "premium"
)

fun patchFlags(cl: ClassLoader) {
    val flags = cl.loadClass("com.spotify.connectivity.flags.LoadedFlags")
    XposedBridge.hookAllMethods(flags, "get", object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            val key = XposedHelpers.getObjectField(param!!.args[0], "identifier") as String
            if (!flagPatches.containsKey(key)) {
                Log.d(TAG, "Unknown flag: $key = ${param.result}")
            }
        }
        override fun beforeHookedMethod(param: MethodHookParam?) {
            val key = XposedHelpers.getObjectField(param!!.args[0], "identifier") as String
            if (flagPatches.containsKey(key)) {
                param.result = flagPatches[key]
            }
        }
    })
}
package io.github.cloudburst.spotifyex.patches

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge

fun patchProduct(cl: ClassLoader) {
    val productClass = cl.loadClass("com.spotify.connectivity.productstate.ProductStateUtil")
    XposedBridge.hookAllMethods(productClass, "isPremium", object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.result = true
        }
    })
    XposedBridge.hookAllMethods(productClass, "isShuffleRestricted", object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.result = false
        }
    })
    XposedBridge.hookAllMethods(productClass, "isOnDemandTrialActivate", object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            param.result = true
        }
    })
}
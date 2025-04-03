package io.github.cloudburst.spotifyex.patches

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import org.luckypray.dexkit.DexKitBridge

val contextMenuMap = mapOf(
    "hide_playlist_radio" to false,
    "premium_upsell_panel_enabled" to false,
    "remove_ads_upsell_enabled" to false,

    "enable_premium_banner" to false,
    "enable_contiguous_viewability_observer_for_in_stream_ads" to false,
    "enable_contiguous_viewability_observer_for_on_surface_ads" to false,
    "video_ads_caching_enabled" to false,
)

fun patchContextMenu(bridge: DexKitBridge, cl: ClassLoader) {
    val booleanFlag = bridge.findClass {
        matcher {
            usingStrings(
                "gated-podcast-upsell-flow",
                "DisableAllEnabledShows"
            )
        }
    }.findMethod {
        matcher {
            returnType = "boolean"
            paramTypes("java.lang.String", "java.lang.String", "boolean")
        }
    }.first()

    XposedBridge.hookAllMethods(booleanFlag.getClassInstance(cl), booleanFlag.name, object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val override = contextMenuMap[param.args[1] as? String] ?: return
            param.result = override
        }
    })
}
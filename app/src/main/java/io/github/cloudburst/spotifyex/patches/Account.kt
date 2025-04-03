package io.github.cloudburst.spotifyex.patches

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType
import org.luckypray.dexkit.query.matchers.base.StringMatcher

private val flagPatches = mapOf<Any, Any>(
    // Disables player and app ads.
    "ads" to false,
    // Works along on-demand, allows playing any song without restriction.
    "player-license" to "premium",
    // Disables shuffle being initially enabled when first playing a playlist.
    "shuffle" to false,
    // Allows playing any song on-demand, without a shuffled order.
    "on-demand" to true,
    // Make sure playing songs is not disabled remotely and playlists show up.
    "streaming" to true,
    // Allows adding songs to queue and removes the smart shuffle mode restriction,
    // allowing to pick any of the other modes.
    "pick-and-shuffle" to false,
    // Disables shuffle-mode streaming-rule, which forces songs to be played shuffled
    // and breaks the player when other patches are applied.
    "streaming-rules" to "",
    // Enables premium UI in settings and removes the premium button in the nav-bar.
    "nft-disabled" to "1",
    // Enable Spotify Car Thing hardware device.
    // Device is discontinued and no longer works with the latest releases,
    // but it might still work with older app targets.
    "can_use_superbird" to true,
    // Removes the premium button in the nav-bar for tablet users.
    "tablet-free" to false
)

fun patchAccount(bridge: DexKitBridge, cl: ClassLoader) {
    val accountAttr = bridge.findClass {
        matcher {
            className(StringMatcher.create(
                value = "internal.AccountAttribute",
                matchType = StringMatchType.EndsWith
            ))
        }
    }
    val valueField = accountAttr.findField {
        matcher {
            name(StringMatcher.create(
                value = "value_",
                matchType = StringMatchType.StartsWith
            ))
        }
    }
    val productStateClass = bridge.findClass {
        matcher {
            className(StringMatcher.create(
                value = "ProductStateProto",
                matchType = StringMatchType.EndsWith
            ))
        }
    }
    val getMethod = productStateClass.findMethod {
        matcher {
            returnType(Map::class.java)
        }
    }

    val accountAttrClass = accountAttr.first().getInstance(cl)
    val valueFieldInstance = accountAttrClass.getDeclaredField(valueField.first().name)
    valueFieldInstance.isAccessible = true

    XposedBridge.hookAllMethods(productStateClass.first().getInstance(cl), getMethod.first().name, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            val oldValue = param!!.result as? Map<*, *> ?: return
            for ((key, value) in oldValue) {
                try {
                    val override = flagPatches[key] ?: continue
                    valueFieldInstance.set(value, override)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to set value for key $key: ${e.message}")
                }
            }
        }
    })
}
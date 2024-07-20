package io.github.cloudburst.spotifyex;

import static io.github.cloudburst.spotifyex.patches.FlagsKt.*;
import static io.github.cloudburst.spotifyex.patches.HttpKt.*;
import static io.github.cloudburst.spotifyex.patches.ProductKt.*;
import static io.github.cloudburst.spotifyex.patches.UrlsKt.*;

import android.text.format.DateFormat;
import android.util.Log;
import org.luckypray.dexkit.DexKitBridge;

import java.util.Date;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public final class Module implements IXposedHookLoadPackage {

    private static final String TAG = "SpotifyEx";

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.spotify.music")) return;

        Log.d(TAG, "Module build date: " + DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date(BuildConfig.BUILD_DATE)));

        var cl = lpparam.classLoader;
        System.loadLibrary("dexkit");
        try (DexKitBridge bridge = DexKitBridge.create(lpparam.appInfo.sourceDir)) {
            if (bridge == null) {
                Log.e(TAG, "Failed to create DexKitBridge");
                return;
            }
            patchFlags(cl);
            blockRequests(cl);
            patchProduct(cl);
            cleanTrackingUrls();
        } catch (Exception e) {
            Log.e(TAG, "Failed to find method", e);
        }
    }

}

package io.github.cloudburst.spotifyex;

import android.text.format.DateFormat;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.luckypray.dexkit.DexKitBridge;

import java.util.Date;

import static io.github.cloudburst.spotifyex.patches.AccountKt.patchAccount;
import static io.github.cloudburst.spotifyex.patches.ContextMenuKt.patchContextMenu;
import static io.github.cloudburst.spotifyex.patches.HttpKt.blockRequests;
import static io.github.cloudburst.spotifyex.patches.UrlsKt.cleanTrackingUrls;


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
            //try { patchFlags(cl); } catch (Exception e) { Log.e(TAG, "Failed to patch flags", e); }
            try { blockRequests(cl); } catch (Exception e) { Log.e(TAG, "Failed to block requests", e); }
            //try { patchProduct(cl); } catch (Exception e) { Log.e(TAG, "Failed to patch product", e); }
            try { patchAccount(bridge, cl); } catch (Exception e) { Log.e(TAG, "Failed to patch account", e); }
            try { patchContextMenu(bridge, cl); } catch (Exception e) { Log.e(TAG, "Failed to patch context menu", e); }
            try { cleanTrackingUrls(); } catch (Exception e) { Log.e(TAG, "Failed to clean tracking URLs", e); }
        } catch (Exception e) {
            Log.e(TAG, "Failed to find method", e);
        }
    }

}

package org.mozilla.cachedrequestloader;

import android.util.Log;

public class Inject {

    static void sleepIfTesting(boolean shouldSleep) {
        Log.d("BOBO", "main sleepIfTesting() called with: shouldSleep = [" + shouldSleep +
                "]");
        // Do nothing on non-test flavors
    }

    static void postDelayIfTesting(Runnable runnable, boolean shouldDelay) {

        runnable.run();
        Log.d("BOBO", "main postDelayIfTesting() called with: runnable = [" + runnable + "]," +
                " " +
                "shouldDelay = [" + shouldDelay + "]");
    }
}

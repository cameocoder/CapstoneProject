package com.cameocoder.capstoneproject.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WasteSyncService extends Service {
    public final String LOG_TAG = WasteSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static WasteSyncAdapter wasteSyncAdapter = null;

    public WasteSyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (wasteSyncAdapter == null) {
                wasteSyncAdapter = new WasteSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return wasteSyncAdapter.getSyncAdapterBinder();
    }
}

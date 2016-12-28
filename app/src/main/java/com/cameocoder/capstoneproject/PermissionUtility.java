package com.cameocoder.capstoneproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PermissionUtility {

    public static boolean hasLocationPermission(Context context) {
        return !lacksPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean lacksPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lacksPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }
}

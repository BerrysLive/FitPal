package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.health.connect.client.HealthConnectClient;
import androidx.health.connect.client.permission.HealthPermission;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.StepsRecord;
import java.util.HashSet;
import java.util.Set;

public class PermissionsRationaleActivity extends Activity {

    // Declare the PERMISSIONS set
    private Set<HealthPermission> PERMISSIONS;

    // Constructor or an initialization block
    public void PermissionsRationaleActivity() {
        PERMISSIONS = new HashSet<>();
        PERMISSIONS.add(HealthPermission.getReadPermission(HeartRateRecord.class));
        PERMISSIONS.add(HealthPermission.getWritePermission(HeartRateRecord.class));
        PERMISSIONS.add(HealthPermission.getReadPermission(StepsRecord.class));
        PERMISSIONS.add(HealthPermission.getWritePermission(StepsRecord.class));
    }

    public static void checkHealthConnectAvailability(Context context, String providerPackageName) {
        int availabilityStatus = HealthConnectClient.getSdkStatus(context, providerPackageName);

        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            // Early return as there is no viable integration
            return;
        }

        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            // Optionally redirect to package installer to find a provider, for example:
            String uriString = "market://details?id=" + providerPackageName + "&url=healthconnect%3A%2F%2Fonboarding";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.android.vending");
            intent.setData(Uri.parse(uriString));
            intent.putExtra("overlay", true);
            intent.putExtra("callerId", context.getPackageName());
            context.startActivity(intent);
            return;
        }

        HealthConnectClient healthConnectClient = HealthConnectClient.getOrCreate(context);
        // Issue operations with healthConnectClient
    }
}

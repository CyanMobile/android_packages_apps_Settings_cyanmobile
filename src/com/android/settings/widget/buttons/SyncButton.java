
package com.android.settings.widget.buttons;

import com.android.settings.R;
import com.android.settings.widget.SettingsAppWidgetProvider;
import com.android.settings.widget.WidgetSettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

public class SyncButton extends WidgetButton {

    private static final SyncButton ownButton = new SyncButton();

    /**
     * Toggle auto-sync
     *
     * @param context
     */
    public void toggleState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean backgroundData = getBackgroundDataState(context);
        boolean sync = ContentResolver.getMasterSyncAutomatically();

        // four cases to handle:
        // setting toggled from off to on:
        // 1. background data was off, sync was off: turn on both
        if (!backgroundData && !sync) {
            connManager.setBackgroundDataSetting(true);
            ContentResolver.setMasterSyncAutomatically(true);
        }

        // 2. background data was off, sync was on: turn on background data
        if (!backgroundData && sync) {
            connManager.setBackgroundDataSetting(true);
        }

        // 3. background data was on, sync was off: turn on sync
        if (backgroundData && !sync) {
            ContentResolver.setMasterSyncAutomatically(true);
        }

        // setting toggled from on to off:
        // 4. background data was on, sync was on: turn off sync
        if (backgroundData && sync) {
            ContentResolver.setMasterSyncAutomatically(false);
        }
    }

    public void toggleState(Context context, int newState) {
        if (getSync(context) && newState == SettingsAppWidgetProvider.STATE_DISABLED) {
            toggleState(context);
        } else if (!getSync(context) && newState == SettingsAppWidgetProvider.STATE_ENABLED) {
            toggleState(context);
        }
    }

    /**
     * Gets the state of background data.
     *
     * @param context
     * @return true if enabled
     */
    private static boolean getBackgroundDataState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getBackgroundDataSetting();
    }

    /**
     * Gets the state of auto-sync.
     *
     * @param context
     * @return true if enabled
     */
    private static boolean getSync(Context context) {
        boolean backgroundData = getBackgroundDataState(context);
        boolean sync = ContentResolver.getMasterSyncAutomatically();
        return backgroundData && sync;
    }

    public void updateState(Context context, SharedPreferences globalPreferences, int[] appWidgetIds) {
        if (getSync(context)) {
            currentIcon = R.drawable.ic_appwidget_settings_sync_on;
            currentState = SettingsAppWidgetProvider.STATE_ENABLED;
        } else {
            currentIcon = R.drawable.ic_appwidget_settings_sync_off;
            currentState = SettingsAppWidgetProvider.STATE_DISABLED;
        }
    }

    public static SyncButton getInstance() {
        return ownButton;
    }

    @Override
    void initButton() {
        buttonID = WidgetButton.BUTTON_SYNC;
        preferenceName = WidgetSettings.TOGGLE_SYNC;
    }

}

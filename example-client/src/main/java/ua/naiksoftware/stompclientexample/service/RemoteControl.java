package ua.naiksoftware.stompclientexample.service;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import ua.naiksoftware.stompclientexample.R;

/**
 * Created by hieu19926@gmail.com on 12/04/2020.
 */
public class RemoteControl {
    public static final Byte TURN_OFF_SCREEN = 1;

    private RemoteControl(){}
    public static void powerController(Byte command, Activity context) {
        if (TURN_OFF_SCREEN.equals(command)) {
            // turn off the screen
            turnScreenOffAndExit(context);

        }
    }

    //Hieu: turn off screen functions
    private static void turnScreenOffAndExit(Activity activity) {
        // first lock screen
        turnScreenOff(activity);
        // then provide feedback
        Vibrator vibrator = ((Vibrator) activity.getApplication().getSystemService(Context.VIBRATOR_SERVICE));
        if (vibrator != null) {
            vibrator.vibrate(50);
        }

    }

    /**
     * Turns the screen off and locks the device, provided that proper rights
     * are given.
     *
     * @param context
     *            - The application context
     */
    /**
     * Turns the screen off and locks the device, provided that proper rights
     * are given.
     *
     * @param context
     *            - The application context
     */
    private static void turnScreenOff(final Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                DeviceAdminDemo.class);
        if (policyManager != null) {
            boolean admin = policyManager.isAdminActive(adminReceiver);
            if (admin) {
                Log.i("TurnScreenOff", "Going to sleep now.");
                policyManager.lockNow();
            } else {
                Log.i("TurnScreenOff", "Not an admin");
                Toast.makeText(context, R.string.device_admin_not_enabled,
                        Toast.LENGTH_LONG).show();
                // open admin permission grant
                context.startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
            }
        }
    }
}

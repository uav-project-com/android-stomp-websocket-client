package ua.naiksoftware.stompclientexample.service;

/**
 * Created by hieu19926@gmail.com on 12/04/2020.
 */

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
public class DeviceAdminDemo extends DeviceAdminReceiver {
    //	implement onEnabled(), onDisabled(),
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onEnabled(Context context, Intent intent) {}

    public void onDisabled(Context context, Intent intent) {}
}
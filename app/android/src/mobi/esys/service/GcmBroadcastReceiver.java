package mobi.esys.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("log", "in");
		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}

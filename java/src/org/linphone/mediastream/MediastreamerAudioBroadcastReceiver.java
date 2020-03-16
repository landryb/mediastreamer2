package org.linphone.mediastream;

import android.media.AudioManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class MediastreamerAudioBroadcastReceiver extends BroadcastReceiver {
	public MediastreamerAudioBroadcastReceiver() {
		super();
		Log.i("MediastreamerAudioBroadcastReceiver created");
	}

	public IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
		intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
		intentFilter.addAction(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT);
		intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);



		return intentFilter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("DEBUG onReceive: intent=" + intent);

		String action = intent.getAction();

		Log.i("DEBUG action=" + action);

		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			int currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.ERROR);
			Log.i("DEBUG Bueltooth adapter state changed current extra state: " + currentState + " previous extra state " + previousState);

			if (
				// ON to OFF transition
				(((previousState == BluetoothAdapter.STATE_ON) || (previousState == BluetoothAdapter.STATE_TURNING_ON)) && ((currentState == BluetoothAdapter.STATE_OFF) || (currentState == BluetoothAdapter.STATE_TURNING_OFF)))
				||
				// OFF to ON transition
				(((previousState == BluetoothAdapter.STATE_OFF) || (previousState == BluetoothAdapter.STATE_TURNING_OFF)) && ((currentState == BluetoothAdapter.STATE_ON) || (currentState == BluetoothAdapter.STATE_TURNING_ON)))
			) {
				Log.i("Bluetooth adapter detected a state change - recomputing device ID");
			}

		} else if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
			int currentState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
			int previousState = intent.getIntExtra(BluetoothHeadset.EXTRA_PREVIOUS_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
			Log.i("DEBUG Bluetooth adapter connection state changed current extra state: " + currentState + " previous extra state " + previousState);
			if (
				// ON to OFF transition
				(((previousState == BluetoothHeadset.STATE_CONNECTED) || (previousState == BluetoothHeadset.STATE_CONNECTING)) && ((currentState == BluetoothHeadset.STATE_DISCONNECTED) || (currentState == BluetoothHeadset.STATE_DISCONNECTING)))
				||
				// OFF to ON transition
				(((previousState == BluetoothHeadset.STATE_DISCONNECTED) || (previousState == BluetoothHeadset.STATE_DISCONNECTING)) && ((currentState == BluetoothHeadset.STATE_CONNECTED) || (currentState == BluetoothHeadset.STATE_CONNECTING)))
			) {
				Log.i("Bluetooth headset detected a connection state change - recomputing device ID");
			}
		} else if (action.equals(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)) {
			int currentState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);
			int previousState = intent.getIntExtra(BluetoothHeadset.EXTRA_PREVIOUS_STATE, BluetoothHeadset.STATE_DISCONNECTED);
			Log.i("DEBUG Bueltooth adapter state changed current extra state: " + currentState + " previous extra state " + previousState);
			if (
				// ON to OFF transition
				((previousState == BluetoothHeadset.STATE_AUDIO_CONNECTED) && (currentState == BluetoothHeadset.STATE_AUDIO_DISCONNECTED))
				||
				// OFF to ON transition
				((previousState == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) && (currentState == BluetoothHeadset.STATE_AUDIO_CONNECTED))
			) {
				Log.i("Bluetooth headset detected an audio state change - recomputing device ID");
			}
		} else if (action.equals(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT)) {
			String cmd = intent.getStringExtra(BluetoothHeadset.EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD);
			String args = intent.getStringExtra(BluetoothHeadset.EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_ARGS);
			int type = intent.getIntExtra(BluetoothHeadset.EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD_TYPE, -1);
			Log.i("DEBUG vendor specific command:  type " + type + " cmd " + cmd + " args " + args);
		} else if (action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
			int currentState = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, AudioManager.SCO_AUDIO_STATE_DISCONNECTED);
			int previousState = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_PREVIOUS_STATE, AudioManager.SCO_AUDIO_STATE_DISCONNECTED);
			Log.i("DEBUG Audio Manager current state: " + currentState + " previous state " + previousState);
			if (
				// ON to OFF transition
				(((previousState == AudioManager.SCO_AUDIO_STATE_CONNECTING) || (previousState == AudioManager.SCO_AUDIO_STATE_CONNECTED)) && (currentState == AudioManager.SCO_AUDIO_STATE_DISCONNECTED))
				||
				// OFF to ON transition
				((previousState == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) && ((currentState == AudioManager.SCO_AUDIO_STATE_CONNECTED) || (currentState == AudioManager.SCO_AUDIO_STATE_CONNECTING)))
			) {
				Log.i("Audio manager detected an audio state change - recomputing device ID");
			}
		} else {
		    Log.w("[Mediastreamer Broadcast Receiver] Bluetooth unknown action: " + action);
		}

	}
}


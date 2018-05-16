package de.androidnewcomer.weginsbuero;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by uwe on 23.03.15.
 */
public class WearSteuerungService extends WearableListenerService {

    private static final String LOG_TAG = "WearableListener";

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        String id = peer.getId();
        String name = peer.getDisplayName();

        Log.d(LOG_TAG, "Connected peer name & ID: " + name + "|" + id);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        String message = messageEvent.getPath();
        Log.d(LOG_TAG, "received a message from wear: " + message);
        if("start".equalsIgnoreCase(message)) {
            startService(new Intent(this, WegAufzeichnungService.class));
        }
        if("stop".equalsIgnoreCase(message)) {
            startService(new Intent(this, WegAufzeichnungService.class));
        }
    }


}

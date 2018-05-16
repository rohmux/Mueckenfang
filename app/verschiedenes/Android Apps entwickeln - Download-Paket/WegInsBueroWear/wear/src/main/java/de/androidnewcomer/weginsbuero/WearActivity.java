package de.androidnewcomer.weginsbuero;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class WearActivity extends Activity implements View.OnClickListener {


    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                stub.findViewById(R.id.start).setOnClickListener(WearActivity.this);
                stub.findViewById(R.id.stop).setOnClickListener(WearActivity.this);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.start) {
            sendMessageToHandheld("start");
        }
        if(view.getId()==R.id.stop) {
            sendMessageToHandheld("stop");
        }
    }


    private void sendMessageToHandheld(final String message) {

        if (mGoogleApiClient == null)
            return;

        Log.d("wear", "sending a message to handheld: " + message);

        // use the api client to send the heartbeat value to our handheld
        final PendingResult<NodeApi.GetConnectedNodesResult> result = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        result.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                final List<Node> nodes = result.getNodes();
                if (nodes != null) {
                    for (int i = 0; i < nodes.size(); i++) {
                        final Node node = nodes.get(i);
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), message, null);
                    }
                }
            }
        });

    }
}

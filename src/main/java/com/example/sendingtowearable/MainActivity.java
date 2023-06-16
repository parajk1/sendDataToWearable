package com.example.sendingtowearable;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import android.widget.TextView;
import com.google.android.gms.wearable.Node;
import android.util.Log;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;






public class MainActivity extends ComponentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient = null;
    public static final String TAG = "MyDataMAP.....";
    public static final String WEARABLE_DATA_PATH = "/wearable/data/path";

    @Override
    protected void onCreate(Bundle savedInstanceStance) {
        super.onCreate(savedInstanceStance);
        setContentView(R.layout.activity_main);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient = builder.build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        sendMessage();
    }


    public void sendMessage() {
        if (googleApiClient.isConnected()) {
            String message = ((TextView) findViewById(R.id.text)).getText().toString();
            if (message == null || message.equalsIgnoreCase("")) {
                message = "Hello World";
            }
            new SendMessageToDataLayer(googleApiClient, WEARABLE_DATA_PATH, message).start();
        } else {
            // Handle the case when GoogleApiClient is not connected
        }
    }



    public class SendMessageToDataLayer extends Thread {
        private static final String TAG = "SendMessageToDataLayer";
        private String path;
        private String message;
        private GoogleApiClient googleApiClient;

        public SendMessageToDataLayer(GoogleApiClient googleApiClient, String path, String message) {
            this.googleApiClient = googleApiClient;
            this.path = path;
            this.message = message;
        }

        @Override
        public void run() {
            NodeApi.GetConnectedNodesResult nodesList = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            for (Node node : nodesList.getNodes()) {
                MessageApi.SendMessageResult messageResult = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, message.getBytes()).await();
                if (messageResult.getStatus().isSuccess()) {
                    // Print success log
                    Log.v(TAG, "Message: successfully sent to " + node.getDisplayName());
                    Log.v(TAG, "Message: Node Id is " + node.getId());
                    Log.v(TAG, "Message: Node size is " + nodesList.getNodes().size());
                } else {
                    // Print fail log
                    Log.v(TAG, "Message: Error while sending message");
                }
            }
        }
    }







    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }
} //end of MainActivity

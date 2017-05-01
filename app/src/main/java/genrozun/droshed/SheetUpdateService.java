package genrozun.droshed;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static android.R.attr.password;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * WIP : working on update/receive logic...
 */
public class SheetUpdateService extends IntentService {
    private static final String ACTION_RECEIVE_UPDATE = "genrozun.droshed.action.RECEIVE_UPDATE";
    private static final String ACTION_SEND_UPDATE = "genrozun.droshed.action.SEND_UPDATE";

    private static final String CURRENT_CLIENT_VERSION = "genrozun.droshed.extra.CURRENT_CLIENT_VERSION";


    private LocalBroadcastManager broadcastManager;

    public SheetUpdateService() {
        super("SheetUpdateService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startReceiveUpdate(Context context, int lastClientVersion) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_RECEIVE_UPDATE);
        intent.putExtra(CURRENT_CLIENT_VERSION, lastClientVersion);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startSendUpdate(Context context) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_SEND_UPDATE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("SERVICE", "STARTED NEW WORK");
        if (intent != null) {
            broadcastManager = LocalBroadcastManager.getInstance(this);
            final String action = intent.getAction();
            if (ACTION_RECEIVE_UPDATE.equals(action)) {
                final int currentClientVersion = intent.getIntExtra(CURRENT_CLIENT_VERSION, 1);
                handleActionReceiveUpdate(currentClientVersion);
            } else if (ACTION_SEND_UPDATE.equals(action)) {
                handleActionSendUpdate();
            }
        }
    }

    /**
     Ask the Service to receive updates (=update the local storage)
     */
    private void handleActionReceiveUpdate(int currentClientVersion) {
        //1. Ask last version
        Log.i("SERVICE", "Asking last server version");
        int lastServerVersion = askServerLastVersion();
        Log.i("SERVICE", "Last version is "+lastServerVersion);

        while(currentClientVersion < lastServerVersion) {
            //2. Update for each missing version, and broadcast the update
            currentClientVersion++;

            /* Ici récupérer une version et la stocker */

            Intent intent = new Intent("droshed-sync");
            intent.putExtra("last_version", currentClientVersion);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private int askServerLastVersion() {
        String answer = executeLastVersionRequest();
        return Integer.valueOf(answer);
    }

    public static String executeLastVersionRequest()
    {
        URL url = null;
        try {
            url = new URL("http://192.168.1.24:8765/model1/data/lastversion");
        } catch (MalformedURLException e) {
            //do nothing
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            // Read answer
            Log.i("SERVICE", urlConnection.getResponseMessage());
            Log.i("SERVICE", "Length: "+urlConnection.getHeaderField("Content-Length"));

            byte[] tmp = new byte[Integer.valueOf(urlConnection.getHeaderField("Content-Length"))];
            in.read(tmp);
            ByteBuffer wrapped = ByteBuffer.wrap(tmp);
            String num = Charset.forName("UTF-8").decode(wrapped).toString();

            Log.i("SERVICE", "Last version: "+num);

            return num;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendUpdate() {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

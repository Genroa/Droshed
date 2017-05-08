package genrozun.droshed;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final String ACTION_CHECK_AUTH = "genrozun.droshed.action.CHECK_AUTH";
    private static final String ACTION_RECEIVE_UPDATE = "genrozun.droshed.action.RECEIVE_UPDATE";
    private static final String ACTION_SEND_UPDATE = "genrozun.droshed.action.SEND_UPDATE";


    private static final String CURRENT_CLIENT_VERSION = "genrozun.droshed.extra.CURRENT_CLIENT_VERSION";
    private static final String MODEL_NAME = "genrozun.droshed.extra.MODEL_NAME";


    public static final String AUTH_OK = "genrozun.droshed.auth.AUTH_OK";
    public static final String AUTH_ERROR = "genrozun.droshed.auth.AUTH_ERROR";

    private LocalBroadcastManager broadcastManager;
    private static String CURRENT_SERVER_IP = "http://192.168.43.187:8765";

    public SheetUpdateService() {
        super("SheetUpdateService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startReceiveUpdate(Context context, String model) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_RECEIVE_UPDATE);
        intent.putExtra(MODEL_NAME, model);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSendUpdate(Context context) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_SEND_UPDATE);
        context.startService(intent);
    }

    public static void startCheckAuth(Context context) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_CHECK_AUTH);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("SERVICE", "STARTED NEW WORK");
        if (intent != null) {
            broadcastManager = LocalBroadcastManager.getInstance(this);
            final String action = intent.getAction();
            switch(action) {
                case ACTION_CHECK_AUTH:
                    handleActionCheckAuth();
                    break;

                case ACTION_RECEIVE_UPDATE:
                    final String model = intent.getStringExtra(MODEL_NAME);
                    handleActionReceiveUpdate(model);
                    break;

                case ACTION_SEND_UPDATE:
                    handleActionSendUpdate();
                    break;
            }
        }
    }

    /**
     Ask the Service to receive updates (=update the local storage)
     */
    private void handleActionReceiveUpdate(String model) {
        SharedPreferences sp = getSharedPreferences("droshed_model_"+model, MODE_PRIVATE);
        int currentClientVersion = sp.getInt("currentVersion", 0);

        //1. Ask last version
        Log.i("SERVICE", "Asking last server version for model "+model);
        int lastServerVersion = askServerLastVersion(model);
        Log.i("SERVICE", "Last version is "+lastServerVersion);

        while(currentClientVersion < lastServerVersion) {
            //2. Update for each missing version, and broadcast the update
            currentClientVersion++;

            /* Ici récupérer une version et la stocker */
            String newVersionData = askServerVersion(currentClientVersion);

            /* Mettre à jour les informations persistantes */
            SharedPreferences.Editor e = sp.edit();
            e.putInt("currentVersion", currentClientVersion);
            e.apply();

            Intent intent = new Intent("droshed-sync");
            intent.putExtra("last_version", currentClientVersion);
            broadcastManager.sendBroadcast(intent);

        }
    }

    private int askServerLastVersion(String model) {
        String answer = executeLastVersionRequest(model);
        return Integer.valueOf(answer);
    }

    private String askServerVersion(int version) {
        return executeReceiveVersionRequest(version);
    }

    public static String executeReceiveVersionRequest(int version)
    {
        URL url = null;
        try {
            url = new URL(CURRENT_SERVER_IP + "/model1/data/"+version);
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
            String body = Charset.forName("UTF-8").decode(wrapped).toString();

            Log.i("SERVICE", "Data: "+body);

            return body;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static String executeLastVersionRequest(String model)
    {
        URL url = null;
        try {
            url = new URL(CURRENT_SERVER_IP + "/"+model+"/data/lastversion");
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
            String body = Charset.forName("UTF-8").decode(wrapped).toString();

            Log.i("SERVICE", "Last version: "+body);

            return body;
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

    private void handleActionCheckAuth() {
        URL url = null;
        try {
            url = new URL(CURRENT_SERVER_IP + "/checkauth");
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

            Log.i("SERVICE", "Auth answer code: "+urlConnection.getResponseCode());

            Intent intent = new Intent("droshed-auth");
            if(urlConnection.getResponseCode() != 200) {
                intent.putExtra("result", AUTH_ERROR);
            } else {
                intent.putExtra("result", AUTH_OK);
            }
            broadcastManager.sendBroadcast(intent);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

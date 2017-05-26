package genrozun.droshed.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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
    private static final String ACTION_GET_NEW_MODEL = "genrozun.droshed.action.GET_NEW_MODEL";


    private static final String CURRENT_CLIENT_VERSION = "genrozun.droshed.extra.CURRENT_CLIENT_VERSION";
    private static final String MODEL_NAME = "genrozun.droshed.extra.MODEL_NAME";


    public static final String OPERATION_OK = "genrozun.droshed.auth.OPERATION_OK";
    public static final String OPERATION_ERROR = "genrozun.droshed.auth.OPERATION_ERROR";

    private LocalBroadcastManager broadcastManager;
    private static String CURRENT_SERVER_IP = "http://192.168.1.24:7777";

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
        Log.i("SERVICE", "CheckAuth task");
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_CHECK_AUTH);
        context.startService(intent);
    }

    public static void startGetNewModel(Context context, String modelName) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_GET_NEW_MODEL);
        intent.putExtra(MODEL_NAME, modelName);
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
                
                case ACTION_GET_NEW_MODEL:
                    final String modelName = intent.getStringExtra(MODEL_NAME);
                    handleActionGetNewModel(modelName);
                    break;
            }
        }
    }

    private void handleActionGetNewModel(String model) {
        SharedPreferences logins = getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        String user = logins.getString("droshed_user", null);
        if(user == null) throw new IllegalStateException("User can't be null");


        //Ask server model schema
        Log.i("SERVICE", "Asking to provide model file named "+model);

        String modelSchema = askServerModelSchema(model);

        Intent intent = new Intent("droshed-new-model");
        intent.putExtra("model_name", model);

        if(modelSchema == null) {
            Log.i("SERVICE", "Model schema is null");
            intent.putExtra("status", OPERATION_ERROR);
            broadcastManager.sendBroadcast(intent);
        } else {
            DataManager.createModel(getApplicationContext(), model, modelSchema);

            intent.putExtra("status", OPERATION_OK);
            broadcastManager.sendBroadcast(intent);
        }
    }


    /**
     Ask the Service to receive updates (=update the local storage)
     */
    private void handleActionReceiveUpdate(String model) {
        SharedPreferences logins = getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        String user = logins.getString("droshed_user", null);
        if(user == null) throw new IllegalStateException("User can't be null");

        SharedPreferences sp = getSharedPreferences("droshed_model_"+model, MODE_PRIVATE);
        int currentClientVersion = sp.getInt(user+"_lastVersion", 0);

        //1. Ask last version
        Log.i("SERVICE", "Asking last server version for model "+model);
        int lastServerVersion = askServerLastVersion(model);
        Log.i("SERVICE", "Last version is "+lastServerVersion);

        while(currentClientVersion < lastServerVersion) {
            //2. Update for each missing version, and broadcast the update
            currentClientVersion++;

            /* Ici récupérer une version et la stocker */
            String newVersionData = askServerVersion(model, currentClientVersion);

            DataManager.createNewVersion(getApplicationContext(), model, newVersionData);

            /* Mettre à jour les informations persistantes */
            SharedPreferences.Editor e = sp.edit();
            e.putInt("currentVersion", currentClientVersion);
            e.apply();

            Intent intent = new Intent("droshed-sync");
            intent.putExtra("model_name", model);
            intent.putExtra("last_version", currentClientVersion);
            broadcastManager.sendBroadcast(intent);

        }
    }

    private int askServerLastVersion(String model) {
        HTTPResponse response = sendGetQuery(CURRENT_SERVER_IP + "/"+model+"/data/lastversion");
        try {
            if(response.header.getResponseCode() == 200) {
                return Integer.valueOf(response.body);
            }
        } catch(IOException e) {
            Log.i("SERVICE", "Error: "+e);
        }
        return -1;
    }

    private String askServerModelSchema(String model) {
        HTTPResponse response = sendGetQuery(CURRENT_SERVER_IP + "/"+model+"/model");
        try {
            if(response.header.getResponseCode() == 200) {
                return response.body;
            }
        } catch(IOException e) {
            Log.i("SERVICE", "Error: "+e);
        }
        return null;
    }

    private String askServerVersion(String model, int version) {
        HTTPResponse response = sendGetQuery(CURRENT_SERVER_IP + "/"+model+"/data/"+version);
        try {
            if(response.header.getResponseCode() == 200) {
                return response.body;
            }
        } catch(IOException e) {
            Log.i("SERVICE", "Error: "+e);
        }
        return null;
    }

    private static HTTPResponse sendPutQuery(String urlPath, String content) {
        return sendQuery(urlPath, "PUT", false, content);
    }

    private static HTTPResponse sendGetQuery(String urlPath) {
        return sendQuery(urlPath, "GET", true, null);
    }

    private static HTTPResponse sendQuery(String urlPath, String method, boolean expectInput, String content) {
        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
            //do nothing
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod(method);
            urlConnection.setDoInput(expectInput);

            if(content != null) {
                ByteBuffer requestBody = Charset.forName("UTF-8").encode(content);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Length", ""+requestBody.capacity());
                urlConnection.setRequestProperty("Content-Type", "text/html; charset=utf-8");


                BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(requestBody.array());
            }

            // Read answer
            Log.i("SERVICE", urlConnection.getResponseMessage());
            Log.i("SERVICE", "Length: "+urlConnection.getHeaderField("Content-Length"));
            Log.i("SERVICE", urlConnection.toString());

            if(expectInput) {
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                byte[] tmp = new byte[Integer.valueOf(urlConnection.getHeaderField("Content-Length"))];
                in.read(tmp);
                ByteBuffer wrapped = ByteBuffer.wrap(tmp);
                String body = Charset.forName(urlConnection.getContentType()).decode(wrapped).toString();

                Log.i("SERVICE", "Data: "+body);
                return new HTTPResponse(urlConnection, body);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new HTTPResponse(urlConnection, null);
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

        Log.i("SERVICE", "CheckAuth: Asking server");
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
                intent.putExtra("result", OPERATION_ERROR);
            } else {
                intent.putExtra("result", OPERATION_OK);
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

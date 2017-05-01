package genrozun.droshed;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * WIP : working on update/receive logic...
 */
public class SheetUpdateService extends IntentService {
    private static final String ACTION_RECEIVE_UPDATE = "genrozun.droshed.action.RECEIVE_UPDATE";
    private static final String ACTION_SEND_UPDATE = "genrozun.droshed.action.SEND_UPDATE";

    private static final String LAST_CLIENT_VERSION = "genrozun.droshed.extra.LAST_CLIENT_VERSION";


    private LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

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
        intent.putExtra(LAST_CLIENT_VERSION, lastClientVersion);
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
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECEIVE_UPDATE.equals(action)) {
                final int param1 = intent.getIntExtra(LAST_CLIENT_VERSION, 1);
                handleActionReceiveUpdate(param1);
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
        int lastServerVersion = 1;

        while(currentClientVersion < lastServerVersion) {

            //2. Update for each missing version, and broadcast the update
            Intent intent = new Intent("droshed-sync");
            intent.putExtra("last_version", currentClientVersion);
            broadcastManager.sendBroadcast(intent);
        }
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

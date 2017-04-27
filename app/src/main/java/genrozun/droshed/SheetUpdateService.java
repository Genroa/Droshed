package genrozun.droshed;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * WIP : working on update/receive logic...
 */
public class SheetUpdateService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_RECEIVE_UPDATE = "genrozun.droshed.action.RECEIVE_UPDATE";
    private static final String ACTION_SEND_UPDATE = "genrozun.droshed.action.SEND_UPDATE";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "genrozun.droshed.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "genrozun.droshed.extra.PARAM2";

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
    public static void startReceiveUpdate(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_RECEIVE_UPDATE);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startSendUpdate(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SheetUpdateService.class);
        intent.setAction(ACTION_SEND_UPDATE);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RECEIVE_UPDATE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionReceiveUpdate(param1, param2);
            } else if (ACTION_SEND_UPDATE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionSendUpdate(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionReceiveUpdate(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendUpdate(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

package genrozun.droshed.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ProgressBar;

import genrozun.droshed.R;
import genrozun.droshed.SheetView;
import genrozun.droshed.model.Model;
import genrozun.droshed.sync.SheetUpdateService;

public class SheetEditActivity extends AppCompatActivity {

    private Model currentModel;
    private String currentModelName;
    private SheetView sheetView;

    private BroadcastReceiver dataFileReceiver;

    public SheetEditActivity() {

        //Call the server to get last dataversion, then update the model
        this.dataFileReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String modelName = intent.getStringExtra("model_name");
                int modelVersion = intent.getIntExtra("last_version", 0);
                if(modelName.equals(currentModelName)) {
                    currentModel = Model.createModelFromModelFile(currentModelName, getApplicationContext());
                    sheetView.setModel(currentModel);
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_edit);
        LocalBroadcastManager.getInstance(this).registerReceiver(dataFileReceiver, new IntentFilter("droshed-sync"));

        this.sheetView = (SheetView) findViewById(R.id.grid);

        Intent intent = getIntent();
        this.currentModelName = intent.getStringExtra("model_name");
        this.currentModel = Model.createModelFromModelFile(currentModelName, getApplicationContext());
        SheetUpdateService.startReceiveUpdate(getApplicationContext(), currentModelName);


        sheetView.setModel(currentModel);
    }
}

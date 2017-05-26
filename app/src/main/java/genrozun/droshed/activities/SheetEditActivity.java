package genrozun.droshed.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import genrozun.droshed.R;
import genrozun.droshed.SheetView;
import genrozun.droshed.model.Model;

public class SheetEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_edit);

        SheetView sheetView = (SheetView) findViewById(R.id.grid);
        Intent intent = getIntent();
        sheetView.setModel(Model.createModelFromModelFile(intent.getStringExtra("model_name"), getApplicationContext()));
    }
}

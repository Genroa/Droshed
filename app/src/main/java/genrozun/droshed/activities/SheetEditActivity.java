package genrozun.droshed.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import genrozun.droshed.R;
import genrozun.droshed.SheetView;

public class SheetEditActivity extends AppCompatActivity {
    ScaleGestureDetector scaleGestureDetector;
    SheetView sheetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_edit);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScreenTouchListener());
        sheetView = (SheetView) findViewById(R.id.grid);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    class ScreenTouchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            sheetView.setZoomLevel((float) Math.min(Math.max(0.5, sheetView.getZoomLevel()+((scaleFactor-1)/20)), 1.5));
            return super.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }
}

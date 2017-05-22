package genrozun.droshed.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import genrozun.droshed.R;
import genrozun.droshed.SheetView;

public class SheetEditActivity extends AppCompatActivity implements View.OnTouchListener {
    ScaleGestureDetector scaleGestureDetector;
    SheetView sheetView;
    private float initialX;
    private float initialY;
    private float diffX;
    private float diffY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_edit);

        sheetView = (SheetView) findViewById(R.id.grid);
        sheetView.setOnTouchListener(this);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScreenTouchListener());


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float moveX = event.getRawX();
        float moveY = event.getRawY();
        int pointerCount;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = moveX;
                initialY = moveY;
                diffX = sheetView.getViewPositionX();
                diffY = sheetView.getViewPositionY();
                if(event.getPointerCount() > 1) return false;
                break;

            case MotionEvent.ACTION_MOVE:
                pointerCount = event.getPointerCount();
                if(pointerCount > 1) return false;

                moveX -= initialX;
                moveY -= initialY;

                Log.i("Touch", "X: "+moveX);
                Log.i("Touch", "Y: "+moveY);

                sheetView.setViewPositionX((diffX - moveX));
                sheetView.setViewPositionY((diffY - moveY));

                Log.i("Touch", "viewX: "+sheetView.getViewPositionX());
                Log.i("Touch", "viewY: "+sheetView.getViewPositionY());


                break;

            case MotionEvent.ACTION_UP:
                /*if(event.getPointerCount() == 1) {
                    sheetView.setViewPositionX(initialX);
                    sheetView.setViewPositionY(initialY);
                }*/
                Log.i("Touch", "End");
                break;

            default:
                break;
        }

        return true;
    }

    class ScreenTouchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            sheetView.setZoomLevel((float) Math.min(Math.max(0.5, sheetView.getZoomLevel()+((scaleFactor-1)/50)), 1.5));
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }
    }
}

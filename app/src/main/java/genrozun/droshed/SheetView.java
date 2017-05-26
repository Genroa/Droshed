package genrozun.droshed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.Objects;

import genrozun.droshed.model.Model;

/**
 * TODO: document your custom view class.
 */
public class SheetView extends View {
    private int paddingLeft = getPaddingLeft();
    private int paddingTop = getPaddingTop();
    private int paddingRight = getPaddingRight();
    private int paddingBottom = getPaddingBottom();

    private float zoomLevel = 1;
    private float viewPositionX = 0;
    private float viewPositionY = 0;
    private ScaleGestureDetector scaleGestureDetector;



    int contentWidth = getWidth() - paddingLeft - paddingRight;
    int contentHeight = getHeight() - paddingTop - paddingBottom;


    private float cellWidth;
    private float cellHeight;
    private Model currentModel;


    public SheetView(Context context) {
        super(context);
        init(context);
    }

    public SheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SheetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setOnTouchListener(new DragListener(this));
        scaleGestureDetector = new ScaleGestureDetector(context, new SheetView.PinchListener(this));
        recomputeDimensions();
    }

    private void recomputeDimensions() {
        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        cellWidth = (contentWidth / 4)* zoomLevel;
        cellHeight = (contentHeight / 9)* zoomLevel;

        //setModel(Model.create(""));
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setColor(Color.parseColor("#dddddd"));
        p.setStyle(Paint.Style.FILL);

        // BACKGROUND
        canvas.drawRect(paddingLeft,
                        paddingTop,
                        paddingLeft+contentWidth,
                        paddingTop+contentHeight,
                        p);

        // HEADER
        p.setColor(Color.parseColor("#c5c5c5"));
        canvas.drawRect(paddingLeft - (viewPositionX * zoomLevel),
                        paddingTop - (viewPositionY * zoomLevel),
                        paddingLeft+(cellWidth*(currentModel.getColumnNumber()+1)*zoomLevel) - (viewPositionX * zoomLevel),
                        paddingTop+cellHeight*zoomLevel - (viewPositionY * zoomLevel),
                        p);

        // FIRST COLUMN
        canvas.drawRect(paddingLeft - (viewPositionX * zoomLevel),
                paddingTop - (viewPositionY * zoomLevel),
                paddingLeft+(cellWidth*zoomLevel) - (viewPositionX * zoomLevel),
                paddingTop+(cellHeight*(currentModel.getLineNumber()+1)*zoomLevel) - (viewPositionY * zoomLevel),
                p);

        // GRID
        p.setColor(Color.parseColor("#a1a1a1"));
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        for(int line = 0; line < currentModel.getLineNumber()+1+1; line ++) {
            float linePosY = paddingTop + line*cellHeight*zoomLevel - (viewPositionY * zoomLevel);
            canvas.drawLine(paddingLeft - (viewPositionX * zoomLevel),
                            linePosY,
                            paddingLeft+(cellWidth*(currentModel.getColumnNumber()+1)*zoomLevel)- (viewPositionX * zoomLevel),
                            linePosY,
                            p);
        }

        for(int column = 0; column < currentModel.getColumnNumber()+1+1; column ++) {
            float columnPosX = paddingLeft + column*cellWidth*zoomLevel - (viewPositionX * zoomLevel);
            canvas.drawLine(columnPosX,
                            paddingTop - (viewPositionY * zoomLevel),
                            columnPosX,
                            paddingTop+(cellHeight*(currentModel.getLineNumber()+1)*zoomLevel)- (viewPositionY * zoomLevel),
                            p);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        contentWidth = widthMeasureSpec;
        contentHeight = heightMeasureSpec;
        recomputeDimensions();
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setModel(Model model) {
        currentModel = Objects.requireNonNull(model);
        invalidate();
        requestLayout();
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float newLevel) {
        //currentModel.getColumnNumber() * cellWidth * zoomLevel = contentWidth;
        //zoomLevel = contentWidth / (currentModel.getColumnNumber() * cellWidth)
        Log.i("Zoom", "Max zoomLevel: "+contentWidth / (currentModel.getColumnNumber() * cellWidth));
        zoomLevel = Math.max(newLevel, contentWidth / (currentModel.getColumnNumber() * cellWidth));
        recomputeDimensions();
    }

    public float getViewPositionX() {
        return viewPositionX;
    }

    public float getViewPositionY() {
        return viewPositionY;
    }

    public void setViewPositionX(float newX) {
        viewPositionX = Math.min(Math.max(0, newX*zoomLevel), ((currentModel.getColumnNumber()*cellWidth*zoomLevel)-contentWidth));
        recomputeDimensions();
    }

    public void setViewPositionY(float newY) {
        viewPositionY = Math.min(Math.max(0, newY), (((currentModel.getLineNumber()+1)*cellHeight*zoomLevel)-contentHeight));
        recomputeDimensions();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    class DragListener implements View.OnTouchListener {
        private SheetView sheetView;
        private float initialX;
        private float initialY;
        private float diffX;
        private float diffY;

        public DragListener(SheetView sheetView) {
            this.sheetView = sheetView;
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
    }

    public static class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float currentScale;
        private float initialScale;
        private SheetView sheetView;

        public PinchListener(SheetView sheetView) {
            this.sheetView = sheetView;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            initialScale = sheetView.getZoomLevel();
            currentScale = detector.getScaleFactor();
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {


            currentScale = initialScale + (detector.getScaleFactor()-1);
            Log.i("Scale", ""+currentScale);
            sheetView.setZoomLevel((float) Math.min(Math.max(0.5, currentScale), 1.5));
            sheetView.setViewPositionX(sheetView.getViewPositionX());
            sheetView.setViewPositionY(sheetView.getViewPositionY());
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }
}

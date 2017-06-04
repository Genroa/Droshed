package genrozun.droshed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.List;
import java.util.Objects;

import genrozun.droshed.activities.DataProjectionActivity;
import genrozun.droshed.model.Column;
import genrozun.droshed.model.Line;
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
        recomputeDimensions();
        setOnTouchListener(chain(new ClickListener(this), new DragListener(this)));
        scaleGestureDetector = new ScaleGestureDetector(context, new PinchListener(this));
    }

    private void recomputeDimensions() {
        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        cellWidth = (contentWidth / 4)* zoomLevel;
        cellHeight = (contentHeight / 9)* zoomLevel;

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

        // HEADER BACKGROUND COLOR
        p.setColor(Color.parseColor("#c5c5c5"));
        canvas.drawRect(paddingLeft - (viewPositionX * zoomLevel),
                        paddingTop - (viewPositionY * zoomLevel),
                        paddingLeft+(cellWidth*(currentModel.getColumnNumber()+1)*zoomLevel) - (viewPositionX * zoomLevel),
                        paddingTop+cellHeight*zoomLevel - (viewPositionY * zoomLevel),
                        p);

        // LINE ID BACKGROUND COLOR
        canvas.drawRect(paddingLeft - (viewPositionX * zoomLevel),
                paddingTop - (viewPositionY * zoomLevel),
                paddingLeft+(cellWidth*zoomLevel) - (viewPositionX * zoomLevel),
                paddingTop+(cellHeight*(currentModel.getLineNumber()+1)*zoomLevel) - (viewPositionY * zoomLevel),
                p);

        // GRID LINES
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

        p.setColor(Color.BLACK);

        int fontSize = 30;

        p.setTextSize(fontSize);
        p.setStyle(Paint.Style.FILL);

        drawCells(canvas, p);

        drawLineIDs(canvas, p);

        drawHeader(canvas, p);
    }

    private void drawCells(Canvas canvas, Paint p) {
        for(int columnNumber = 0; columnNumber < currentModel.getColumnNumber(); columnNumber++) {
            Column col = currentModel.getColumn(columnNumber);

            for(int lineNumber = 0; lineNumber < currentModel.getLineNumber(); lineNumber++) {
                Line line = currentModel.getLine(lineNumber);

                String cellValue = col.getValueAsString(line.getID());
                //if(cellValue == null) cellValue = "NULL";

                //Log.i("GRID", "CELL: col=" + col.getID() + " line=" + line.getID());

                canvas.drawText(limitLength(cellValue, (int)((cellWidth*zoomLevel*2)/(p.getTextSize()))),
                        paddingLeft+5*zoomLevel-(viewPositionX*zoomLevel)+cellWidth*zoomLevel*(columnNumber+1),
                        paddingTop+15*zoomLevel+(cellHeight/2*zoomLevel)-(viewPositionY*zoomLevel)+(cellHeight*zoomLevel*(lineNumber+1)),
                        p);

            }
        }
    }

    private void drawHeader(Canvas canvas, Paint p) {
        int i=1;
        for(Column column : currentModel.getColumns()) {
            canvas.drawText(limitLength(column.getName(), (int)((cellWidth*zoomLevel*2)/(p.getTextSize()))),
                    paddingLeft+5*zoomLevel-(viewPositionX*zoomLevel)+cellWidth*zoomLevel*i,
                    paddingTop+15*zoomLevel+((cellHeight/2)*zoomLevel)-(viewPositionY*zoomLevel),
                    p);
            i++;
        }
    }

    private void drawLineIDs(Canvas canvas, Paint p) {
        int j=1;
        for(Line line : currentModel.getLines()) {
            //Log.i("DRAW", "Y: "+(paddingTop+15+(cellHeight/2)-(viewPositionY*zoomLevel)+(cellHeight*j)));
            canvas.drawText(limitLength(line.getName(), (int)((cellWidth*zoomLevel*2)/(p.getTextSize()))),
                    paddingLeft+5*zoomLevel-(viewPositionX*zoomLevel),
                    paddingTop+15*zoomLevel+(cellHeight/2*zoomLevel)-(viewPositionY*zoomLevel)+(cellHeight*zoomLevel*j),
                    p);
            j++;
        }
    }


    private String limitLength(String content, int maxLength) {
        if(content.length() <= maxLength) return content;

        return content.substring(0, Math.max(0, maxLength-3))+ "...";
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
        recomputeDimensions();
        invalidate();
        requestLayout();
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float newLevel) {
        //currentModel.getColumnNumber() * cellWidth * zoomLevel = contentWidth;
        //zoomLevel = contentWidth / (currentModel.getColumnNumber() * cellWidth)
        Log.i("Zoom", "Max zoomLevel: "+contentWidth / ((currentModel.getColumnNumber()+1) * cellWidth));
        zoomLevel = Math.max(newLevel, contentWidth / ((currentModel.getColumnNumber()+1) * cellWidth));
        recomputeDimensions();
    }

    public float getViewPositionX() {
        return viewPositionX;
    }

    public float getViewPositionY() {
        return viewPositionY;
    }

    public void setViewPositionX(float newX) {
        viewPositionX = Math.max(0, newX);//, ((currentModel.getColumnNumber()*cellWidth*zoomLevel)-contentWidth));
        recomputeDimensions();
    }

    public void setViewPositionY(float newY) {
        viewPositionY = Math.max(0, newY);//, (((currentModel.getLineNumber()+1)*cellHeight*zoomLevel)-contentHeight));
        recomputeDimensions();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private void clickEvent(float x, float y) {
        // Detect on which cell the click event happened on the screen
        float realX = x + viewPositionX*zoomLevel;
        float realY = y + viewPositionY*zoomLevel;

        int columnPosition = (int) Math.floor(realX/(cellWidth*zoomLevel));
        int linePosition = (int) Math.floor(realY/(cellHeight*zoomLevel));

        if(columnPosition == 0 && linePosition == 0) return;
        if(columnPosition > currentModel.getColumnNumber() || linePosition > currentModel.getLineNumber()) return;

        // Appui sur une cellule en-tête de colonne.
        if(linePosition == 0) {
            Log.i("TOUCH", "Touched header");
            Column column = currentModel.getColumn(columnPosition-1);
            Intent clickIntent = new Intent(getContext(), DataProjectionActivity.class);

            Bundle b = new Bundle();
            b.putSerializable("model", currentModel);
            clickIntent.putExtra("modelBundle", b);

            clickIntent.putExtra("title", column.getName());
            clickIntent.putExtra("projection", "column");
            clickIntent.putExtra("id", column.getID());
            clickIntent.putExtra("targetID", "");
            Log.i("BUNDLE", b.toString());
            getContext().startActivity(clickIntent);
        } else {
            Line line = currentModel.getLine(linePosition-1);

            Intent clickIntent = new Intent(getContext(), DataProjectionActivity.class);

            Bundle b = new Bundle();
            b.putSerializable("model", currentModel);
            clickIntent.putExtra("modelBundle", b);

            clickIntent.putExtra("title", line.getName());
            clickIntent.putExtra("projection", "line");
            clickIntent.putExtra("id", line.getID());

            // Appui sur une ligne
            if(columnPosition == 0) {
                Log.i("TOUCH", "Touched line name col");
                clickIntent.putExtra("targetID", "");
            } else {
                clickIntent.putExtra("targetID", currentModel.getColumn(columnPosition-1).getID());
            }
            //Log.i("BUNDLE", b.toString());
            getContext().startActivity(clickIntent);
        }
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

                    //Log.i("Touch", "X: "+moveX);
                    //Log.i("Touch", "Y: "+moveY);

                    sheetView.setViewPositionX((diffX - moveX));
                    sheetView.setViewPositionY((diffY - moveY));

                    //Log.i("Touch", "viewX: "+sheetView.getViewPositionX());
                    //Log.i("Touch", "viewY: "+sheetView.getViewPositionY());


                    break;

                case MotionEvent.ACTION_UP:
                /*if(event.getPointerCount() == 1) {
                    sheetView.setViewPositionX(initialX);
                    sheetView.setViewPositionY(initialY);
                }*/
                    //Log.i("Touch", "End");
                    break;

                default:
                    break;
            }

            return true;
        }
    }

    class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
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

    class ClickListener implements View.OnTouchListener {
        private SheetView sheetView;
        private boolean tapping = false;

        public ClickListener(SheetView sheetView) {
            this.sheetView = sheetView;
        }
        private int originalX;
        private int originalY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("TAG", "touched down");
                    if(event.getPointerCount() == 1) {
                        tapping = true;
                        originalX = x;
                        originalY = y;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(Math.abs(originalX - x) > 20 || Math.abs(originalY - y) > 20) {
                        //Log.i("TAG", "moving: (" + x + ", " + y + ")");
                        tapping = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(tapping) {
                        //Log.i("TAG", "touched up");
                        clickEvent(originalX, originalY);
                    } else {
                        //Log.i("TAG", "Tap!");
                        tapping = false;
                        return true;
                    }
                    break;
            }

            return false;
        }
    }

    private static OnTouchListener chain(OnTouchListener... listeners) {
        return (View v, MotionEvent event) -> {
                boolean consumed = false;
                for(OnTouchListener listener : listeners) {
                    consumed = listener.onTouch(v, event);
                    if(consumed) return true;
                }

                return false;
            };
    }
}
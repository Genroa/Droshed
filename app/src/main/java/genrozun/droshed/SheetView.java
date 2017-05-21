package genrozun.droshed;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

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
    private ScaleGestureDetector gestureDetector;


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
    }

    private void recomputeDimensions() {
        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        cellWidth = (contentWidth / 4)* zoomLevel;
        cellHeight = (contentHeight / 9)* zoomLevel;

        viewPositionX = 0;//cellWidth/2;
        viewPositionY = 0;//cellHeight/2;

        currentModel = new Model();
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.FILL);

        // BACKGROUND
        canvas.drawRect(paddingLeft, paddingTop, paddingLeft+contentWidth, paddingTop+contentHeight, p);

        // HEADER
        Log.i("Width header", contentWidth+" "+currentModel.getColumnNumber()+" "+((contentWidth*currentModel.getColumnNumber()*zoomLevel) - (viewPositionX * zoomLevel)));
        p.setColor(Color.CYAN);
        canvas.drawRect(paddingLeft,
                        paddingTop,
                        paddingLeft+(cellWidth*currentModel.getColumnNumber()*zoomLevel) - (viewPositionX * zoomLevel),
                        paddingTop+cellHeight*zoomLevel - (viewPositionY * zoomLevel),
                        p);

        // GRID
        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        for(int line = 0; line < currentModel.getLineNumber()+1+1; line ++) {
            float linePosY = paddingTop + line*cellHeight*zoomLevel - (viewPositionY * zoomLevel);
            canvas.drawLine(paddingLeft - (viewPositionX * zoomLevel),
                            linePosY,
                            paddingLeft+(cellWidth*currentModel.getColumnNumber()*zoomLevel)- (viewPositionX * zoomLevel),
                            linePosY,
                            p);
        }

        for(int column = 0; column < currentModel.getColumnNumber()+1; column ++) {
            float columnPosX = paddingLeft + column*cellWidth*zoomLevel - (viewPositionX * zoomLevel);
            canvas.drawLine(columnPosX,
                            paddingTop - (viewPositionY * zoomLevel),
                            columnPosX,
                            paddingTop+(cellHeight*(currentModel.getLineNumber()+1)*zoomLevel)- (viewPositionY * zoomLevel),
                            p);
        }



        Log.i("draw", "Drawed view");
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
        currentModel = model;
        invalidate();
        requestLayout();
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float newLevel) {
        zoomLevel = newLevel;
        recomputeDimensions();
    }
}

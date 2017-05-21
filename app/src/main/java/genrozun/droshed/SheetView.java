package genrozun.droshed;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
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
    private int viewPositionX = 0;
    private int viewPositionY = 0;

    private Model currentModel;


    int contentWidth = getWidth() - paddingLeft - paddingRight;
    int contentHeight = getHeight() - paddingTop - paddingBottom;

    public SheetView(Context context) {
        super(context);
    }

    public SheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SheetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        // Draw the text.
        /*canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }*/
    }


    public void setModel(Model model) {
        currentModel = model;
        invalidate();
        requestLayout();
    }
}

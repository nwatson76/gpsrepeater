package com.nauticbits.gpsrepeater;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

//custom TextView class that is made to maximize its size to the container that it occupies
public class FontFitTextView extends TextView {

    public FontFitTextView(Context context) {
        super(context);
        initialise();
        
    }
    
    public FontFitTextView(Context context, int textSize) {
        super(context);
        initialise();
        setMaxTextSize(textSize);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		//resize the text to fit the parent it occupies
        refitText(this.getText().toString(), parentWidth, parentHeight);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }
    
    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    private void initialise() {
        testPaint = new Paint();
        testPaint.set(this.getPaint());
        //max size defaults to the initially specified text size unless it is too small
        
    	maxTextSize = this.getTextSize();
        if (maxTextSize < 20) {
            maxTextSize = 500;
        }
        
        minTextSize = 10;
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, int textWidth, int textHeight) 
    { 

    	if(text.length()<3)  textWidth = textWidth-50;
    	if (textWidth > 0) 
        {
            double availableWidth = (textWidth - this.getPaddingLeft() - this.getPaddingRight())*0.8;
            int availWidth = (int)availableWidth;
            double availableHeight = (textHeight - this.getPaddingTop() - this.getPaddingBottom())*0.8;
            int availHeight = (int)availableHeight;
            int trySize = (int)maxTextSize;
            int increment = 3;

            testPaint.setTextSize(trySize);
            
            while ((trySize > minTextSize) && (testPaint.measureText(text) > availWidth)) 
            {
                trySize -= increment;
                if (trySize <= minTextSize) 
                {
                    trySize = (int)minTextSize;
                    break;
                }
                testPaint.setTextSize(trySize);
            }

            this.setTextSize( TypedValue.COMPLEX_UNIT_PX, trySize);
        }
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth(), this.getHeight());
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w, h);
        }
    }

    //Getters and Setters
    public float getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
    }

    public float getMaxTextSize() {
        return maxTextSize;
    }

    public void setMaxTextSize(int minTextSize) {
        this.maxTextSize = minTextSize;
    }

    //Attributes
    private Paint testPaint;
    private float minTextSize;
    private float maxTextSize;
    

}

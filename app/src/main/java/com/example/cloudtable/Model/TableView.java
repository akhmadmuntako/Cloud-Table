package com.example.cloudtable.Model;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.example.cloudtable.R;

/**
 * Created by Lenovo on 09/08/2016.
 */
public class TableView extends View {
    private String displayShapeName;
    private int shapeColor;
    private Paint paintShape;
    private int shapeWidth = 100;
    private int shapeHeight = 100;
    private int textXOffset = 0;
    private int textYOffset = 30;
    private int left;
    private int top;
    private int bottom;
    private int right;
    // This is the view state for this shape selector
    private int currentShapeIndex = 0;

    public TableView(Context context) {
        this(context,null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
        setupPaint();
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, shapeWidth, shapeHeight, paintShape);
        canvas.drawText(displayShapeName, shapeWidth + textXOffset, shapeHeight + textXOffset, paintShape);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textPadding = 0;
        int contentWidth = shapeWidth;
        // Resolve the width based on our minimum and the measure spec
        int minw = contentWidth + getPaddingLeft() + getPaddingRight();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 0);

        // Ask for a height that would let the view get as big as it can
        int minh = shapeHeight + getPaddingBottom() + getPaddingTop();

            minh += textYOffset + textPadding;


        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        // Calling this method determines the measured width and height
        // Retrieve with getMeasuredWidth or getMeasuredHeight methods later
        setMeasuredDimension(w, h);
    }

    public String getDisplayShapeName() {
        return displayShapeName;
    }

    public void setDisplayingShapeName(String state) {
        this.displayShapeName = state;
        invalidate();
        requestLayout();
    }


    private void setupPaint() {
        paintShape = new Paint();
        paintShape.setStyle(Paint.Style.FILL);
        paintShape.setColor(Color.BLUE);
        paintShape.setTextSize(20);
    }


    private void setupAttributes(AttributeSet attrs) {
        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TableView, 0, 0);
        // Extract custom attributes into member variables
        try {
            shapeColor = a.getColor(R.styleable.TableView_shapeColor, Color.BLUE);
            shapeColor = a.getColor(R.styleable.TableView_displayShapeName, Color.BLACK);

        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }
    }

    @Override

    public Parcelable onSaveInstanceState() {

        // Construct bundle
        Bundle bundle = new Bundle();

        // Store base view state
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        // Save our custom view state to bundle
        bundle.putInt("currentShapeIndex", this.currentShapeIndex);
        // ... store any other custom state here ...
        // Return the bundle
        return bundle;

    }
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        // Checks if the state is the bundle we saved
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            // Load back our custom view state
            this.currentShapeIndex = bundle.getInt("currentShapeIndex");
            // ... load any other custom state here ...
            // Load base view state back
            state = bundle.getParcelable("instanceState");
        }
        // Pass base view state on to super
        super.onRestoreInstanceState(state);
    }



}

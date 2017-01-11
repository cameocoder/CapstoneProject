package com.cameocoder.capstoneproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * http://stackoverflow.com/questions/35761636/is-it-possible-to-use-vectordrawable-in-buttons-and-textviews-using-androiddraw
 */
public class VectorDrawableAppCompatButton extends AppCompatButton {
    public VectorDrawableAppCompatButton(Context context) {
        super(context);
    }

    public VectorDrawableAppCompatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public VectorDrawableAppCompatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.VectorDrawableAppCompatButton);

            Drawable drawableLeft = null;
            Drawable drawableRight = null;
            Drawable drawableBottom = null;
            Drawable drawableTop = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableLeft = attributeArray.getDrawable(R.styleable.VectorDrawableAppCompatButton_drawableLeftCompatButton);
                drawableRight = attributeArray.getDrawable(R.styleable.VectorDrawableAppCompatButton_drawableRightCompatButton);
                drawableBottom = attributeArray.getDrawable(R.styleable.VectorDrawableAppCompatButton_drawableBottomCompatButton);
                drawableTop = attributeArray.getDrawable(R.styleable.VectorDrawableAppCompatButton_drawableTopCompatButton);
            } else {
                final int drawableLeftId = attributeArray.getResourceId(R.styleable.VectorDrawableAppCompatButton_drawableLeftCompatButton, -1);
                final int drawableRightId = attributeArray.getResourceId(R.styleable.VectorDrawableAppCompatButton_drawableRightCompatButton, -1);
                final int drawableBottomId = attributeArray.getResourceId(R.styleable.VectorDrawableAppCompatButton_drawableBottomCompatButton, -1);
                final int drawableTopId = attributeArray.getResourceId(R.styleable.VectorDrawableAppCompatButton_drawableTopCompatButton, -1);

                if (drawableLeftId != -1)
                    drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId);
                if (drawableRightId != -1)
                    drawableRight = AppCompatResources.getDrawable(context, drawableRightId);
                if (drawableBottomId != -1)
                    drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
                if (drawableTopId != -1)
                    drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            attributeArray.recycle();
        }
    }
}
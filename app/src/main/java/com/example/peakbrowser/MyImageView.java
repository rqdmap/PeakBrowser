package com.example.peakbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* Attention: Special designed for Icon!
 * Take care if extended!
 */
@SuppressLint("AppCompatCustomView")
public abstract class MyImageView extends ImageView {
    protected Context mContext;

    // 点击时Path区域的转换，用于触摸点的判断
    protected RectF mPathRectF = new RectF();

    protected MyImageView.OnClickListener mClick;

    protected CircleArea area;

    protected boolean ok;

    // Initialize.
    public MyImageView(Context context) {
        this(context, null);
    }
    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initData();
    }

    private void initData(){
        int[] paths = new int[3];
        int basePX = WebActivity.dp2px(30);
        paths[0] =  (int)(57.0 / 67.0 * basePX);
        paths[1] = (int)(9.0 / 67.0 * basePX);
        paths[2] = (int)(20.0 / 67.0 * basePX);
        // Log: 70, 11, 24
        area = new CircleArea(paths);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        ok = false;
        checkArea(event);//检查touch是否在错号上

        mClick.onClick(ok);
        return super.onTouchEvent(event);
    }

    private void checkArea(MotionEvent event){
        mPathRectF.setEmpty();
        Path path = area.getmPath();
        path.computeBounds(mPathRectF, true);
        if(mPathRectF.contains(event.getX(), event.getY())) ok = true;
    }


    public void setOnClickListener(OnClickListener listener) {
        this.mClick = listener;
    }
    public interface OnClickListener {
        void onClick(boolean isCancel);
    }

    private class CircleArea {
        private Path mPath;
        public CircleArea(int[] paths){
            super();
            mPath = new Path();

            mPath.addCircle(paths[0], paths[1],paths[2], Path.Direction.CW);
            mPath.close();
        }

        public Path getmPath(){return mPath;}
    }
}

package com.md.demo.recyclerview.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by wangkegang on 2016/07/06 .
 */
public class MDGridRvDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    /**
     * 用于绘制间隔样式
     */
    private Drawable mDivider;

    public MDGridRvDividerDecoration(Context context) {
        // 获取默认主题的属性
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }


    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        // 绘制间隔，每一个item，绘制右边和下方间隔样式
        int childCount = parent.getChildCount();
        int spanCount = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        int orientation = ((GridLayoutManager)parent.getLayoutManager()).getOrientation();
        boolean isDrawHorizontalDivider = true;
        boolean isDrawVerticalDivider = true;
        int extra = childCount % spanCount;
        extra = extra == 0 ? spanCount : extra;//每行或者是每列最后一个item，位于该行或者该列中的位置
        for(int i = 0; i < childCount; i++) {
            isDrawVerticalDivider = true;
            isDrawHorizontalDivider = true;
            // 如果是竖向滑动，每行最后一个item不绘制右边的竖线
            if(orientation == OrientationHelper.VERTICAL && (i + 1) % spanCount == 0) {
                isDrawVerticalDivider = false;
            }

            // 如果是竖向滑动，最后一行不绘制底部的横线
            if(orientation == OrientationHelper.VERTICAL && i >= childCount - extra) {
                isDrawHorizontalDivider = false;
            }

            // 如果是横向滑动，最下面一行不绘制水平方向的间隔
            if(orientation == OrientationHelper.HORIZONTAL && (i + 1) % spanCount == 0) {
                isDrawHorizontalDivider = false;
            }

            // 如果是横向滑动，最后一列不绘制竖直方向间隔
            if(orientation == OrientationHelper.HORIZONTAL && i >= childCount - extra) {
                isDrawVerticalDivider = false;
            }

            if(isDrawHorizontalDivider) {
                drawHorizontalDivider(canvas, parent, i);
            }

            if(isDrawVerticalDivider) {
                drawVerticalDivider(canvas, parent, i);
            }
        }
    }

    int getItemOffsetsCount;
    /*
     * 计算分割线的距离，如果不重写 onDraw 会直接显示父控件的布局背景颜色
     * VERTICAL 和 HORIZONTAL 只有一个会生效，要么上下滑动，要么左右滑动
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 每个item都会计算一次
//        getItemOffsetsCount = getItemOffsetsCount+1;
//        Log.e("chris","getItemOffsetsCount==" + getItemOffsetsCount);

        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        // 本质上横向和竖向滑动的 spanCount 是有不同意义的，但是可以认为他们是一样的。
//        Log.e("chris","spanCount==" + spanCount);
        int orientation = ((GridLayoutManager)parent.getLayoutManager()).getOrientation();
        int position = parent.getChildLayoutPosition(view);

        // 为什么他不一样？每行最后一个item，不留边距，就是不打算在这里画分割线
        if(orientation == OrientationHelper.VERTICAL && (position + 1) % spanCount == 0) {// 每行最后一个item
            Log.e("chris","VERTICAL position==" + position);
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());// TODO: 2018/12/17 试试 mDivider.getIntrinsicHeight()改为0 ，这里与底部线是什么关系？
            return;
        }

        // 为什么他不一样？
        if(orientation == OrientationHelper.HORIZONTAL && (position + 1) % spanCount == 0) {// 每列最后一个item
            Log.e("chris","HORIZONTAL position==" + position);
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            return;
        }

        // 不是最后一条，就是又有item的正常上下左右的值
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
    }

    /**
     * 绘制竖直间隔线
     *
     * @param canvas
     * @param parent
     *              父布局，RecyclerView
     * @param position
     *              irem在父布局中所在的位置
     */
    private void drawVerticalDivider(Canvas canvas, RecyclerView parent, int position) {
        final View child = parent.getChildAt(position);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        final int top = child.getTop() - params.topMargin;
        final int bottom = child.getBottom() + params.bottomMargin + mDivider.getIntrinsicHeight();
        final int left = child.getRight() + params.rightMargin;
        final int right = left + mDivider.getIntrinsicWidth();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(canvas);
    }

    /**
     * 绘制水平间隔线
     *
     * @param canvas
     * @param parent
     *              父布局，RecyclerView
     * @param position
     *              item在父布局中所在的位置
     */
    private void drawHorizontalDivider(Canvas canvas, RecyclerView parent, int position) {
        final View child = parent.getChildAt(position);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        final int top = child.getBottom() + params.bottomMargin;
        final int bottom = top + mDivider.getIntrinsicHeight();
        final int left = child.getLeft() - params.leftMargin;
        final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(canvas);
    }
}

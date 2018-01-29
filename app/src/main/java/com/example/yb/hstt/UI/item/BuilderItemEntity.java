package com.example.yb.hstt.UI.item;

/**
 * Created by TFHR02 on 2017/6/29.
 */

import android.view.View;

/**
 * item对象
 */
public class BuilderItemEntity {
    /**
     * item距上高度
     */
    public int margintop = 0;
    /**
     * 左边的图标资源id
     */
    public int leftImgResId = 0;
    /**
     * 右边的图标资源id(0，默认显示返回箭头的图标；-1不显示箭头；)
     */
    public int rightImgResId = 0;
    /**
     * 标题文本
     */
    public String TitleText;
    /**
     * 标题文本颜色
     */
    public int TitleTextColor=-1;
    /**
     * 标题内容
     */
    public String TitleTextContent;
    /**
     * 是否设置分割线
     */
    public boolean isSetDivide;
    /**
     * item点击事件
     */
    public View.OnClickListener listener;
    /**
     * item右边图标点击事件
     */
    public View.OnClickListener rightImgListener;

    /**
     * 是否显示contentView(默认显示)
     */
    public boolean is_showContentView = true;

    /**
     * 基本构造方法
     *
     * @param margintop
     * @param leftImgResId
     * @param rightImgResId
     * @param titleText
     * @param titleTextContent
     * @param isSetDivide
     * @param listener
     * @param rightImgListener
     */
    public BuilderItemEntity(int margintop, int leftImgResId, int rightImgResId, String titleText, String titleTextContent,
                             boolean isSetDivide, View.OnClickListener listener, View.OnClickListener rightImgListener) {
        setParams(margintop, leftImgResId, rightImgResId, titleText, titleTextContent, isSetDivide, listener, rightImgListener);
    }

    /**
     * 加入文本颜色
     *
     * @param margintop
     * @param leftImgResId
     * @param rightImgResId
     * @param titleText
     * @param titleTextContent
     * @param isSetDivide
     * @param listener
     * @param rightImgListener
     * @param TitleTextColor   文本颜色
     */
    public BuilderItemEntity(int margintop, int leftImgResId, int rightImgResId, String titleText, String titleTextContent,
                             boolean isSetDivide, View.OnClickListener listener, View.OnClickListener rightImgListener, int TitleTextColor) {
        setParams(margintop, leftImgResId, rightImgResId, titleText, titleTextContent, isSetDivide, listener, rightImgListener);

        this.TitleTextColor = TitleTextColor;
    }

    /**
     * 加入了是否显示文本内容
     *
     * @param margintop
     * @param leftImgResId
     * @param rightImgResId
     * @param titleText
     * @param titleTextContent
     * @param isSetDivide
     * @param listener
     * @param rightImgListener
     * @param is_showContentView 是否显示主布局
     */
    public BuilderItemEntity(int margintop, int leftImgResId, int rightImgResId, String titleText, String titleTextContent,
                             boolean isSetDivide, View.OnClickListener listener, View.OnClickListener rightImgListener, boolean is_showContentView) {
        setParams(margintop, leftImgResId, rightImgResId, titleText, titleTextContent, isSetDivide, listener, rightImgListener);

        this.is_showContentView = is_showContentView;
    }

    private void setParams(int margintop, int leftImgResId, int rightImgResId, String titleText, String titleTextContent,
                           boolean isSetDivide, View.OnClickListener listener, View.OnClickListener rightImgListener) {
        this.margintop = margintop;
        this.leftImgResId = leftImgResId;
        this.rightImgResId = rightImgResId;
        this.TitleText = titleText;
        this.TitleTextContent = titleTextContent;
        this.isSetDivide = isSetDivide;
        this.listener = listener;
        this.rightImgListener = rightImgListener;
    }
}
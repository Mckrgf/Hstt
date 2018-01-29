package com.example.yb.hstt.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.yb.hstt.Utils.SystemAppUtils;


/**
 * @author ...
 */
public class SildingFinishLayout extends LinearLayout implements OnTouchListener {
	/**
	 * SildingFinishLayout布局的父布局
	 */
	private ViewGroup mParentView;
	/**
	 * 处理滑动逻辑的View
	 */
	private View touchView;
	/**
	 * 滑动的最小距离
	 */
	private int mTouchSlop;
	/**
	 * 按下点的X坐标
	 */
	private int downX;
	/**
	 * 按下点的Y坐标
	 */
	private int downY;
	/**
	 * 临时存储X坐标
	 */
	private int tempY;
	/**
	 * 滑动类
	 */
	private Scroller mScroller;
	/**
	 * SildingFinishLayout的高度
	 */
	private int viewHeight;
	/**
	 * 记录是否正在滑动
	 */
	private boolean isSilding;

	private boolean isToUpFinish=false;
	private boolean isToBottomFinish=true;

	private Interpolator interpolator=new BounceInterpolator();
	public SildingFinishLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context, null);
		mParentView = this;
	}

	View contentview;
	int windowheight;
	/**
	 * 设置Touch的View
	 * 
	 * @param touchView
	 */
	public void setTouchView(View touchView,View contentview,int windowheight) {
		this.touchView = touchView;
		this.contentview=contentview;
		this.windowheight=windowheight;
//		touchView.setOnTouchListener(this);
	}

	/**
	 * 滚动到下边
	 */
	private void scrollBottom() {
		final int delta = (windowheight + mParentView.getScrollY());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		// int startX;//滑动动作的起始点x坐标，偏移量
		// int startY;//滑动动作的起始点y坐标，偏移量
		// int dx; //x轴偏移量
		// int dy; //y轴偏移量
		//dy=-(startY+结束点y坐标)
		//dx=-(startX+结束点x坐标)
		mScroller.startScroll(0, mParentView.getScrollY(), 0, -(delta-contentview.getHeight()- SystemAppUtils.getStatusHeight(getContext())), Math.abs(delta));
		postInvalidate();
		
		//给事件源view，设置动画
//		if (is_need_animation) {
//			touchView.startAnimation(getAnimation(touhview_degress, touhview_degress+180,Math.abs(delta)));
//			touhview_degress=touhview_degress+180;
//		}
	}

	/**
	 * 滚动顶部
	 */
	private void scrollTop() {
		int delta = mParentView.getScrollY();
		mScroller.startScroll(0, mParentView.getScrollY(), 0, -(delta), Math.abs(delta));
		postInvalidate();
		
		//给事件源view，设置动画
//		if (is_need_animation) {
//			touchView.startAnimation(getAnimation(touhview_degress, touhview_degress+180,Math.abs(delta)));
//			touhview_degress=touhview_degress+180;
//		}
	}

	//是否需要调用动画
	private boolean is_need_animation;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) event.getRawX();
			downY = tempY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveY = (int) event.getRawY();
			//偏移量=开始位-结束位
			int deltaY = tempY - moveY;
			tempY = moveY;
			//判断是否在垂直方向产生了滑动
			if (Math.abs((int) moveY - downY) > mTouchSlop) {
				isSilding = true;
			}

			if (isSilding) {
				mParentView.scrollBy(0, deltaY);
			}
			break;
		case MotionEvent.ACTION_UP:
			//如果拖动的view，在y轴上有滑动时。。。
			isSilding = false;
			// getscrollx：偏移量（向右,下为负，向左，上为正）
			if (mParentView.getScrollY() <= -windowheight / 2) {//如果，滑动的前后差距大于，屏幕一半高
				if (isToUpFinish){//如果上次是在顶部
					is_need_animation=true;
				}else{
					is_need_animation=false;
				}
				scrollBottom();
				isToBottomFinish=true;
				isToUpFinish = false;

			} else {
				if (isToBottomFinish){//如果上次是在底部
					is_need_animation=true;
				}else{
					is_need_animation=false;
				}
				scrollTop();
				isToUpFinish=true;
				isToBottomFinish = false;
			}
			break;
		}
		return true;
	}

	/**
	 * 旋转度
	 */
	private int touhview_degress=0;
	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (mScroller.computeScrollOffset()) {
			// getCurrX () 返回当前滚动X方向的偏移 返回值 距离原点X方向的绝对值
			mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			if (mScroller.isFinished()) {
//				Log.i("scrollfinish", mScroller.getFinalY()+"--"+mScroller.getCurrY());
				//完成后的，回调处理，传递布局位置
				if (listener!=null) {
					listener.onSildingFinish(Math.abs(mScroller.getCurrY()));
				}
			}
		}
	}

	private OnSildingFinishListener listener;
	public void setOnSildingFinishListener(OnSildingFinishListener listener){
		this.listener=listener;
	}
	public interface OnSildingFinishListener {
		public void onSildingFinish(int curry);
	}
	
	private RotateAnimation r_animation = null;
	/**
	 * 初始化旋转动画，
	 * 
	 * @param fromDegress
	 *            开始角度
	 * @param toDegress
	 *            结束角度
	 * @return
	 */
	public RotateAnimation getAnimation(int fromDegress, int toDegress,long duration) {
		r_animation = new RotateAnimation(fromDegress, toDegress, touchView.getWidth() / 2,
				touchView.getHeight() / 2);
		r_animation.setDuration(duration);
		r_animation.setFillAfter(true);
		return r_animation;
	}


}

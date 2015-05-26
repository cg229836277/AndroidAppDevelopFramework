package com.chuck.commonlib.view;

import com.chuck.commonlib.util.StringUtil;
import com.example.chuckcommonlib.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class CustomFlipperView extends ViewFlipper {
	private OnFlipListener onFlipListener;
	private final String TAG = "NotifiableViewFlipper";
	
	public final static String STYLE_VIEW_MATCH_PARENT = "layout_match_parent";
	public final static String STYLE_VIEW_WRAP_PARENT = "layout_wrap_parent";

	public static interface OnFlipListener {
		public void onShowPrevious(CustomFlipperView flipper);

		public void onShowNext(CustomFlipperView flipper);
		
		public void flipToTheLast();
	}

	public void setOnFlipListener(OnFlipListener onFlipListener) {
		this.onFlipListener = onFlipListener;
	}

	public CustomFlipperView(Context context) {
		super(context);
	}

	public CustomFlipperView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void showPrevious() {
		super.showPrevious();
		if (hasFlipListener()) {
			onFlipListener.onShowPrevious(this);
		}
	}
	
	public void addChildView(View view , String addStyle){
		if(StringUtil.isEmpty(addStyle) || view == null){
			return;
		}
		
		LayoutParams params = null;
		
		if(STYLE_VIEW_MATCH_PARENT.equals(addStyle)){
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}else if(STYLE_VIEW_WRAP_PARENT.equals(addStyle)){
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		if(params != null){		
			int childViewCount = getChildCount();
			view.setTag(childViewCount + 1);
			addView(view, params);
		}
	}
	
	public void startRightFlipAnimation(){
		Animation rInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in); 	
		Animation rOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_right_out); 

		setInAnimation(rInAnim);
		setOutAnimation(rOutAnim);
		showPrevious();
	}
	
	public void startLeftFlipAnimation(){
		Animation lInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);	
		Animation lOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out); 	

		setInAnimation(lInAnim);
		setOutAnimation(lOutAnim);
		showNext();
	}

	@Override
	public void showNext() {
		super.showNext();
		if (hasFlipListener()) {
			onFlipListener.onShowNext(this);
			
			View view  = getCurrentView();
			if(view != null){
				int tag = (Integer)view.getTag();
				//Log.e(TAG, "tag = " + tag + " count = " + getChildCount());
				if(tag == getChildCount()){
					onFlipListener.flipToTheLast();
				}
			}
		}
	}

	private boolean hasFlipListener() {
		return onFlipListener != null;
	}
}
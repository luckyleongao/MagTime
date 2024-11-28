package com.leongao.magtime.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.leongao.magtime.R;


/**
 * 阅读器的顶部悬浮菜单
 */
public class ReaderMenuTopView extends ViewAnimator {
	private ImageView mBtnBack;
	private Animation mAniIn;
	private Animation mAniOut;
	
	public ReaderMenuTopView(Context context) {
		this(context,null);
	}
	
	public ReaderMenuTopView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View view = LayoutInflater.from(context).inflate(R.layout.fragment_classic_reader_menu_top, this);
		mBtnBack = view.findViewById(R.id.btn_back);
		if(!isInEditMode()){
			mAniIn = AnimationUtils.loadAnimation(context,R.anim.reader_menu_top_in);
			mAniOut = AnimationUtils.loadAnimation(context, R.anim.reader_menu_top_out);
		}		
		
	}

	public ImageView getBackBtn() {
		return mBtnBack;
	}

	public void runAnimationIn() {
		this.setVisibility(View.VISIBLE);
		this.startAnimation(mAniIn);
	}
	
	public void runAnimationOut() {
		this.setVisibility(View.INVISIBLE);
		this.startAnimation(mAniOut);
	}
}

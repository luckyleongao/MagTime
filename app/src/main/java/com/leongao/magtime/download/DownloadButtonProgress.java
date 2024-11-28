package com.leongao.magtime.download;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.leongao.magtime.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdularis on 02/01/18.
 *
 * This view class shows 4 different view in different state
 * 1. Idle state show a button (download button in this case, but not limited)
 * 2. Indeterminate state show indeterminate circular progress, with optional button in the center
 * 3. Determinate state show determinate progress, with optional button in the center
 * 4. Finish state show finish drawable or hide this view
 *
 * you can use this view to make a download or upload button, you might also use this for another
 * purpose.
 */

public class DownloadButtonProgress extends View implements View.OnClickListener {

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_MAX_PROGRESS = "max_progress";
    private static final String INSTANCE_CURRENT_PROGRESS = "current_progress";
    private static final String INSTANCE_CURRENT_STATE = "current_state";
    private static final String INSTANCE_PAUSABLE = "pausable";
    private static final String INSTANCE_IDLE_WIDTH = "idle_width";
    private static final String INSTANCE_IDLE_HEIGHT = "idle_height";
    private static final String INSTANCE_PAUSE_WIDTH = "pause_width";
    private static final String INSTANCE_PAUSE_HEIGHT = "pause_height";
    private static final String INSTANCE_RESUME_WIDTH = "resume_width";
    private static final String INSTANCE_RESUME_HEIGHT = "resume_height";
    private static final String INSTANCE_FINISH_WIDTH = "finish_width";
    private static final String INSTANCE_FINISH_HEIGHT = "finish_height";
    private static final String INSTANCE_IDLE_BG_COLOR = "idle_bg_color";
    private static final String INSTANCE_FINISH_BG_COLOR = "finish_bg_color";
    private static final String INSTANCE_RESUME_BG_COLOR = "finish_bg_color";
    private static final String INSTANCE_INDETERMINATE_BG_COLOR = "indeterminate_bg_color";
    private static final String INSTANCE_DETERMINATE_BG_COLOR = "determinate_bg_color";
    private static final String INSTANCE_PROGRESS_DETERMINATE_COLOR = "prog_det_color";
    private static final String INSTANCE_PROGRESS_INDETERMINATE_COLOR = "prog_indet_color";
    private static final String INSTANCE_PROGRESS_MARGIN = "prog_margin";

    public static final int  STATE_IDLE = 1;
    public static final int STATE_INDETERMINATE = 2;
    public static final int STATE_DETERMINATE = 3;
    public static final int STATE_FINISHED = 4;
    public static final int STATE_RESUME = 5;

    private static final int BASE_START_ANGLE = -90;
    private static final int DEF_BG_COLOR = 0xB4000000;
    private static final boolean DEF_PAUSABLE = true;
    private static final int DEF_DETERMINATE_COLOR = Color.GREEN;
    private static final int DEF_INDETERMINATE_COLOR = Color.WHITE;
    private static final int DEF_PROGRESS_WIDTH = 8;
    private static final int DEF_PROGRESS_MARGIN = 5;
    private static final int DEF_PROGRESS_INDETERMINATE_WIDTH = 90;


    private Drawable mIdleIcon;
    private Drawable mPauseIcon;
    private Drawable mResumeIcon;
    private Drawable mFinishIcon;
    private boolean mPausable = DEF_PAUSABLE;
    private int mIdleIconWidth;
    private int mIdleIconHeight;
    private int mPauseIconWidth;
    private int mPauseIconHeight;
    private int mResumeIconWidth;
    private int mResumeIconHeight;
    private int mFinishIconWidth;
    private int mFinishIconHeight;

    private int mCurrState = STATE_IDLE;
    private int mMaxProgress = 100;
    private int mCurrProgress = 0;

    private int mIdleBgColor = DEF_BG_COLOR;
    private int mFinishBgColor = DEF_BG_COLOR;
    private int mIndeterminateBgColor = DEF_BG_COLOR;
    private int mDeterminateBgColor = DEF_BG_COLOR;
    private int mResumeBgColor = DEF_BG_COLOR;

    private Drawable mIdleBgDrawable;
    private Drawable mFinishBgDrawable;
    private Drawable mIndeterminateBgDrawable;
    private Drawable mDeterminateBgDrawable;
    private Drawable mResumeBgDrawable;

    private ValueAnimator mIndeterminateAnimator;
    private int mCurrIndeterminateBarPos;
    private int mProgressIndeterminateSweepAngle = DEF_PROGRESS_INDETERMINATE_WIDTH;

    private int mProgressDeterminateColor = DEF_DETERMINATE_COLOR;
    private int mProgressIndeterminateColor = DEF_INDETERMINATE_COLOR;
    private int mProgressMargin = DEF_PROGRESS_MARGIN;

    private final Paint mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mBgRect = new RectF();

    private final Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mProgressRect = new RectF();

    private final List<OnClickListener> mClickListeners = new ArrayList<>();
    private final List<OnStateChangedListener> mOnStateChangedListeners = new ArrayList<>();

    public DownloadButtonProgress(Context context) {
        this(context, null);
    }

    public DownloadButtonProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnClickListener(this);

        initIndeterminateAnimator();

        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setDither(true);
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setPathEffect(new CornerPathEffect(50f));

        Resources res = context.getResources();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownloadButtonProgress, 0, 0);

            initBackgroundDrawableFromAttribs(res, a);

            mCurrState = a.getInt(R.styleable.DownloadButtonProgress_state, STATE_IDLE);
            mPausable = a.getBoolean(R.styleable.DownloadButtonProgress_pausable, DEF_PAUSABLE);
            mProgressIndeterminateSweepAngle = a.getInteger(R.styleable.DownloadButtonProgress_progressIndeterminateSweepAngle, DEF_PROGRESS_INDETERMINATE_WIDTH);
            mProgressDeterminateColor = a.getColor(R.styleable.DownloadButtonProgress_progressDeterminateColor, DEF_DETERMINATE_COLOR);
            mProgressIndeterminateColor = a.getColor(R.styleable.DownloadButtonProgress_progressIndeterminateColor, DEF_INDETERMINATE_COLOR);
            mProgressPaint.setStrokeWidth(
                    a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_progressWidth, DEF_PROGRESS_WIDTH)
            );
            mProgressMargin = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_progressMargin, DEF_PROGRESS_MARGIN);
            mCurrProgress = a.getInteger(R.styleable.DownloadButtonProgress_currentProgress, 0);
            mMaxProgress = a.getInteger(R.styleable.DownloadButtonProgress_maxProgress, 100);

            int icIdleDrawableId = a.getResourceId(R.styleable.DownloadButtonProgress_idleIconDrawable, R.drawable.ic_default_download);
            mIdleIcon = res.getDrawable(icIdleDrawableId);
            mIdleIconWidth = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_idleIconWidth, mIdleIcon.getMinimumWidth());
            mIdleIconHeight = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_idleIconHeight, mIdleIcon.getMinimumHeight());

            int icPauseDrawableId = a.getResourceId(R.styleable.DownloadButtonProgress_pauseIconDrawable, R.drawable.ic_default_pause);
            mPauseIcon = res.getDrawable(icPauseDrawableId);
            mPauseIconWidth = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_pauseIconWidth, mPauseIcon.getMinimumWidth());
            mPauseIconHeight = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_pauseIconHeight, mPauseIcon.getMinimumHeight());

            int icResumeDrawableId = a.getResourceId(R.styleable.DownloadButtonProgress_resumeIconDrawable, R.drawable.ic_default_resume);
            mResumeIcon = res.getDrawable(icResumeDrawableId);
            mResumeIconWidth = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_resumeIconWidth, mResumeIcon.getMinimumWidth());
            mResumeIconHeight = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_resumeIconHeight, mResumeIcon.getMinimumHeight());

            int icFinishDrawableId = a.getResourceId(R.styleable.DownloadButtonProgress_finishIconDrawable, R.drawable.ic_default_finish);
            mFinishIcon = res.getDrawable(icFinishDrawableId);
            mFinishIconWidth = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_finishIconWidth, mFinishIcon.getMinimumWidth());
            mFinishIconHeight = a.getDimensionPixelSize(R.styleable.DownloadButtonProgress_finishIconHeight, mFinishIcon.getMinimumHeight());

            a.recycle();
        } else {
            mProgressPaint.setStrokeWidth(DEF_PROGRESS_WIDTH);

            mIdleIcon = res.getDrawable(R.drawable.ic_default_download);
            mIdleIconWidth = mIdleIcon.getMinimumWidth();
            mIdleIconHeight = mIdleIcon.getMinimumHeight();

            mPauseIcon = res.getDrawable(R.drawable.ic_default_pause);
            mPauseIconWidth = mPauseIcon.getMinimumWidth();
            mPauseIconHeight = mPauseIcon.getMinimumHeight();

            mResumeIcon = res.getDrawable(R.drawable.ic_default_resume);
            mResumeIconWidth = mResumeIcon.getMinimumWidth();
            mResumeIconHeight = mResumeIcon.getMinimumHeight();

            mFinishIcon = res.getDrawable(R.drawable.ic_default_finish);
            mFinishIconWidth = mFinishIcon.getMinimumWidth();
            mFinishIconHeight = mFinishIcon.getMinimumHeight();
        }

        if (mCurrState == STATE_INDETERMINATE)
            setIndeterminate();
    }

    private void initBackgroundDrawableFromAttribs(Resources res, TypedArray attrs) {
        int idleResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_idleBackgroundDrawable, -1);
        int finishResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_finishBackgroundDrawable, -1);
        int indeterminateResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_indeterminateBackgroundDrawable, -1);
        int determinateResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_determinateBackgroundDrawable, -1);
        int resumeResId = attrs.getResourceId(R.styleable.DownloadButtonProgress_resumeBackgroundDrawable, -1);

        if (idleResId != -1) mIdleBgDrawable = res.getDrawable(idleResId);
        if (finishResId != -1) mFinishBgDrawable = res.getDrawable(finishResId);
        if (indeterminateResId != -1) mIndeterminateBgDrawable = res.getDrawable(indeterminateResId);
        if (determinateResId != -1) mDeterminateBgDrawable = res.getDrawable(determinateResId);
        if (resumeResId != -1) mResumeBgDrawable = res.getDrawable(resumeResId);


        mIdleBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_idleBackgroundColor, DEF_BG_COLOR);
        mFinishBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_finishBackgroundColor, DEF_BG_COLOR);
        mIndeterminateBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_indeterminateBackgroundColor, DEF_BG_COLOR);
        mDeterminateBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_determinateBackgroundColor, DEF_BG_COLOR);
        mResumeBgColor = attrs.getColor(R.styleable.DownloadButtonProgress_resumeBackgroundColor, DEF_BG_COLOR);
    }

    public int getCurrState() {
        return mCurrState;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public int getCurrentProgress() {
        return mCurrProgress;
    }

    public Drawable getIdleIcon() {
        return mIdleIcon;
    }

    public Drawable getPauseIcon() {
        return mPauseIcon;
    }

    public Drawable getResumeIcon() {
        return mResumeIcon;
    }
    public Drawable getFinishIcon() {
        return mFinishIcon;
    }

    public boolean isPausable() {
        return mPausable;
    }

    public int getIdleIconWidth() {
        return mIdleIconWidth;
    }

    public int getIdleIconHeight() {
        return mIdleIconHeight;
    }

    public int getPauseIconWidth() {
        return mPauseIconWidth;
    }

    public int getPauseIconHeight() {
        return mPauseIconHeight;
    }

    public int getResumeIconWidth() {
        return mResumeIconWidth;
    }

    public int getResumeIconHeight() {
        return mResumeIconHeight;
    }

    public int getFinishIconWidth() {
        return mFinishIconWidth;
    }

    public int getFinishIconHeight() {
        return mFinishIconHeight;
    }

    public int getIdleBgColor() {
        return mIdleBgColor;
    }

    public int getFinishBgColor() {
        return mFinishBgColor;
    }

    public int getResumeBgColor() {
        return mResumeBgColor;
    }

    public int getIndeterminateBgColor() {
        return mIndeterminateBgColor;
    }

    public int getDeterminateBgColor() {
        return mDeterminateBgColor;
    }

    public Drawable getIdleBgDrawable() {
        return mIdleBgDrawable;
    }

    public Drawable getFinishBgDrawable() {
        return mFinishBgDrawable;
    }

    public Drawable getIndeterminateBgDrawable() {
        return mIndeterminateBgDrawable;
    }

    public Drawable getDeterminateBgDrawable() {
        return mDeterminateBgDrawable;
    }

    public int getProgressDeterminateColor() {
        return mProgressDeterminateColor;
    }

    public int getProgressIndeterminateColor() {
        return mProgressIndeterminateColor;
    }

    public int getProgressMargin() {
        return mProgressMargin;
    }

    public int getProgressIndeterminateSweepAngle() {
        return mProgressIndeterminateSweepAngle;
    }

    public void setCurrentProgress(int progress) {
        if (mCurrState != STATE_DETERMINATE && mCurrState != STATE_RESUME) return;
        mCurrProgress = Math.min(progress, mMaxProgress);
        invalidate();
    }

    public void setIdle() {
        mCurrState = STATE_IDLE;
        callStateChangedListener(mCurrState);

        invalidate();
    }

    public void setResume() {
        mCurrProgress = 0;
        mCurrState = STATE_RESUME;
        callStateChangedListener(mCurrState);

        invalidate();
    }

    public void setIndeterminate() {
        mCurrIndeterminateBarPos = BASE_START_ANGLE;
        mCurrState = STATE_INDETERMINATE;
        callStateChangedListener(mCurrState);

        invalidate();

        mIndeterminateAnimator.start();
    }

    public void setDeterminate() {
        mIndeterminateAnimator.end();

        mCurrProgress = 0;
        mCurrState = STATE_DETERMINATE;
        callStateChangedListener(mCurrState);

        invalidate();
    }

    public void setFinish() {
        mCurrProgress = 0;
        mCurrState = STATE_FINISHED;
        callStateChangedListener(mCurrState);

        invalidate();
    }

    public void setIdleIcon(Drawable idleIcon) {
        mIdleIcon = idleIcon;
        invalidate();
    }

    public void setPauseIcon(Drawable pauseIcon) {
        mPauseIcon = pauseIcon;
        invalidate();
    }

    public void setResumeIcon(Drawable resumeIcon) {
        mResumeIcon = resumeIcon;
        invalidate();
    }

    public void setFinishIcon(Drawable finishIcon) {
        mFinishIcon = finishIcon;
        invalidate();
    }

    public void setPausable(boolean pausable) {
        mPausable = pausable;
        invalidate();
    }

    public void setIdleIconWidth(int idleIconWidth) {
        mIdleIconWidth = idleIconWidth;
        invalidate();
    }

    public void setIdleIconHeight(int idleIconHeight) {
        mIdleIconHeight = idleIconHeight;
        invalidate();
    }

    public void setPauseIconWidth(int pauseIconWidth) {
        mPauseIconWidth = pauseIconWidth;
        invalidate();
    }

    public void setPauseIconHeight(int pauseIconHeight) {
        mPauseIconHeight = pauseIconHeight;
        invalidate();
    }

    public void setResumeIconWidth(int resumeIconWidth) {
        mPauseIconWidth = resumeIconWidth;
        invalidate();
    }

    public void setResumeIconHeight(int resumeIconHeight) {
        mResumeIconHeight = resumeIconHeight;
        invalidate();
    }

    public void setFinishIconWidth(int finishIconWidth) {
        mFinishIconWidth = finishIconWidth;
        invalidate();
    }

    public void setFinishIconHeight(int finishIconHeight) {
        mFinishIconHeight = finishIconHeight;
        invalidate();
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        invalidate();
    }

    public void setIdleBgColor(int idleBgColor) {
        mIdleBgColor = idleBgColor;
        invalidate();
    }

    public void setResumeBgColor(int resumeBgColor) {
        mResumeBgColor = resumeBgColor;
        invalidate();
    }

    public void setFinishBgColor(int finishBgColor) {
        mFinishBgColor = finishBgColor;
        invalidate();
    }

    public void setIndeterminateBgColor(int indeterminateBgColor) {
        mIndeterminateBgColor = indeterminateBgColor;
        invalidate();
    }

    public void setDeterminateBgColor(int determinateBgColor) {
        mDeterminateBgColor = determinateBgColor;
        invalidate();
    }

    public void setIdleBgDrawable(Drawable idleBgDrawable) {
        mIdleBgDrawable = idleBgDrawable;
        invalidate();
    }

    public void setResumeBgDrawable(Drawable resumeBgDrawable) {
        mResumeBgDrawable = resumeBgDrawable;
        invalidate();
    }

    public void setFinishBgDrawable(Drawable finishBgDrawable) {
        mFinishBgDrawable = finishBgDrawable;
        invalidate();
    }

    public void setIndeterminateBgDrawable(Drawable indeterminateBgDrawable) {
        mIndeterminateBgDrawable = indeterminateBgDrawable;
        invalidate();
    }

    public void setDeterminateBgDrawable(Drawable determinateBgDrawable) {
        mDeterminateBgDrawable = determinateBgDrawable;
        invalidate();
    }

    public void setProgressDeterminateColor(int progressDeterminateColor) {
        mProgressDeterminateColor = progressDeterminateColor;
        invalidate();
    }

    public void setProgressIndeterminateColor(int progressIndeterminateColor) {
        mProgressIndeterminateColor = progressIndeterminateColor;
        invalidate();
    }

    public void setProgressMargin(int progressMargin) {
        mProgressMargin = progressMargin;
        invalidate();
    }

    public void setProgressIndeterminateSweepAngle(int progressIndeterminateSweepAngle) {
        mProgressIndeterminateSweepAngle = progressIndeterminateSweepAngle;
        invalidate();
    }

    public void addOnClickListener(OnClickListener listener) {
        if (!mClickListeners.contains(listener))
            mClickListeners.add(listener);
    }

    public void removeOnClickListener(OnClickListener listener) {
        mClickListeners.remove(listener);
    }

    public void addOnStateChangedListeners(OnStateChangedListener listener) {
        if (!mOnStateChangedListeners.contains(listener))
            mOnStateChangedListeners.add(listener);
    }

    public void removeOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListeners.remove(listener);
    }

    private void callStateChangedListener(int newState) {
        for (OnStateChangedListener listener : mOnStateChangedListeners)
            listener.onStateChanged(newState);
    }

    @Override
    public void onClick(View v) {
        if (!/*mCancelable*/mPausable && (mCurrState == STATE_INDETERMINATE || mCurrState == STATE_DETERMINATE))
            return;

        if (mCurrState == STATE_IDLE) {
            for (OnClickListener listener : mClickListeners)
                listener.onIdleButtonClick(v);
        }
        else if (mCurrState == STATE_INDETERMINATE || mCurrState == STATE_DETERMINATE) {
            for (OnClickListener listener : mClickListeners)
                listener.onPauseButtonClick(v);
        }
        else if (mCurrState == STATE_RESUME) {
            for (OnClickListener listener : mClickListeners)
                listener.onResumeButtonClick(v);
        }
        else if (mCurrState == STATE_FINISHED) {
            for (OnClickListener listener : mClickListeners)
                listener.onFinishButtonClick(v);
        }
    }

    private void drawIdleState(Canvas canvas) {
        if (mIdleBgDrawable != null) {
            mIdleBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mIdleBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mIdleBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        drawDrawableInCenter(mIdleIcon, canvas, mIdleIconWidth, mIdleIconHeight);
    }

    private void drawResumeState(Canvas canvas) {
        if (mResumeBgDrawable != null) {
            mResumeBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mResumeBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mResumeBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        drawDrawableInCenter(mResumeIcon, canvas, mResumeIconWidth, mResumeIconHeight);

        setProgressRectBounds();
        mProgressPaint.setColor(mProgressDeterminateColor);
        canvas.drawArc(mProgressRect, BASE_START_ANGLE, getDegrees(), false, mProgressPaint);
    }

    private void drawFinishState(Canvas canvas) {
        if (mFinishBgDrawable != null) {
            mFinishBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mFinishBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mFinishBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        drawDrawableInCenter(mFinishIcon, canvas, mFinishIconWidth, mFinishIconHeight);
    }

    private void drawIndeterminateState(Canvas canvas) {
        if (mIndeterminateBgDrawable != null) {
            mIndeterminateBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mIndeterminateBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mIndeterminateBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        if (mPausable) {
            drawDrawableInCenter(mPauseIcon, canvas, mPauseIconWidth, mPauseIconHeight);
        }

        setProgressRectBounds();
        mProgressPaint.setColor(mProgressIndeterminateColor);
        canvas.drawArc(mProgressRect, mCurrIndeterminateBarPos, mProgressIndeterminateSweepAngle, false, mProgressPaint);
    }

    private void drawDeterminateState(Canvas canvas) {
        if (mDeterminateBgDrawable != null) {
            mDeterminateBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            mDeterminateBgDrawable.draw(canvas);
        } else {
            mBgRect.set(0, 0, getWidth(), getHeight());
            mBgPaint.setColor(mDeterminateBgColor);
            canvas.drawOval(mBgRect, mBgPaint);
        }

        if (mPausable) {
            drawDrawableInCenter(mPauseIcon, canvas, mPauseIconWidth, mPauseIconHeight);
        }

        setProgressRectBounds();
        mProgressPaint.setColor(mProgressDeterminateColor);
        canvas.drawArc(mProgressRect, BASE_START_ANGLE, getDegrees(), false, mProgressPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurrState == STATE_IDLE) {
            drawIdleState(canvas);
        }
        else if (mCurrState == STATE_INDETERMINATE) {
            drawIndeterminateState(canvas);
        }
        else if (mCurrState == STATE_DETERMINATE) {
            drawDeterminateState(canvas);
        }
        else if (mCurrState == STATE_RESUME) {
            drawResumeState(canvas);
        }
        else if (mCurrState == STATE_FINISHED) {
            drawFinishState(canvas);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_MAX_PROGRESS, getMaxProgress());
        bundle.putInt(INSTANCE_CURRENT_PROGRESS, getCurrentProgress());
        bundle.putInt(INSTANCE_CURRENT_STATE, getCurrState());
        bundle.putBoolean(INSTANCE_PAUSABLE, isPausable());
        bundle.putInt(INSTANCE_IDLE_WIDTH, getIdleIconWidth());
        bundle.putInt(INSTANCE_IDLE_HEIGHT, getIdleIconHeight());
        bundle.putInt(INSTANCE_PAUSE_WIDTH, getPauseIconWidth());
        bundle.putInt(INSTANCE_PAUSE_HEIGHT, getPauseIconHeight());
        bundle.putInt(INSTANCE_RESUME_WIDTH, getResumeIconWidth());
        bundle.putInt(INSTANCE_RESUME_HEIGHT, getResumeIconHeight());
        bundle.putInt(INSTANCE_FINISH_WIDTH, getFinishIconWidth());
        bundle.putInt(INSTANCE_FINISH_HEIGHT, getFinishIconHeight());
        bundle.putInt(INSTANCE_IDLE_BG_COLOR, getIdleBgColor());
        bundle.putInt(INSTANCE_FINISH_BG_COLOR, getFinishBgColor());
        bundle.putInt(INSTANCE_RESUME_BG_COLOR, getResumeBgColor());
        bundle.putInt(INSTANCE_INDETERMINATE_BG_COLOR, getIndeterminateBgColor());
        bundle.putInt(INSTANCE_DETERMINATE_BG_COLOR, getDeterminateBgColor());
        bundle.putInt(INSTANCE_PROGRESS_DETERMINATE_COLOR, getProgressDeterminateColor());
        bundle.putInt(INSTANCE_PROGRESS_INDETERMINATE_COLOR, getProgressIndeterminateColor());
        bundle.putInt(INSTANCE_PROGRESS_MARGIN, getProgressMargin());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mMaxProgress = bundle.getInt(INSTANCE_MAX_PROGRESS);
            mCurrProgress = bundle.getInt(INSTANCE_CURRENT_PROGRESS);
            mCurrState = bundle.getInt(INSTANCE_CURRENT_STATE);
            mPausable = bundle.getBoolean(INSTANCE_PAUSABLE);
            mIdleIconWidth = bundle.getInt(INSTANCE_IDLE_WIDTH);
            mIdleIconHeight = bundle.getInt(INSTANCE_IDLE_HEIGHT);
            mPauseIconWidth = bundle.getInt(INSTANCE_PAUSE_WIDTH);
            mPauseIconHeight = bundle.getInt(INSTANCE_PAUSE_HEIGHT);
            mResumeIconWidth = bundle.getInt(INSTANCE_RESUME_WIDTH);
            mResumeIconHeight = bundle.getInt(INSTANCE_RESUME_HEIGHT);
            mFinishIconWidth = bundle.getInt(INSTANCE_FINISH_WIDTH);
            mFinishIconHeight = bundle.getInt(INSTANCE_FINISH_HEIGHT);
            mIdleBgColor = bundle.getInt(INSTANCE_IDLE_BG_COLOR);
            mFinishBgColor = bundle.getInt(INSTANCE_FINISH_BG_COLOR);
            mResumeBgColor = bundle.getInt(INSTANCE_RESUME_BG_COLOR);
            mIndeterminateBgColor = bundle.getInt(INSTANCE_INDETERMINATE_BG_COLOR);
            mDeterminateBgColor = bundle.getInt(INSTANCE_DETERMINATE_BG_COLOR);
            mProgressDeterminateColor = bundle.getInt(INSTANCE_PROGRESS_DETERMINATE_COLOR);
            mProgressIndeterminateColor = bundle.getInt(INSTANCE_PROGRESS_INDETERMINATE_COLOR);
            mProgressMargin = bundle.getInt(INSTANCE_PROGRESS_MARGIN);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));

            if (mCurrState == STATE_INDETERMINATE) mIndeterminateAnimator.start();

            return;
        }
        super.onRestoreInstanceState(state);
    }

    private void setProgressRectBounds() {
        float halfStroke = mProgressPaint.getStrokeWidth() / 2.0f;
        float totalMargin = mProgressMargin + halfStroke;
        mProgressRect.set(totalMargin, totalMargin, getWidth() - totalMargin, getHeight() - totalMargin);
    }

    private void initIndeterminateAnimator() {
        mIndeterminateAnimator = ValueAnimator.ofInt(0, 360);
        mIndeterminateAnimator.setInterpolator(new LinearInterpolator());
        mIndeterminateAnimator.setDuration(1000);
        mIndeterminateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndeterminateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mIndeterminateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mCurrIndeterminateBarPos = value - BASE_START_ANGLE;
                invalidate();
            }
        });
    }

    private float getDegrees() {
        return ((float)mCurrProgress / (float)mMaxProgress) * 360;
    }

    private void drawDrawableInCenter(Drawable drawable, Canvas canvas, int width, int height) {
        int left = (getWidth() / 2) - (width / 2);
        int top = (getHeight() / 2) - (height / 2);
        drawable.setBounds(left, top, left + width, top + height);
        drawable.draw(canvas);
    }

    public interface OnClickListener {
        void onIdleButtonClick(View view);
        void onResumeButtonClick(View view);
        void onPauseButtonClick(View view);
        void onFinishButtonClick(View view);
    }

    public interface OnStateChangedListener {
        void onStateChanged(int newState);
    }
}

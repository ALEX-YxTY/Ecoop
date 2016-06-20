package com.milai.ecoop.view;

import com.milai.ecoop.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class CountDownView extends TextView {
	private int size;
	private int backgroundColor;
	private int textColor;
	private int colonColor;
	private Rect mBound1;
	private Rect mBound2;
	private Paint mPaint;
	private boolean run = false;
	private long endTime = 0;
	private String HH = "";
	private String MM = "";
	private String SS = "";

	public CountDownView(Context context) {
		this(context, null);
	}

	public CountDownView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CountDownView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountDownView, defStyle, 0);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {

			case R.styleable.CountDownView_size:
				size = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
						getResources().getDisplayMetrics()));
				break;
			case R.styleable.CountDownView_backgroundColor:
				backgroundColor = a.getColor(attr, Color.DKGRAY);
				break;
			case R.styleable.CountDownView_textColor:
				textColor = a.getColor(attr, Color.WHITE);
				break;
			case R.styleable.CountDownView_colonColor:
				colonColor = a.getColor(attr, Color.WHITE);
				break;
			}

		}
		a.recycle();
		String text1 = "00";
		String text2 = ":";
		mPaint = new Paint();
		mPaint.setTextSize(size);
		mBound1 = new Rect();
		mBound2 = new Rect();
		mPaint.getTextBounds("00", 0, text1.length(), mBound1);
		mPaint.getTextBounds(":", 0, text2.length(), mBound2);
	}

	public void setEndTime(long scond) {
		endTime = scond;
		run = true;
		t.start();
	}

	public void stop() {
		run = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int width;
		Rect mBounds = new Rect();
		String s = "00:00:00";
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			mPaint.setTextSize(size);
			mPaint.getTextBounds(s, 0, s.length(), mBounds);
			float textWidth = mBounds.width();
			int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
			width = desired;
		}

		setMeasuredDimension(width * 5 / 3, width * 5 / 12);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setColor(backgroundColor);
		int r = getWidth() / 20;
		RectF ova1 = new RectF(0, 0, getWidth() * 2 / 8, getHeight());
		canvas.drawRoundRect(ova1, r, r, mPaint);
		RectF ova2 = new RectF(getWidth() * 3 / 8, 0, getWidth() * 5 / 8, getHeight());
		canvas.drawRoundRect(ova2, r, r, mPaint);
		RectF ova3 = new RectF(getWidth() * 6 / 8, 0, getWidth(), getHeight());
		canvas.drawRoundRect(ova3, r, r, mPaint);

		mPaint.setColor(textColor);
		canvas.drawText(HH, getWidth() / 8 - mBound1.width() / 2, getHeight() / 2 + mBound1.height() / 2, mPaint);
		canvas.drawText(MM, getWidth() / 2 - mBound1.width() / 2, getHeight() / 2 + mBound1.height() / 2, mPaint);
		canvas.drawText(SS, getWidth() * 7 / 8 - mBound1.width() / 2, getHeight() / 2 + mBound1.height() / 2, mPaint);
		mPaint.setColor(colonColor);
		canvas.drawText(":", getWidth() * 5 / 16 - mBound2.width() / 2, getHeight() / 2 + mBound1.height() / 2, mPaint);
		canvas.drawText(":", getWidth() * 11 / 16 - mBound2.width() / 2, getHeight() / 2 + mBound1.height() / 2,
				mPaint);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			invalidate();
		};
	};
	private Thread t = new Thread(new Runnable() {
		public void run() {
			while (run) {
				long currentSecond = System.currentTimeMillis() / 1000;
				if (endTime - currentSecond >= 0) {
					HH = String.valueOf((endTime - currentSecond) / 60 / 60 % 99);
					if (HH.length() == 1)
						HH = "0" + HH;
					MM = String.valueOf((endTime - currentSecond) / 60 % 60);
					if (MM.length() == 1)
						MM = "0" + MM;
					SS = String.valueOf((endTime - currentSecond) % 60);
					if (SS.length() == 1)
						SS = "0" + SS;
					mHandler.sendEmptyMessage(0);
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
			}
		}
	});

}

package com.milai.ecoop.view;

import java.util.ArrayList;
import java.util.List;

import com.milai.ecoop.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class RollViewPager extends ViewPager {
	private MyPagerAdapter mAdapter;
	private Context context;

	public RollViewPager(Context context) {
		this(context, null);
	}

	public RollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 通知其父控件，现在进行的是本控件的操作，不允许拦截
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			// 通知其父控件，现在进行的是本控件的操作，不允许拦截
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 初始化
	 * */
	private void init() {
		String[] urls = new String[] { "a", "b" ,"c","d","e","f"};
		mAdapter = new MyPagerAdapter(urls);
		setAdapter(mAdapter);
	}

	private class MyPagerAdapter extends PagerAdapter {
		private List<ImageView> images;
		private String[] urls;

		public MyPagerAdapter(String[] urls) {
			this.urls = urls;
			images = new ArrayList<ImageView>();
			for (String url : urls) {
				ImageView iv = new ImageView(context);
				iv.setLayoutParams(new ViewGroup.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				iv.setImageResource(R.drawable.ic_launcher);
				images.add(iv);
			}
		}

		@Override
		public int getCount() {
			Log.d("bh", "getCount");
			return urls.length;
		}
		
		//销毁item时调用的方法
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(images.get(position));
			Log.d("bh","destroyItem");
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			Log.d("bh", "isViewFromObject");
			return arg0 == arg1;
		}

		/**
		 * 创建item时调用的方法
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.d("bh", "instantiateItem");
			container.addView(images.get(position));
			return images.get(position);
		}
	}
}



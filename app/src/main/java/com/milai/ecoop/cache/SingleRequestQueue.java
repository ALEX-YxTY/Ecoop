package com.milai.ecoop.cache;

import java.io.File;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.util.Log;

public class SingleRequestQueue {
	private static RequestQueue mQueue;

	private SingleRequestQueue(Context context) {
		mQueue = Volley.newRequestQueue(context);
	}

	public static synchronized RequestQueue getRequestQueue(Context context){
		if (mQueue == null){
			File cacheDir = new File("/data/data/com.milai.ecoop/cache/volley");
			DiskBasedCache cache = new DiskBasedCache(cacheDir);
			new SingleRequestQueue(context.getApplicationContext());
		}

		return mQueue;
	
	}
}

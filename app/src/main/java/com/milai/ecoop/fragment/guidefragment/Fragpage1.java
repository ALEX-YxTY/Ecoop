package com.milai.ecoop.fragment.guidefragment;





import com.milai.ecoop.R;
import com.milai.ecoop.fragment.FragClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class Fragpage1 extends Fragment{
	private View view;
	private FragClickListener mFraglistener;
	public static Fragpage1 createInstance(FragClickListener l) {
		Fragpage1 f = new Fragpage1();
		f.mFraglistener = l;
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_page1, null);
		return view;
	}
}

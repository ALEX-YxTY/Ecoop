package com.milai.ecoop.fragment.guidefragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.milai.ecoop.R;
import com.milai.ecoop.fragment.FragClickListener;


public class Fragpage2 extends Fragment{
	private View view;
	private FragClickListener mFraglistener;
	public static Fragpage2 createInstance(FragClickListener l) {
		Fragpage2 f = new Fragpage2();
		f.mFraglistener = l;
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_page2, null);
		return view;
	}
}

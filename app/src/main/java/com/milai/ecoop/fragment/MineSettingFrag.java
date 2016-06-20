package com.milai.ecoop.fragment;

import com.milai.ecoop.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MineSettingFrag extends Fragment{
	private View view;
	private FragClickListener mFraglistener;
    public static MineSettingFrag createInstance(FragClickListener l) {
    	MineSettingFrag f = new MineSettingFrag();
		f.mFraglistener = l;
        return f;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.frag_minesetting, null);
		return view;
	}
}

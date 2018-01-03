package com.test.kkl.testing01.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.kkl.testing01.R;
import com.test.kkl.testing01.databinding.ActivityMain2Binding;
import com.test.kkl.testing01.databinding.ActivityMainBinding;
import com.test.kkl.testing01.databinding.Fragment2Binding;


/**
 * Created by Kuo Liang on 25-Oct-17.
 */

public class Fragment2 extends Fragment {

    public static final int ID = "VideoEditFragment".hashCode();

    Fragment2Binding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_2, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("kkl", "onDestroy: " + Fragment2.class.getName());
        mBinding = null;
    }
}

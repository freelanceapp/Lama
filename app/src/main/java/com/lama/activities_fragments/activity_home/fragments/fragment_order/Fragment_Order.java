package com.lama.activities_fragments.activity_home.fragments.fragment_order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lama.R;
import com.lama.activities_fragments.activity_home.HomeActivity;
import com.lama.adapters.ViewPagerOrderAdapter;
import com.lama.databinding.FragmentOrderBinding;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Fragment_Order extends Fragment {

    private HomeActivity activity;
    private FragmentOrderBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private ViewPagerOrderAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> title;

    public static Fragment_Order newInstance() {
        return new Fragment_Order();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);
        initView();
        return binding.getRoot();
    }


    private void initView() {
        fragmentList = new ArrayList<>();
        title = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        binding.tab.setupWithViewPager(binding.pager);
        binding.pager.setOffscreenPageLimit(2);

        fragmentList.add(Fragment_Current_Order.newInstance());
        fragmentList.add(Fragment_Previous_Order.newInstance());
        title.add(getString(R.string.current));
        title.add(getString(R.string.prevoius));
        adapter = new ViewPagerOrderAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(fragmentList);
        adapter.addTitles(title);
        binding.pager.setAdapter(adapter);

    }

}

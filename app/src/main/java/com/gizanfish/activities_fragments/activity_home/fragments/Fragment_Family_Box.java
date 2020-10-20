package com.gizanfish.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gizanfish.R;
import com.gizanfish.activities_fragments.activity_checkout.CheckoutActivity;
import com.gizanfish.activities_fragments.activity_home.HomeActivity;
import com.gizanfish.activities_fragments.activity_order_details.OrderDetailsActivity;
import com.gizanfish.adapters.CartAdapter;
import com.gizanfish.adapters.FamilyBoxesAdapter;
import com.gizanfish.adapters.MostSellerAdapter;
import com.gizanfish.adapters.Swipe;
import com.gizanfish.databinding.FragmentFamilyBoxBinding;
import com.gizanfish.models.ItemCartModel;
import com.gizanfish.models.OrderModel;
import com.gizanfish.models.ProductDataModel;
import com.gizanfish.models.SettingModel;
import com.gizanfish.models.SingleProductDataModel;
import com.gizanfish.models.UserModel;
import com.gizanfish.preferences.Preferences;
import com.gizanfish.remote.Api;
import com.gizanfish.share.Common;
import com.gizanfish.singleton.CartSingleton;
import com.gizanfish.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Family_Box extends Fragment {

    private HomeActivity activity;
    private FragmentFamilyBoxBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private LinearLayoutManager manager;
    private List<SingleProductDataModel> familyBoxesList;
    private FamilyBoxesAdapter familyBoxesAdapter;
    public static Fragment_Family_Box newInstance() {
        return new Fragment_Family_Box();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_family_box, container, false);
        initView();
        getFamilyBoxes();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void initView() {
        activity = (HomeActivity) getActivity();
        familyBoxesList=new ArrayList<>();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        binding.recView.setLayoutManager(new GridLayoutManager(activity,2));
        familyBoxesAdapter = new FamilyBoxesAdapter(familyBoxesList, activity, this);
        binding.recView.setAdapter(familyBoxesAdapter);


    }

    public void getFamilyBoxes() {

        try {

            Api.getService(Tags.base_url).
                    getFamilyBoxes("off").
                    enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                familyBoxesList.clear();
                                familyBoxesList.addAll(response.body().getData());
                                if (familyBoxesList.size() > 0) {
                                    familyBoxesAdapter.notifyDataSetChanged();
                                } else {

                                }

                            } else {
                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
                            binding.progBar.setVisibility(View.GONE);
                            try {
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }


                        }
                    });
        } catch (Exception e) {

        }


    }


}

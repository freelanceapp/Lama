package com.lama.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lama.R;
import com.lama.activities_fragments.activity_home.HomeActivity;
import com.lama.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.lama.adapters.OffersAdapter;
import com.lama.databinding.FragmentSearchBinding;
import com.lama.models.MainCategoryDataModel;
import com.lama.models.ProductDataModel;
import com.lama.models.SingleProductDataModel;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;
import com.lama.remote.Api;
import com.lama.tags.Tags;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Search extends Fragment {

    private HomeActivity activity;
    private FragmentSearchBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private List<MainCategoryDataModel.Data> mainDepartmentsList;
    private List<SingleProductDataModel> offersDataList;
    private OffersAdapter offersAdapter;
    private String department_id = "all";

    public static Fragment_Search newInstance() {
        return new Fragment_Search();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        initView();
        getCategory();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {
        mainDepartmentsList = new ArrayList<>();
        offersDataList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);

        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recViewOffer.setLayoutManager(new LinearLayoutManager(activity));
        offersAdapter = new OffersAdapter(offersDataList, activity, this);
        binding.recViewOffer.setAdapter(offersAdapter);
        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (mainDepartmentsList.size()>0){
                    MainCategoryDataModel.Data categoryModel = mainDepartmentsList.get(pos);
                    department_id = categoryModel.getId() + "";
                    getOffersProducts();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void getCategory() {

       /* Api.getService(Tags.base_url)
                .getCategory("off")
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            updateTabUI(response.body());

                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
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
                    public void onFailure(Call<MainCategoryDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });*/
    }

    public void getOffersProducts() {
        offersDataList.clear();
        offersAdapter.notifyDataSetChanged();
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);


        /*try {
            int uid;*/
/*
            if (userModel != null) {
                uid = userModel.getUser().getId();
            } else {
                uid = 0;
            }*/
           /* Api.getService(Tags.base_url).
                    getOffersProducts("off", department_id, "", "all").
                    enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                offersDataList.clear();
                                offersDataList.addAll(response.body().getData());
                                if (offersDataList.size() > 0) {
                                    offersAdapter.notifyDataSetChanged();
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }

                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

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
                            binding.tvNoData.setVisibility(View.VISIBLE);
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
*/

    }


   /* public void setItemDataOffers(SingleProductDataModel model) {

        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("product_id", model.getId());
        startActivityForResult(intent, 100);
    }

*/
/*
    private void updateTabUI(MainCategoryDataModel data) {
        mainDepartmentsList.clear();
        mainDepartmentsList.addAll(data.getData());

        for (MainCategoryDataModel.Data categoryModel1 : data.getData()) {

            binding.tab.addTab(binding.tab.newTab().setText(categoryModel1.getTitle()));


        }

        new Handler().postDelayed(() -> binding.tab.getTabAt(0).select(), 100);


    }
*/
}

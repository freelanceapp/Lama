package com.lama.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lama.R;
import com.lama.activities_fragments.activity_home.HomeActivity;
import com.lama.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.lama.adapters.FamilyBoxesAdapter;
import com.lama.databinding.FragmentMyReservationsBinding;
import com.lama.models.ProductDataModel;
import com.lama.models.SingleProductDataModel;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;
import com.lama.remote.Api;
import com.lama.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_My_Reservations extends Fragment {

    private HomeActivity activity;
    private FragmentMyReservationsBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private LinearLayoutManager manager;
    private List<SingleProductDataModel> familyBoxesList;
    private FamilyBoxesAdapter familyBoxesAdapter;
    public static Fragment_My_Reservations newInstance() {
        return new Fragment_My_Reservations();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_reservations, container, false);
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

    public void setItemDataOffers(SingleProductDataModel model) {

        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("product_id", model.getId());
        startActivityForResult(intent, 100);
    }
}

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
import androidx.recyclerview.widget.RecyclerView;

import com.lama.R;
import com.lama.activities_fragments.activity_home.HomeActivity;
import com.lama.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.lama.adapters.FamilyBoxesAdapter;
import com.lama.adapters.OrderAdapter;
import com.lama.databinding.FragmentMyReservationsBinding;
import com.lama.models.OrderDataModel;
import com.lama.models.OrderModel;
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
    private OrderAdapter adapter;
    private List<OrderModel> orderModelList;
    private int current_page = 1;
    private boolean isLoading = false;
    public static Fragment_My_Reservations newInstance() {
        return new Fragment_My_Reservations();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_reservations, container, false);
        initView();
     //   getFamilyBoxes();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void initView() {
        activity = (HomeActivity) getActivity();
        orderModelList=new ArrayList<>();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);
        binding.recView.setLayoutManager(new GridLayoutManager(activity,2));
        adapter = new OrderAdapter(activity, orderModelList, this);
        binding.recView.setAdapter(adapter);

        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total_item = binding.recView.getAdapter().getItemCount();
                    int last_visible_item = ((LinearLayoutManager) binding.recView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                    if (total_item >= 20 && (total_item - last_visible_item) == 5 && !isLoading) {

                        isLoading = true;
                        int page = current_page + 1;
                        orderModelList.add(null);
                        adapter.notifyItemInserted(orderModelList.size() - 1);

                        loadMore(page);
                    }
                }
            }
        });
        getOrders();


    }

    private void getOrders() {
        try {
            current_page = 1;
            Api.getService(Tags.base_url)
                    .getOrders(userModel.getUser().getToken(), userModel.getUser().getId()+"", "on", current_page, 20)
                    .enqueue(new Callback<OrderDataModel>() {
                        @Override
                        public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                orderModelList.clear();
                                orderModelList.addAll(response.body().getData());
                                Log.e("mmmmmmm",orderModelList.size()+"");

                                if (orderModelList.size() > 0) {

                                    adapter.notifyDataSetChanged();

                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

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

    private void loadMore(int page) {
        try {

            Api.getService(Tags.base_url)
                    .getOrders(userModel.getUser().getToken(), userModel.getUser().getId()+"", "on", page, 20)
                    .enqueue(new Callback<OrderDataModel>() {
                        @Override
                        public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
                            isLoading = false;
                            orderModelList.remove(orderModelList.size() - 1);
                            adapter.notifyItemRemoved(orderModelList.size() - 1);


                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                int oldPos = orderModelList.size() - 1;

                                orderModelList.addAll(response.body().getData());

                                if (response.body().getData().size() > 0) {
                                    current_page = response.body().getMeta().getCurrent_page();
                                    adapter.notifyItemRangeChanged(oldPos, orderModelList.size() - 1);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderDataModel> call, Throwable t) {
                            try {

                                if (orderModelList.get(orderModelList.size() - 1) == null) {
                                    isLoading = false;
                                    orderModelList.remove(orderModelList.size() - 1);
                                    adapter.notifyItemRemoved(orderModelList.size() - 1);

                                }

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

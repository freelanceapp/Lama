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
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.tabs.TabLayout;
import com.lama.R;
import com.lama.activities_fragments.activity_home.HomeActivity;
import com.lama.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.lama.adapters.CategoryAdapter;
import com.lama.adapters.MostSellerAdapter;
import com.lama.adapters.OffersAdapter;
import com.lama.adapters.SlidingImage_Adapter;
import com.lama.databinding.FragmentMainBinding;
import com.lama.models.MainCategoryDataModel;
import com.lama.models.ProductDataModel;
import com.lama.models.SingleProductDataModel;
import com.lama.models.Slider_Model;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;
import com.lama.remote.Api;
import com.lama.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Fragment_Main extends Fragment {
    private HomeActivity activity;
    private FragmentMainBinding binding;
    private Preferences preferences;
    private String lang;
    private TimerTask timerTask;
    private Timer timer;
    private int current_page = 0, NUM_PAGES;
    private LinearLayoutManager manager;
    private boolean isLoading = false;
    private UserModel userModel;
    private SlidingImage_Adapter slidingImage__adapter;
    private List<MainCategoryDataModel.Data> mainDepartmentsList;
    private List<SingleProductDataModel> offersDataList;
    private OffersAdapter offersAdapter;
    private String category_id = "all";
    private CategoryAdapter categoryAdapter;

    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        get_slider();
        change_slide_image();
        getCategory();
    }

    private void initView() {
        offersDataList = new ArrayList<>();
        mainDepartmentsList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");

        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBar.setVisibility(View.GONE);
        categoryAdapter = new CategoryAdapter(mainDepartmentsList, this, activity);

        binding.recViewdepart.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));

        binding.recViewdepart.setAdapter(categoryAdapter);

        offersAdapter = new OffersAdapter(offersDataList, activity, this);

        binding.recView.setLayoutManager(manager);
        binding.recView.setAdapter(offersAdapter);

        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total_item = binding.recView.getAdapter().getItemCount();
                    int last_visible_item = ((LinearLayoutManager) binding.recView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                    if (total_item >= 20 && (total_item - last_visible_item) == 5 && !isLoading) {
                        Log.e("kldkkdkdk", "dkkdkkdkdk");
                        isLoading = true;
                        int page = current_page + 1;
                        offersDataList.add(null);
                        offersAdapter.notifyItemInserted(offersDataList.size() - 1);

                        loadMore(page);
                    }
                }
            }
        });

        getCategory();
        getOffersProducts();

    }

    private void get_slider() {

        Api.getService(Tags.base_url).get_slider().enqueue(new Callback<Slider_Model>() {
            @Override
            public void onResponse(Call<Slider_Model> call, Response<Slider_Model> response) {
                binding.progBarSlider.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    if (response.body().getData().size() > 0) {
                        NUM_PAGES = response.body().getData().size();
                        slidingImage__adapter = new SlidingImage_Adapter(activity, response.body().getData());
                        binding.pager.setAdapter(slidingImage__adapter);

                    } else {

                        binding.pager.setVisibility(View.GONE);
                    }
                } else if (response.code() == 404) {
                    binding.pager.setVisibility(View.GONE);
                } else {
                    binding.pager.setVisibility(View.GONE);
                    try {
                        Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Slider_Model> call, Throwable t) {
                try {
                    binding.progBarSlider.setVisibility(View.GONE);
                    binding.pager.setVisibility(View.GONE);

                    Log.e("Error", t.getMessage());

                } catch (Exception e) {

                }

            }
        });

    }

    private void change_slide_image() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (current_page == NUM_PAGES) {
                    current_page = 0;
                }
                binding.pager.setCurrentItem(current_page++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    public void setItemDataOffers(SingleProductDataModel model) {

        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("product_id", model.getId());
        startActivityForResult(intent, 100);
    }

    private void getOffersProducts() {
        try {
            offersDataList.clear();
            // reDataList = new ArrayList<>();
            offersAdapter.notifyDataSetChanged();
            // binding.tvNoData.setVisibility(View.GONE);
            binding.progBar.setVisibility(View.VISIBLE);
            current_page = 1;
            Api.getService(Tags.base_url)
                    .getOffersProducts("on", category_id + "", "", "all", "20", current_page)
                    .enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                                offersDataList.addAll(response.body().getData());
                                if (offersDataList.size() > 0) {
                                    //   Log.e("lllll", reDataList.size() + "");
                                    offersAdapter.notifyDataSetChanged();

                                    //  binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    //  binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    // binding.tvNoData.setVisibility(View.VISIBLE);
                                    //   Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
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
            Log.e("flfllflfl", e.toString());
        }
    }

    private void loadMore(int page) {


        try {

            Api.getService(Tags.base_url)
                    .getOffersProducts("on", category_id + "", "", "all", "20", page)
                    .enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            isLoading = false;
                            if (offersDataList.get(offersDataList.size() - 1) == null) {
                                offersDataList.remove(offersDataList.size() - 1);
                                offersAdapter.notifyItemRemoved(offersDataList.size() - 1);
                            }

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {

                                int oldPos = offersDataList.size() - 1;

                                offersDataList.addAll(response.body().getData());

                                if (response.body().getData().size() > 0) {
                                    // current_page = response.body().getCurrent_page();
                                    offersAdapter.notifyItemRangeChanged(oldPos, offersDataList.size() - 1);

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
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
                            try {

                                if (offersDataList.get(offersDataList.size() - 1) == null) {
                                    isLoading = false;
                                    offersDataList.remove(offersDataList.size() - 1);
                                    offersAdapter.notifyItemRemoved(offersDataList.size() - 1);

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

    private void getCategory() {
        Api.getService(Tags.base_url)
                .getCategory("off")
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBarCategory.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            mainDepartmentsList.clear();
                            mainDepartmentsList.addAll(response.body().getData());

                            if (mainDepartmentsList.size() > 0) {
                                categoryAdapter.notifyDataSetChanged();
//                                binding.tvNoDatadepart.setVisibility(View.GONE);
                                Log.e(",dkdfkfkkfk", mainDepartmentsList.get(0).getTitle());
                            } else {
//                                binding.tvNoDatadepart.setVisibility(View.VISIBLE);

                            }


                        } else {
                            binding.progBarCategory.setVisibility(View.GONE);

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
                            binding.progBarCategory.setVisibility(View.GONE);

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
                });
    }

    public void setitemData(String id) {

        category_id = id;
        getOffersProducts();
    }


}

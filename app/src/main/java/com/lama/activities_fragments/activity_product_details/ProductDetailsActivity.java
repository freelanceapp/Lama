package com.lama.activities_fragments.activity_product_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

//import com.anaqaphone.Animate.CircleAnimationUtil;
import com.lama.R;
import com.lama.activities_fragments.activity_images.ImagesActivity;
import com.lama.adapters.ProductDetialsSlidingImage_Adapter;
import com.lama.databinding.ActivityProductDetailsBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;

import com.lama.models.ItemCartModel;
import com.lama.models.SingleProductDataModel;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;
import com.lama.remote.Api;
import com.lama.share.Common;
import com.lama.singleton.CartSingleton;
import com.lama.tags.Tags;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProductDetailsBinding binding;
    private String lang;
    private SingleProductDataModel productDataModel;
    private String product_id;
    private Preferences preferences;
    private UserModel userModel;
    private ProductDetialsSlidingImage_Adapter slidingImage__adapter;
    private CartSingleton cartSingleton;
    private SingleProductDataModel singleProductDataModel;
    private CartSingleton singleton;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        getDataFromIntent();
        initView();
        getOrder();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            product_id = intent.getIntExtra("product_id", 0) + "";

        }

    }


    private void initView() {
        Paper.init(this);
        cartSingleton = CartSingleton.newInstance();
        preferences = Preferences.getInstance();
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        binding.setModel(productDataModel);
        binding.tab.setupWithViewPager(binding.pager);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        binding.descriptions.setOnClickListener(v -> {
            binding.descriptions.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.descriptions.setTextColor(getResources().getColor(R.color.white));
            binding.preparationWay.setBackgroundColor(getResources().getColor(R.color.gray2));
            binding.preparationWay.setTextColor(getResources().getColor(R.color.black));
            binding.tvData.setText(singleProductDataModel.getContents());
        });
        binding.preparationWay.setOnClickListener(v -> {
            binding.descriptions.setBackgroundColor(getResources().getColor(R.color.gray2));
            binding.descriptions.setTextColor(getResources().getColor(R.color.black));
            binding.preparationWay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.preparationWay.setTextColor(getResources().getColor(R.color.white));
            binding.tvData.setText(singleProductDataModel.getFeatures());

        });
        binding.imageDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(binding.tvAmount.getText().toString()) > 1) {
                    binding.tvAmount.setText((Integer.parseInt(binding.tvAmount.getText().toString()) - 1) + "");
                }
            }
        });
        binding.imageIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.tvAmount.setText((Integer.parseInt(binding.tvAmount.getText().toString()) + 1) + "");

            }
        });
        binding.flAddToCart.setOnClickListener(v -> addToCart(singleProductDataModel));


    }

    public void addToCart(SingleProductDataModel singleProductDataModel) {
        if (cartSingleton.getItemCartModelList() != null && cartSingleton.getItemCartModelList().size() > 0) {
            int postion = -1;

            for (int i = 0; i < cartSingleton.getItemCartModelList().size(); i++) {
                ItemCartModel itemCartModel = cartSingleton.getItemCartModelList().get(i);
                //  Log.e("fllflfl", color_id + " " + itemCartModel.getPrice_id());

                if (product_id.equals(itemCartModel.getProduct_id() + "")) {
                    postion = i;
                    break;
                }
            }
            if (postion > -1) {
                ItemCartModel itemCartModel = cartSingleton.getItemCartModelList().get(postion);
                itemCartModel.setAmount(itemCartModel.getAmount() + Integer.parseInt(binding.tvAmount.getText().toString()));
                itemCartModel.setPrice(itemCartModel.getAmount() * singleProductDataModel.getPrice());
                cartSingleton.deleteItem(postion);
                cartSingleton.addItem(itemCartModel);
//                    if (binding.expandLayout.isExpanded()) {
//                        binding.expandLayout.collapse(true);
//                    } else {
//                        binding.expandLayout.expand(true);
//                    }
                Toast.makeText(this, getResources().getString(R.string.add_to_cart), Toast.LENGTH_SHORT).show();
            } else {
                ItemCartModel itemCartModel = new ItemCartModel(product_id, singleProductDataModel.getTitle(), singleProductDataModel.getPrice(), Integer.parseInt(binding.tvAmount.getText().toString()), singleProductDataModel.getImage());
                cartSingleton.addItem(itemCartModel);

                Toast.makeText(this, getResources().getString(R.string.add_to_cart), Toast.LENGTH_SHORT).show();
            }
        } else {
            ItemCartModel itemCartModel = new ItemCartModel(product_id, singleProductDataModel.getTitle(), singleProductDataModel.getPrice(), Integer.parseInt(binding.tvAmount.getText().toString()), singleProductDataModel.getImage());
            cartSingleton.addItem(itemCartModel);

            Toast.makeText(this, getResources().getString(R.string.add_to_cart), Toast.LENGTH_SHORT).show();
        }
        binding.setCartcount(cartSingleton.getItemCartModelList().size());
    }


    private void getOrder() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url)
                    .Product_detials(product_id)
                    .enqueue(new Callback<SingleProductDataModel>() {
                        @Override
                        public void onResponse(Call<SingleProductDataModel> call, Response<SingleProductDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                UPDATEUI(response.body());
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleProductDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void UPDATEUI(SingleProductDataModel body) {

        binding.setModel(body);
        try {
            binding.tvtitle.setText(Jsoup.parse(body.getTitle()).text());
            binding.tvData.setText(Jsoup.parse(body.getContents()).text());

        } catch (Exception e) {
            binding.tvtitle.setText(body.getTitle());
            binding.tvData.setText(singleProductDataModel.getContents());

            Log.e("kskskks", e.toString());
        }
        this.singleProductDataModel = body;
        binding.progBarSlider.setVisibility(View.GONE);
        slidingImage__adapter = new ProductDetialsSlidingImage_Adapter(this, body.getProducts_images());
        binding.pager.setAdapter(slidingImage__adapter);

    }


    @Override
    public void back() {
        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
//            createOrderModel = preferences.getCartData(this);
//            if (createOrderModel == null) {
//                createOrderModel = new CreateOrderModel();
//                createOrderModel.setMarkter_id(market.getId());
//                binding.setCartCount(0);
//                isDataAdded = true;
//
//            } else {
//
//                binding.setCartCount(createOrderModel.getProducts().size());
//            }
//        }
        }
    }

    public void show() {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("data", singleProductDataModel);
        startActivityForResult(intent, 100);
    }


    public void updateCartCount(int count) {
        binding.setCartcount(count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        singleton = CartSingleton.newInstance();
        if (singleton.getItemCartModelList() != null) {
            updateCartCount(singleton.getItemCount());
        }

    }
}
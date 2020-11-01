  package com.lama.activities_fragments.activity_product_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lama.R;
import com.lama.activities_fragments.activity_images.ImagesActivity;
import com.lama.activities_fragments.activity_reservation.ReservationActivity;
import com.lama.adapters.ProductDetialsSlidingImage_Adapter;
import com.lama.databinding.ActivityProductDetailsBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;
import com.lama.models.SingleProductDataModel;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;
import com.lama.remote.Api;
import com.lama.share.Common;
import com.lama.singleton.CartSingleton;
import com.lama.tags.Tags;
import java.io.IOException;
import java.util.Locale;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements Listeners.BackListener , OnMapReadyCallback {
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
    private double lat = 0.0, lng = 0.0;
    private GoogleMap mMap;
    private Marker marker;
    private float zoom = 15.0f;

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
        updateUI();


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
        preferences = Preferences.getInstance();
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(productDataModel);
        binding.tab.setupWithViewPager(binding.pager);
        binding.progBarSlider.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailsActivity.this, ReservationActivity.class);
                startActivity(intent);
            }
        });

        binding.tvOldprice.setPaintFlags(binding.tvOldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }

    private void updateUI() {

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);


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
        this.singleProductDataModel = body;
        binding.progBarSlider.setVisibility(View.GONE);
        slidingImage__adapter = new ProductDetialsSlidingImage_Adapter(this, body.getProducts_images());
        binding.pager.setAdapter(slidingImage__adapter);
        Log.e("eeee",singleProductDataModel.getLatitude()+"  ----"+singleProductDataModel.getLongitude());
        if(mMap!=null){
            Log.e("eeee",singleProductDataModel.getLatitude()+"  ----"+singleProductDataModel.getLongitude());

            AddMarker(singleProductDataModel.getLatitude(),singleProductDataModel.getLongitude());
    }}


    @Override
    public void back() {
        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }

    public void show() {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra("data", singleProductDataModel);
        startActivityForResult(intent, 100);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {


        if (googleMap != null) {
            mMap = googleMap;
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(ProductDetailsActivity.this, R.raw.maps));
            mMap.setTrafficEnabled(false);
            mMap.setBuildingsEnabled(false);
            mMap.setIndoorEnabled(true);
        /*    Log.e("eeee",productDataModel.getLatitude()+"  ----"+productDataModel.getLongitude());
            AddMarker(productDataModel.getLatitude(), productDataModel.getLongitude());
*/
            if(singleProductDataModel!=null){
                AddMarker(singleProductDataModel.getLatitude(), singleProductDataModel.getLongitude());
            }

        }
    }


    private void AddMarker(double lat, double lng) {

        this.lat = lat;
        this.lng = lng;

        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        } else {
            marker.setPosition(new LatLng(lat, lng));


        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
    }

}

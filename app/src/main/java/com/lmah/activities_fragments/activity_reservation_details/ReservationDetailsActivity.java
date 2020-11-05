package com.lmah.activities_fragments.activity_reservation_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.lmah.R;
import com.lmah.databinding.ActivityReservationDetailsBinding;
import com.lmah.interfaces.Listeners;
import com.lmah.language.Language;
import com.lmah.models.Create_Order_Model;
import com.lmah.models.SingleProductDataModel;
import com.lmah.models.UserModel;
import com.lmah.preferences.Preferences;
import com.lmah.remote.Api;
import com.lmah.share.Common;
import com.lmah.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityReservationDetailsBinding binding;
    private String lang;
    private String firstdate, seconddate;
    private SingleProductDataModel productDataModel;
    private int size;
    private UserModel userModel;
    private Preferences preferences;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reservation_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            firstdate = intent.getStringExtra("firstdate");
            seconddate = intent.getStringExtra("seconeddate");
            size = intent.getIntExtra("size", 0);
            productDataModel = (SingleProductDataModel) intent.getSerializableExtra("product");


        }
    }


    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        //  Log.e("dldlldl",productDataModel.getAddress());
        binding.setModel(productDataModel);
        binding.setFirstdate(firstdate);
        binding.setSeconeddate(seconddate);
        binding.setSize((double) size);
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendorder();
            }
        });

    }

    private void sendorder() {
        Create_Order_Model create_order_model = new Create_Order_Model();
        create_order_model.setPay_type("cash");
        create_order_model.setUser_id(userModel.getUser().getId() + "");
        create_order_model.setTotal_price(Double.parseDouble(binding.tvTotal.getText().toString().replace(getResources().getString(R.string.ryal), "")));
        Create_Order_Model.OrderDetails orderDetails = new Create_Order_Model.OrderDetails();
        orderDetails.setBook_date_from(firstdate);
        orderDetails.setBook_date_to(seconddate);
        orderDetails.setPrice(create_order_model.getTotal_price());
        orderDetails.setProduct_id(productDataModel.getId());
        List<Create_Order_Model.OrderDetails> orderDetailsList = new ArrayList<>();
        orderDetailsList.add(orderDetails);
        create_order_model.setProducts(orderDetailsList);
        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url).accept_orders(userModel.getUser().getToken(), create_order_model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                dialog.dismiss();
                if (response.isSuccessful()) {

// Common.CreateSignAlertDialog(activity, getResources().getString(R.string.sucess));

                    //  activity.refresh(Send_Data.getType());
                    Toast.makeText(ReservationDetailsActivity.this, getResources().getString(R.string.suc), Toast.LENGTH_LONG).show();

                    finish();
                } else {
                    Common.CreateDialogAlert(ReservationDetailsActivity.this, getString(R.string.failed));

                    try {
                        Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    dialog.dismiss();
                    Toast.makeText(ReservationDetailsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error", t.getMessage());
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void back() {
        finish();
    }

}

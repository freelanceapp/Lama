package com.lama.activities_fragments.activity_reservation_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.lama.R;
import com.lama.databinding.ActivityReservationBinding;
import com.lama.databinding.ActivityReservationDetailsBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;
import com.lama.models.SingleProductDataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ReservationDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityReservationDetailsBinding binding;
    private String lang;
    private String firstdate, seconddate;
    private SingleProductDataModel productDataModel;
    private int size;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
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
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
      //  Log.e("dldlldl",productDataModel.getAddress());
        binding.setModel(productDataModel);
        binding.setFirstdate(firstdate);
        binding.setSeconeddate(seconddate);
        binding.setSize((double) size);


    }

    @Override
    public void back() {
        finish();
    }

}

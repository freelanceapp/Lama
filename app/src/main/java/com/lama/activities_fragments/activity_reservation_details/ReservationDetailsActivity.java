package com.lama.activities_fragments.activity_reservation_details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.lama.R;
import com.lama.databinding.ActivityReservationBinding;
import com.lama.databinding.ActivityReservationDetailsBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ReservationDetailsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityReservationDetailsBinding binding;
    private String lang;
    private List<Calendar> calendarList;





    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
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
        if (intent!=null&&intent.hasExtra("type"))
        {
           // type = intent.getIntExtra("type",1);

        }
    }


    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);


    }

    @Override
    public void back() {
        finish();
    }

}

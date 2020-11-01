package com.lama.activities_fragments.activity_reservation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.lama.R;
import com.lama.activities_fragments.activity_reservation_details.ReservationDetailsActivity;
import com.lama.databinding.ActivityReservationBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ReservationActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityReservationBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);
        initView();
    }

    private void initView()
    {
        calendarList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        Calendar calendar = Calendar.getInstance();
        binding.calendarViewRangeMonth.setMinimumDate(calendar);

        binding.btnConfirm.setOnClickListener(view -> {

            Intent intent=new Intent(ReservationActivity.this, ReservationDetailsActivity.class);
            startActivity(intent);


        });


    }





    @Override
    public void back() {
        finish();
    }

}

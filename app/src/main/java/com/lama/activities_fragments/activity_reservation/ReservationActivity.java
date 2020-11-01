package com.lama.activities_fragments.activity_reservation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.lama.R;
import com.lama.activities_fragments.activity_reservation_details.ReservationDetailsActivity;
import com.lama.databinding.ActivityReservationBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;
import com.lama.models.SingleProductDataModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ReservationActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityReservationBinding binding;
    private String lang;
    private List<Calendar> calendarList;

    private SingleProductDataModel productDataModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            // type = intent.getIntExtra("type",1);
            productDataModel = (SingleProductDataModel) intent.getSerializableExtra("product");


        }
    }

    private void initView() {
        calendarList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 11, 3);
        calendarList.add(calendar);
        //binding.calendarViewRangeMonth.setMinimumDate(calendar);
        //  binding.calendarViewRangeMonth.setSelectedDates(calendarList);
        binding.calendarViewRangeMonth.setDisabledDays(calendarList);


        binding.btnConfirm.setOnClickListener(view -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Intent intent = new Intent(ReservationActivity.this, ReservationDetailsActivity.class);
            intent.putExtra("firstdate", dateFormat.format(binding.calendarViewRangeMonth.getSelectedDates().get(0).getTime()));
            intent.putExtra("seconeddate", dateFormat.format(binding.calendarViewRangeMonth.getSelectedDates().get(binding.calendarViewRangeMonth.getSelectedDates().size() - 1).getTime()));
            intent.putExtra("product", productDataModel);
            intent.putExtra("size", binding.calendarViewRangeMonth.getSelectedDates().size());
            startActivity(intent);


        });


    }


    @Override
    public void back() {
        finish();
    }

}

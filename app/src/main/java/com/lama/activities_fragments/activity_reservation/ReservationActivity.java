package com.lama.activities_fragments.activity_reservation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.lama.R;
import com.lama.activities_fragments.activity_reservation_details.ReservationDetailsActivity;
import com.lama.databinding.ActivityReservationBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;
import com.lama.models.SingleProductDataModel;
import com.lama.models.TimesModel;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;
import com.lama.remote.Api;
import com.lama.share.Common;
import com.lama.tags.Tags;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityReservationBinding binding;
    private String lang;
    private List<Calendar> calendarList;

    private SingleProductDataModel productDataModel;
    private Preferences preferences;
    private UserModel userModel;
    SimpleDateFormat dateFormat;
    private ProgressDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reservation);
        getDataFromIntent();
        initView();
        gettimes();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            // type = intent.getIntExtra("type",1);
            productDataModel = (SingleProductDataModel) intent.getSerializableExtra("product");


        }
    }

    public void gettimes() {
        dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url).getTimes(userModel.getUser().getToken(), productDataModel.getId() + "").enqueue(new Callback<TimesModel>() {
            @Override
            public void onResponse(Call<TimesModel> call, Response<TimesModel> response) {

                //     dialog.dismiss();
                if (response.isSuccessful()) {
                    // Common.CreateSignAlertDialog(activity, getResources().getString(R.string.sucess));
                    update(response.body());

                } else {
                    dialog.dismiss();
                    Common.CreateDialogAlert(ReservationActivity.this, getString(R.string.failed));

                    try {
                        Log.e("Error_code", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<TimesModel> call, Throwable t) {
                try {
                    dialog.dismiss();
                    Toast.makeText(ReservationActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                    Log.e("Error", t.getMessage());
                } catch (Exception e) {
                }
            }
        });
    }

    private void update(TimesModel body) {
        for (int i = 0; i < body.getDates().size(); i++) {
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(dateFormat.parse(body.getDates().get(i)));
            } catch (Exception e) {
            }
            calendarList.add(calendar);
            //   Toast.makeText(ReservationActivity.this, calendar.getTime().toString(), Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();

        binding.calendarViewRangeMonth.setDisabledDays(calendarList);
        binding.calendarViewRangeMonth.refreshDrawableState();


    }


    private void initView() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        calendarList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);


        //binding.calendarViewRangeMonth.setMinimumDate(calendar);
        //  binding.calendarViewRangeMonth.setSelectedDates(calendarList);
        binding.calendarViewRangeMonth.setDisabledDays(calendarList);
        binding.calendarViewRangeMonth.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                if (binding.calendarViewRangeMonth.getSelectedDates().size() > 0) {
                    for (int i = 0; i < calendarList.size(); i++) {
                        if ((calendarList.get(i).getTimeInMillis() < eventDay.getCalendar().getTimeInMillis() && calendarList.get(i).getTimeInMillis() > binding.calendarViewRangeMonth.getSelectedDates().get(0).getTimeInMillis()) || (calendarList.get(i).getTimeInMillis() < binding.calendarViewRangeMonth.getSelectedDates().get(binding.calendarViewRangeMonth.getSelectedDates().size() - 1).getTimeInMillis() && calendarList.get(i).getTimeInMillis() > eventDay.getCalendar().getTimeInMillis())) {
                            Toast.makeText(ReservationActivity.this, getResources().getString(R.string.unavaliable), Toast.LENGTH_LONG).show();
                            binding.calendarViewRangeMonth.setSelectedDates(new ArrayList<>());
                            binding.calendarViewRangeMonth.setSelected(false);
                            //eventDay.setEnabled(false);
                            break;
                        }
                    }
                }
            }

        });


//        binding.calendarViewRange"dkkdkMonth.setSelectedDates(calendarList);

        binding.btnConfirm.setOnClickListener(view -> {
            if (binding.calendarViewRangeMonth.getSelectedDates().size() > 0) {
                Intent intent = new Intent(ReservationActivity.this, ReservationDetailsActivity.class);
                intent.putExtra("firstdate", dateFormat.format(binding.calendarViewRangeMonth.getSelectedDates().get(0).getTime()));
                intent.putExtra("seconeddate", dateFormat.format(binding.calendarViewRangeMonth.getSelectedDates().get(binding.calendarViewRangeMonth.getSelectedDates().size() - 1).getTime()));
                intent.putExtra("product", productDataModel);
                intent.putExtra("size", binding.calendarViewRangeMonth.getSelectedDates().size());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getResources().getString(R.string.ch_date), Toast.LENGTH_LONG).show();
            }


        });


    }


    @Override
    public void back() {
        finish();
    }

}

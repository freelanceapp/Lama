package com.lama.activities_fragments.activity_reservation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.lama.R;
import com.lama.databinding.ActivityReservationBinding;
import com.lama.interfaces.Listeners;
import com.lama.language.Language;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

class ReservationActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityReservationBinding binding;
    private String lang;
    private int type;
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
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null&&intent.hasExtra("type"))
        {
            type = intent.getIntExtra("type",1);

        }
    }


    private void initView()
    {
        calendarList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);




        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH,1);

        binding.calendarView1DayContinuous.setMinimumDate(calendar);
        try {
            binding.calendarView3DayContinuous.setDate(calendar2);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        binding.calendarView3DayContinuous.setMinimumDate(calendar);
        try {
            binding.calendarView3DayContinuous.setDate(calendar2);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        binding.calendarViewMoreDay.setMinimumDate(calendar);
        try {
            binding.calendarViewMoreDay.setDate(calendar2);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        binding.calendarViewRangeWeek.setMinimumDate(calendar);
        try {
            binding.calendarViewRangeWeek.setDate(calendar2);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        binding.calendarViewRangeMonth.setMinimumDate(calendar);
        try {
            binding.calendarViewRangeMonth.setDate(calendar2);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }


        binding.btnConfirm.setOnClickListener(view -> {

            if (type ==1)
            {
                calendarList.clear();

                calendarList.addAll(binding.calendarView3DayContinuous.getSelectedDates());
                if (calendarList.size()>0)
                {
                    int isFriday = isFriday(calendarList);

                    if (isFriday == -1)
                    {


                        if (calendarList.size()==3)
                        {
                            Intent intent = getIntent();
                            intent.putExtra("day", (Serializable) calendarList);
                            setResult(RESULT_OK,intent);
                            finish();

                        }else
                        {
                        //    Toast.makeText(this, R.string.ch_max_day, Toast.LENGTH_SHORT).show();

                        }


                    }else
                    {


                        calendarList.remove(isFriday);
                        if (calendarList.size()==3)
                        {
                          //  Toast.makeText(this, R.string.friday_deleted, Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            intent.putExtra("day", (Serializable) calendarList);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else
                        {
                        //    Toast.makeText(this, R.string.friday_not, Toast.LENGTH_SHORT).show();

                          //  Toast.makeText(this, R.string.ch_max_day, Toast.LENGTH_SHORT).show();

                        }

                    }


                }else
                {
                   // Toast.makeText(this, R.string.ch_day2, Toast.LENGTH_SHORT).show();
                }
            }else if (type ==2)
            {

                calendarList.clear();
                calendarList.addAll(binding.calendarView1DayContinuous.getSelectedDates());

                if (calendarList.size()>0)
                {
                    int isFriday = isFriday(calendarList);
                    if (isFriday ==-1)
                    {

                        Intent intent = getIntent();
                        intent.putExtra("day", (Serializable) calendarList);
                        setResult(RESULT_OK,intent);
                        finish();


                    }else
                    {

                     //   Toast.makeText(this, R.string.friday_not, Toast.LENGTH_SHORT).show();


                    }


                }else
                {
                  //  Toast.makeText(this, R.string.ch_day2, Toast.LENGTH_SHORT).show();
                }

                /*if (calendarList.size()>0)
                {
                    int isFriday = isFriday(calendarList);
                    if (isFriday ==-1)
                    {

                        if (calendarList.size()==3)
                        {

                            Intent intent = getIntent();
                            intent.putExtra("day", (Serializable) calendarList);
                            setResult(RESULT_OK,intent);
                            finish();

                        }else
                        {
                            Toast.makeText(this, R.string.ch_max_day, Toast.LENGTH_SHORT).show();

                        }


                    }else
                    {

                        calendarList.remove(isFriday);
                        if (calendarList.size()==3)
                        {
                            Toast.makeText(this, R.string.friday_deleted, Toast.LENGTH_SHORT).show();

                            Intent intent = getIntent();
                            intent.putExtra("day", (Serializable) calendarList);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else
                        {
                            Toast.makeText(this, R.string.friday_not, Toast.LENGTH_SHORT).show();

                            Toast.makeText(this, R.string.ch_max_day, Toast.LENGTH_SHORT).show();

                        }

                    }


                }else
                {
                    Toast.makeText(this, R.string.ch_day2, Toast.LENGTH_SHORT).show();
                }*/
            }else if (type ==3)
            {

                calendarList.clear();
                calendarList.addAll(binding.calendarViewRangeWeek.getSelectedDates());

                if (calendarList.size()>0)
                {

                    int isFriday = isFriday(calendarList);

                    if (isFriday==-1)
                    {
                        if (calendarList.size()==7)
                        {
                            Intent intent = getIntent();
                            intent.putExtra("day", (Serializable) calendarList);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else
                        {
                          //  Toast.makeText(this, R.string.max_7day, Toast.LENGTH_SHORT).show();

                        }

                    }else
                    {


                        calendarList.remove(isFriday);
                        if (calendarList.size()==7)
                        {
                            //Toast.makeText(this, R.string.friday_deleted, Toast.LENGTH_SHORT).show();

                            Intent intent = getIntent();
                            intent.putExtra("day", (Serializable) calendarList);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else
                        {
                           // Toast.makeText(this, R.string.friday_not, Toast.LENGTH_SHORT).show();

                         //   Toast.makeText(this, R.string.max_7day, Toast.LENGTH_SHORT).show();

                        }


                    }


                }else
                {
                   // Toast.makeText(this, R.string.ch_day2, Toast.LENGTH_SHORT).show();
                }
            }else if (type ==4)
            {

                calendarList.clear();
                calendarList.addAll(binding.calendarViewRangeMonth.getSelectedDates());

                if (calendarList.size()>0)
                {

                    List<Calendar> calendars = removeFriday();
                    calendarList.clear();
                    calendarList.addAll(calendars);


                    if (calendarList.size()==26)
                    {
                     //   Toast.makeText(this, R.string.friday_deleted, Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        intent.putExtra("day", (Serializable) calendarList);
                        setResult(RESULT_OK,intent);
                        finish();
                    }else
                    {
                       // Toast.makeText(this, R.string.max_26day, Toast.LENGTH_SHORT).show();

                    }




                }else
                {
                   // Toast.makeText(this, R.string.ch_day2, Toast.LENGTH_SHORT).show();
                }
            }

        });


    }


    private int isFriday(List<Calendar> calendarList)
    {
        int isFriday = -1;

        for (int i=0;i<calendarList.size();i++)
        {
            int d1  = calendarList.get(i).get(Calendar.DAY_OF_WEEK);

            if (d1 == Calendar.FRIDAY)
            {
                isFriday =i;
                break;
            }
        }

        return isFriday;
    }

    private List<Calendar> removeFriday()
    {

        List<Calendar> calendars = new ArrayList<>();

        for (Calendar calendar:calendarList)
        {
            int d1  = calendar.get(Calendar.DAY_OF_WEEK);

            if (d1!=Calendar.FRIDAY)
            {
                calendars.add(calendar);
            }

        }

        return calendars;
    }

    @Override
    public void back() {
        finish();
    }

}

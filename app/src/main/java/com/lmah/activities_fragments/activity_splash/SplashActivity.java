package com.lmah.activities_fragments.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.lmah.R;
import com.lmah.activities_fragments.activity_home.HomeActivity;
import com.lmah.activities_fragments.activity_login.LoginActivity;
import com.lmah.databinding.ActivitySplashBinding;
import com.lmah.language.Language;
import com.lmah.models.UserModel;
import com.lmah.preferences.Preferences;


import java.util.Locale;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private Preferences preferences;
    private UserModel userModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        new Handler()
                .postDelayed(()->{
                    if (userModel==null){
                        Intent intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },2000);

    }
}

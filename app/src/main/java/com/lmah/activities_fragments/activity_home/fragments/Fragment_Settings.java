package com.lmah.activities_fragments.activity_home.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.lmah.R;
import com.lmah.activities_fragments.activity_about_app.AboutAppActivity;
import com.lmah.activities_fragments.activity_bepartener.BePartenerActivity;
import com.lmah.activities_fragments.activity_edit_profile.EditProfileActivity;
import com.lmah.activities_fragments.activity_home.HomeActivity;
import com.lmah.activities_fragments.activity_language.LanguageActivity;
import com.lmah.databinding.FragmentSettingsBinding;
import com.lmah.interfaces.Listeners;
import com.lmah.models.DefaultSettings;
import com.lmah.models.SettingModel;
import com.lmah.models.UserModel;
import com.lmah.preferences.Preferences;
import com.lmah.remote.Api;
import com.lmah.share.Common;
import com.lmah.tags.Tags;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.BuildConfig;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Fragment_Settings extends Fragment implements Listeners.SettingActions {

    private HomeActivity activity;
    private FragmentSettingsBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private SettingModel settingmodel;
    private DefaultSettings defaultSettings;

    public static Fragment_Settings newInstance() {

        return new Fragment_Settings();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getAppData();

    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        defaultSettings = preferences.getAppSetting(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setActions(this);
        binding.tvVersion.setText(BuildConfig.VERSION_NAME);


        if (defaultSettings!=null){
            if (defaultSettings.getRingToneName()!=null&&!defaultSettings.getRingToneName().isEmpty()){
                binding.tvRingtoneName.setText(defaultSettings.getRingToneName());
            }else {
                binding.tvRingtoneName.setText(getString(R.string.default1));
            }
        }else {
            binding.tvRingtoneName.setText(getString(R.string.default1));

        }
    }

    private void getAppData() {

        Api.getService(Tags.base_url)
                .getSetting(lang)
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.isSuccessful() && response.body() != null) {

                                settingmodel = response.body();

                            } else {
                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (response.code() == 500) {
                                    Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }

    @Override
    public void terms() {
        Intent intent=new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type",2);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
        Intent intent=new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type",1);
        startActivity(intent);
    }

    @Override
    public void share() {

    }

    @Override
    public void onEditProfile() {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        intent.putExtra("data",preferences.getUserData(activity));
        startActivityForResult(intent,2);
    }

    @Override
    public void onLanguageSetting() {
        Intent intent = new Intent(activity, LanguageActivity.class);
        intent.putExtra("type",1);
        startActivity(intent);
    }



    @Override
    public void onPrivacy() {
        Intent intent=new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type",3);
        startActivity(intent);
    }

    @Override
    public void onRate() {
        String appId = activity.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        final List<ResolveInfo> otherApps = activity.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rateIntent.setComponent(componentName);
                startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            startActivity(webIntent);
        }
    }

    @Override
    public void onTone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        startActivityForResult(intent, 100);
    }

    @Override
    public void about() {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    @Override
    public void logout() {
        if (userModel != null) {
            activity.logout();
        } else {
            Common.CreateDialogAlert(activity, getString(R.string.please_sign_in_or_sign_up));
        }
    }


    @Override
    public void bepartener() {
        Intent intent = new Intent(activity, BePartenerActivity.class);
        startActivity(intent);
    }

    @Override
    public void facebook() {
        if (settingmodel!=null)
        {
            if (settingmodel.getSettings().getFacebook()!=null)
            {
                String pattern = "https?://.+\\..{2,}";
                if (settingmodel.getSettings().getFacebook().matches(pattern))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(settingmodel.getSettings().getFacebook()));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

            }


        }else {
            Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void google() {
        if (settingmodel!=null)
        {
            if (settingmodel.getSettings().getGoogle_plus()!=null)
            {
                String pattern = "https?://.+\\..{2,}";
                if (settingmodel.getSettings().getGoogle_plus().matches(pattern))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(settingmodel.getSettings().getGoogle_plus()));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

            }


        }else {
            Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void instgram() {
        if (settingmodel!=null)
        {
            if (settingmodel.getSettings().getInstagram()!=null)
            {
                String pattern = "https?://.+\\..{2,}";
                if (settingmodel.getSettings().getInstagram().matches(pattern))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(settingmodel.getSettings().getInstagram()));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

            }


        }else {
            Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void twitter() {
        if (settingmodel!=null)
        {
            if (settingmodel.getSettings().getTwitter()!=null)
            {
                String pattern = "https?://.+\\..{2,}";
                if (settingmodel.getSettings().getTwitter().matches(pattern))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(settingmodel.getSettings().getTwitter()));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

            }


        }else {
            Toast.makeText(activity, R.string.inv_url, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);


            if (uri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(activity,uri);
                String name = ringtone.getTitle(activity);
                binding.tvRingtoneName.setText(name);

                if (defaultSettings==null){
                    defaultSettings = new DefaultSettings();
                }

                defaultSettings.setRingToneUri(uri.toString());
                defaultSettings.setRingToneName(name);
                preferences.createUpdateAppSetting(activity,defaultSettings);


            }
        } else if (requestCode == 200 && resultCode == RESULT_OK ) {

            activity.setResult(RESULT_OK);
            //finish();
        }
    }
}
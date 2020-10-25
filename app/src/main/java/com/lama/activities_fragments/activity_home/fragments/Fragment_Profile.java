package com.lama.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.lama.R;
import com.lama.activities_fragments.activity_about_app.AboutAppActivity;
import com.lama.activities_fragments.activity_edit_profile.EditProfileActivity;
import com.lama.activities_fragments.activity_home.HomeActivity;
import com.lama.databinding.FragmentProfileBinding;
import com.lama.interfaces.Listeners;
import com.lama.models.SettingModel;
import com.lama.models.UserModel;
import com.lama.preferences.Preferences;
import com.lama.remote.Api;
import com.lama.share.Common;
import com.lama.tags.Tags;


import java.io.IOException;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Profile extends Fragment implements Listeners.SettingActions {

    private HomeActivity activity;
    private FragmentProfileBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private SettingModel settingmodel;

    public static Fragment_Profile newInstance() {

        return new Fragment_Profile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
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
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setAction(this);
        binding.setModel(userModel);

    }




    @Override
    public void terms() {
        Intent intent = new Intent(activity, AboutAppActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
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

    }

    @Override
    public void onPrivacy() {

    }

    @Override
    public void onRate() {

    }

    @Override
    public void onTone() {

    }

    @Override
    public void about() {

    }



    private void getAppData() {

        Api.getService(Tags.base_url)
                .getSetting(lang)
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            settingmodel = response.body();

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {

                            } else {


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void ViewSocial(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        startActivity(intent);

    }
}

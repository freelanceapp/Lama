package com.lama.interfaces;

public interface Listeners {
    interface BackListener {
        void back();
    }

    interface LoginListener {
        void validate();

        void showCountryDialog();
    }

    interface SignUpListener {

        void openSheet();

        void closeSheet();

        void checkDataValid();

        void checkReadPermission();

        void checkCameraPermission();
    }

    interface SettingActions {

        void editProfile();
        void terms();

        void aboutApp();

        void logout();

        void share();

        void whatsapp();


    }


    interface PaymentTypeAction {
        void onCredit();

        void onPaypal();

        void onCash();

        void onNext();

        void onPrevious();
    }


    interface NextPreviousAction {
        void onNext();

        void onPrevious();
    }

    interface UpdateProfileListener {
        void updateProfile();
    }


}

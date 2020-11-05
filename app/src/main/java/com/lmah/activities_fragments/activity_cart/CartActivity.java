package com.lmah.activities_fragments.activity_cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lmah.R;

import com.lmah.activities_fragments.activity_checkout.CheckoutActivity;
import com.lmah.activities_fragments.activity_order_details.OrderDetailsActivity;
import com.lmah.adapters.CartAdapter;
import com.lmah.adapters.Swipe;
import com.lmah.databinding.ActivityCartBinding;
import com.lmah.interfaces.Listeners;
import com.lmah.language.Language;
import com.lmah.models.ItemCartModel;
import com.lmah.models.OrderModel;
import com.lmah.models.UserModel;
import com.lmah.preferences.Preferences;
import com.lmah.share.Common;
import com.lmah.singleton.CartSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity implements Listeners.BackListener, Swipe.SwipeListener {
    private ActivityCartBinding binding;
    private String lang;
    private LinearLayoutManager manager;
    private CartSingleton singleton;
    private CartAdapter adapter;
    private List<ItemCartModel> itemCartModelList;
    private Preferences preferences;
    private UserModel userModel;
    private double total = 0;
    private boolean isDataChanged = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        initView();
    }


    private void initView() {
        itemCartModelList = new ArrayList<>();

        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        singleton = CartSingleton.newInstance();
        itemCartModelList.addAll(singleton.getItemCartModelList());
        manager = new LinearLayoutManager(this);
        binding.recView.setLayoutManager(manager);
        adapter = new CartAdapter(itemCartModelList, this);
       // binding.recView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new Swipe(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(binding.recView);

        binding.btnCheckout.setOnClickListener(view -> navigateToCheckoutActivity());
        updateUI();
//        Log.e("llll",settingmodel.getSettings().getDelivery_value()+"");


    }

    private void navigateToCheckoutActivity() {
        if (userModel != null) {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("total_cost", total);


            startActivityForResult(intent, 100);
        } else {
            Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
        }
    }


    public void updateUI() {

        /*if (singleton==null){
            singleton = CartSingleton.newInstance();

        }*/

        binding.recView.setAdapter(adapter);
        itemCartModelList.clear();
        itemCartModelList.addAll(singleton.getItemCartModelList());

        Log.e("lllll", itemCartModelList.size() + "");
        adapter.notifyDataSetChanged();
        if (itemCartModelList.size() == 0) {
            // Log.e("lllllss", itemCartModelList.size() + "");

            binding.consTotal.setVisibility(View.GONE);
            binding.llEmptyCart.setVisibility(View.VISIBLE);
            binding.tvTotal.setText("");

        } else {
            binding.consTotal.setVisibility(View.VISIBLE);
            binding.llEmptyCart.setVisibility(View.GONE);


        }
        calculateTotal();
        //updateCartCount(itemCartModelList.size());

    }

    private void calculateTotal() {
        total = 0;
        for (ItemCartModel model : itemCartModelList) {

            total += model.getAmount() * model.getPrice();

        }

        binding.tvTotal.setText(String.format(Locale.ENGLISH, "%s %s", String.valueOf(total), getString(R.string.sar)));
    }

    @Override
    public void onSwipe(int pos, int dir) {
        CreateCartDeleteDialog(pos);
    }

    private void CreateCartDeleteDialog(final int pos) {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
        TextView tvDelete = view.findViewById(R.id.tvDelete);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(v -> {
            adapter.notifyDataSetChanged();
            dialog.dismiss();

        });

        tvDelete.setOnClickListener(v -> {
            try {
                itemCartModelList.remove(pos);
                singleton.deleteItem(pos);
                adapter.notifyItemRemoved(pos);
                updateUI();
                //  calculateTotal();
                if (itemCartModelList.size() == 0) {
                    //  binding.llCheckout.setVisibility(View.GONE);
                    binding.llEmptyCart.setVisibility(View.VISIBLE);
                    //binding.llTotal.setVisibility(View.GONE);

                }
            } catch (Exception e) {
            }

            dialog.dismiss();
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();

    }


    @Override
    public void back() {
        if (isDataChanged) {
            Log.e("gg", "yyy");
            setResult(RESULT_OK);
        }
        finish();
    }

    public void increase_decrease(ItemCartModel model2, int adapterPosition) {
        itemCartModelList.set(adapterPosition, model2);
        calculateTotal();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            OrderModel orderModel = (OrderModel) data.getSerializableExtra("data");
            navigateToOrderDetailsActivity(orderModel);
          //  preferences.u(this, null);
            updateUI();
            finish();
        }
    }

    private void navigateToOrderDetailsActivity(OrderModel orderModel) {
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("data", orderModel);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        back();
    }
}


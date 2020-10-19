package com.gizanfish.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.gizanfish.R;
import com.gizanfish.activities_fragments.activity_home.HomeActivity;
import com.gizanfish.activities_fragments.activity_home.fragments.Fragment_Department;
import com.gizanfish.activities_fragments.activity_home.fragments.fragment_order.Fragment_Order;
import com.gizanfish.databinding.MainCategoryRowBinding;
import com.gizanfish.models.MainCategoryDataModel;

import java.util.List;

import io.paperdb.Paper;

public class MainCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MainCategoryDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private HomeActivity activity;
    private String lang;
    private Fragment fragment;
    private Fragment_Order fragment_order;
    private Fragment_Department fragment_department;
    private int i = -1;

    public MainCategoryAdapter(List<MainCategoryDataModel.Data> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
        inflater = LayoutInflater.from(context);
        activity = (HomeActivity) context;
        Paper.init(context);
        lang = Paper.book().read("lang", "ar");


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        MainCategoryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_category_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        MainCategoryDataModel.Data mainDepartments = list.get(position);

        myHolder.binding.cardView.setCardBackgroundColor(Color.parseColor(mainDepartments.getBackground()));
        myHolder.binding.setModel(mainDepartments);
        myHolder.binding.setLang(lang);
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = position;
                notifyDataSetChanged();
            }
        });
        if (i == position) {
            if (fragment instanceof Fragment_Order) {
                fragment_order = (Fragment_Order) fragment;
               // fragment_order.setDepartment(list.get(holder.getLayoutPosition()).getId() + "");
            } else if (fragment instanceof Fragment_Department) {
                fragment_department = (Fragment_Department) fragment;
                //fragment_department.setDepartment(list.get(holder.getLayoutPosition()).getId() + "");
            }
            myHolder.binding.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.second));
        } else {
            myHolder.binding.cardView.setCardBackgroundColor(Color.parseColor(mainDepartments.getBackground()));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public MainCategoryRowBinding binding;

        public MyHolder(@NonNull MainCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}

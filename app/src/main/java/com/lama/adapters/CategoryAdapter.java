package com.lama.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lama.R;
import com.lama.activities_fragments.activity_home.HomeActivity;
import com.lama.activities_fragments.activity_home.fragments.Fragment_Main;
import com.lama.databinding.MainCategoryRowBinding;
import com.lama.models.MainCategoryDataModel;


import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MainCategoryDataModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment_Main fragment_main;
    public CategoryAdapter(List<MainCategoryDataModel.Data> list,Fragment_Main fragment_main, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment_main=fragment_main;


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
///        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(view -> {
            Log.e("sssss",list.get(holder.getLayoutPosition()).getId()+"");

            fragment_main.setitemData(list.get(holder.getLayoutPosition()).getId()+"");
        });
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

package com.lmah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.lmah.R;
import com.lmah.activities_fragments.activity_home.fragments.Fragment_Search;
import com.lmah.activities_fragments.activity_home.fragments.Fragment_My_Reservations;
import com.lmah.activities_fragments.activity_home.fragments.Fragment_Main;
import com.lmah.activities_fragments.activity_search.SearchActivity;
import com.lmah.databinding.FamilyBoxRowBinding;
import com.lmah.models.SingleProductDataModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class FamilyBoxesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String lang;
    private List<SingleProductDataModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private int i = -1;

    public FamilyBoxesAdapter(List<SingleProductDataModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FamilyBoxRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.family_box_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.itemView.setOnClickListener(view -> {
                if (fragment instanceof Fragment_Main) {

                    Fragment_Main fragment_main = (Fragment_Main) fragment;
             //       fragment_main.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
                }

                else if (fragment instanceof Fragment_My_Reservations) {
                    Fragment_My_Reservations fragment_order = (Fragment_My_Reservations) fragment;
//                    fragment_order.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
                } else if (fragment instanceof Fragment_Search) {
                    Fragment_Search fragment_search = (Fragment_Search) fragment;
                   // fragment_search.setItemDataOffers(list.get(myHolder.getAdapterPosition()));
                } else if (context instanceof SearchActivity) {
                    SearchActivity searchActivity = (SearchActivity) context;
                    searchActivity.setItemDataOffers(list.get(myHolder.getAdapterPosition()));

                }
            });



        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public FamilyBoxRowBinding binding;

        public MyHolder(@NonNull FamilyBoxRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }



}

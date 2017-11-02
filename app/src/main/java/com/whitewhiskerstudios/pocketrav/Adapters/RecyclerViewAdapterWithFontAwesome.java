package com.whitewhiskerstudios.pocketrav.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;

import java.util.ArrayList;

/**
 * Created by rachael on 10/20/17.
 */

public class RecyclerViewAdapterWithFontAwesome extends RecyclerView.Adapter<RecyclerViewAdapterWithFontAwesome.DataViewHolder>{

    private ArrayList<CardData> dataList;
    private Context context;
    private static MyClickListener myClickListener;

    public RecyclerViewAdapterWithFontAwesome(ArrayList<CardData> dl) {
        this.dataList = dl;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(DataViewHolder viewHolder, int i) {
        CardData cardData = dataList.get(i);
        viewHolder.tv_top.setText(cardData.tv_top);
        viewHolder.tv_bottom.setText(cardData.tv_bottom);
        viewHolder.iconTextView.setText(cardData.image);
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview_with_icontextview, viewGroup, false);

        context = viewGroup.getContext();
        return new DataViewHolder(itemView);
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView tv_top;
        protected TextView tv_bottom;
        protected IconTextView iconTextView;

        public DataViewHolder(View v){
            super(v);
            tv_top = (TextView) v.findViewById(R.id.tv_top);
            tv_bottom = (TextView) v.findViewById(R.id.tv_bottom);
            iconTextView = (IconTextView) v.findViewById(R.id.iconTextView);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public void updateList(ArrayList<CardData> list){
        if (list != null && list.size() > 0){
            dataList.clear();
            dataList.addAll(list);
            notifyDataSetChanged();
        }
    }
}

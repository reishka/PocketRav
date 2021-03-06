package com.whitewhiskerstudios.pocketrav.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Utils.CardData;

import java.util.ArrayList;

/**
 * Created by rachael on 10/6/17.
 */

public class RecyclerViewAdapterWithPicasso extends RecyclerView.Adapter<RecyclerViewAdapterWithPicasso.DataViewHolder>{

    private ArrayList<CardData> dataList;
    private Context context;
    private static MyClickListener myClickListener;

    public RecyclerViewAdapterWithPicasso(ArrayList<CardData> dl) {
        this.dataList = dl;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(DataViewHolder viewHolder, int i) {
        CardData cardData = dataList.get(i);
        viewHolder.tv_top.setText(cardData.getTv_top());
        viewHolder.tv_bottom.setText(cardData.getTv_bottom());

        if (!cardData.getImage().equals(""))
            Picasso.with(context).load(cardData.getImage()).into(viewHolder.imageView);
        else
            Picasso.with(context).load(R.drawable.placeholder200).into(viewHolder.imageView);

    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview_with_imageview, viewGroup, false);

        context = viewGroup.getContext();
        return new DataViewHolder(itemView);
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView tv_top;
        protected TextView tv_bottom;
        protected ImageView imageView;

        public DataViewHolder(View v){
            super(v);
            tv_top = (TextView) v.findViewById(R.id.tv_top);
            tv_bottom = (TextView) v.findViewById(R.id.tv_bottom);
            imageView = (ImageView) v.findViewById(R.id.photo);
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
}

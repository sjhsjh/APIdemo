package com.example.apidemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.apidemo.R;
import java.util.List;

/**
 * <br>
 * Created by jinhui.shao on 2017/4/11.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_ITEM = 0;
    private final static int TYPE_HEADER = 1;
    private final static int TYPE_FOOTER = 2;
    private List<Integer> mItemDatas;

    public RecycleViewAdapter(List<Integer> itemDatas){
        mItemDatas = itemDatas;
    }

    @Override
    public int getItemViewType(int position) {
        NLog.w("sjh0", "getItemViewType");
        if(position == 0){
            return TYPE_HEADER;
        }
        else if(position == getItemCount() - 1){
            return TYPE_FOOTER;
        }
        else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NLog.w("sjh0", "onCreateViewHolder:");

        if(viewType == TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_header, parent, false);
            HeaderViewHolder headerViewHolder = new HeaderViewHolder(view);
            return headerViewHolder;
        }
        else if(viewType == TYPE_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleviewfooter, parent, false);
            FooterViewHolder footerViewHolder = new FooterViewHolder(view);
            return footerViewHolder;
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NLog.w("sjh0", "onBindViewHolder:");

        if(holder instanceof ItemViewHolder) {
            if(position - 1 < mItemDatas.size()){
                ((ItemViewHolder)holder).textview.setText(mItemDatas.get(position - 1).toString());
                holder.itemView.setTag(position);
            }
        }else if(holder instanceof HeaderViewHolder){

        }else if(holder instanceof FooterViewHolder){

        }

    }

    @Override
    public int getItemCount() {
        NLog.w("sjh0", "getItemCount:");
        return mItemDatas.size() + 2;
     }

    public void setData(List<Integer> itemDatas){
        mItemDatas = itemDatas;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textview;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textview = (TextView) itemView.findViewById(R.id.itemview);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mHeaderTips;
        private ImageView mLoadingIcon;

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView mFootererTips;
        private ImageView mLoadingIcon;

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }



}
package com.example.apidemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;
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

//    @Override
//    public int getCount() {
//        return mItemDatas.size() + 2;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        NLog.i("sjh1", "getItem position = " + position);
//        return mItemDatas.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        NLog.i("sjh1", "getItemId position = " + position);
//        return position;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 3;
//    }
//
//    /**
//     * @param position
//     * @return
//     */
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_HEADER;
//        } else if (position == getCount() - 1) {
//            return TYPE_FOOTER;
//        }
//        else {
//            return TYPE_ITEM;
//        }
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        NLog.i("sjh1", "getView position = " + position + " convertView = " + convertView);
//        ItemViewHolder itemViewHolder = new ItemViewHolder();
//
//        if (convertView == null) {
//            if (position == 0) {
//                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_header, parent, false);
//                itemViewHolder.textview = ((TextView)convertView.findViewById(R.id.header_tips));
//            } else if (position == getCount() - 1) {
//                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleviewfooter, parent, false);
//                itemViewHolder.textview = ((TextView)convertView.findViewById(R.id.footer_tips));
//            } else {
//                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
//                itemViewHolder.textview = ((TextView)convertView.findViewById(R.id.itemview));
//            }
//            convertView.setTag(itemViewHolder);
//        }else {
//            itemViewHolder = (ItemViewHolder)convertView.getTag();
//        }
//
//        if(position > 0 && position < getCount() - 1 && itemViewHolder.textview != null){
//            itemViewHolder.textview.setText(mItemDatas.get(position - 1).toString());
//        }
//
//        return convertView;
//    }

    @Override
    public int getItemViewType(int position) {
        NLog.i("sjh0", "getItemViewType  position = " + position);
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
        NLog.w("sjh0", "onCreateViewHolder");
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
        NLog.w("sjh0", "onBindViewHolder: position = " + position);
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
        NLog.i("sjh0", "getItemCount");
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView mHeaderTips;
        public ImageView mLoadingIcon;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mHeaderTips = (TextView) itemView.findViewById(R.id.header_tips);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView mFootererTips;
        private ImageView mLoadingIcon;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mFootererTips = (TextView) itemView.findViewById(R.id.footer_tips);
        }
    }



}
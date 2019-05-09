package com.example.apidemo.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;


public class DemoAdapter extends BaseAdapter {
	private static final boolean DEBUG = false;
	Context mContext;
	List<Object> mData;
	
	public DemoAdapter(Context context, List<Object> data) {
		this.mContext = context;
		mData = data;
	}

	@Override
	public int getCount() { //size为0也行，只是Listview中没有条目
		// NLog.d("sjh0", "getCount");
		return mData.size();
	}

	@Override
	public Object getItem(int position) { // onItemClick中的getItemAtPosition才会调这里。Listview加载时不调这里
//		NLog.i("sjh0", "getItem position = " + position);
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// NLog.w("sjh0", "getView position = " + position);
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
			holder.textView = (TextView) convertView.findViewById(R.id.itemview);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(((Class<?>)getItem(position)).getSimpleName()); // getclass得“class”？

		return convertView;
	}
	
	
	private static class ViewHolder{
		TextView textView;
	}

}

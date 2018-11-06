package com.hbck.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

/**
 * @Date 2018-11-06.
 */
public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private List<PoiInfo> list;

    public MyAdapter(Context mContext, List<PoiInfo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_address, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_name.setText(list.get(i).name);
        holder.tv_address.setText(list.get(i).address);
        return view;
    }

    static class ViewHolder {
        public TextView tv_name;
        public TextView tv_address;

        public ViewHolder(View view) {
            tv_name = view.findViewById(R.id.tv_name);
            tv_address = view.findViewById(R.id.tv_address);
        }
    }
}

package com.gc.networkinglesson.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gc.networkinglesson.R;
import com.gc.networkinglesson.models.JSONRedditItem;

/**
 * Created by terry on 10/31/13.
 */
public class RedditItemAdapter extends ArrayAdapter<JSONRedditItem> {
    public static class ViewHolder {
        TextView title;
    }

    public RedditItemAdapter(Context context) {
        super(context, R.layout.reddit_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONRedditItem item = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.reddit_list_item, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(item.title);

        return convertView;
    }
}

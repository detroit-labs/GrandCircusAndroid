package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.networkinglesson.R;
import com.gc.networkinglesson.models.JSONRedditItem;

/**
 * Created by terry on 11/1/13.
 */
public class RedditItemFragment extends Fragment {

    private String selfText;

    public static RedditItemFragment newInstance(JSONRedditItem item) {
        RedditItemFragment fragment = new RedditItemFragment();
        Bundle args = new Bundle();
        args.putString("selftext", item.selftext);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selfText = getArguments().getString("selftext");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reddit_item, container, false);
        TextView txt = (TextView) rootView.findViewById(R.id.txt_selftext);
        String text = getArguments().getString("selftext");
        txt.setText(text);
        return rootView;
    }
}
package fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gc.networkinglesson.R;
import com.gc.networkinglesson.adapters.RedditItemAdapter;
import com.gc.networkinglesson.models.JSONRedditItem;
import com.gc.networkinglesson.models.JSONSubreddit;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by terry on 10/31/13.
 */
public class SubRedditListFragment extends ListFragment {

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        public void onSubRedditItemSelected(JSONRedditItem redditItem);
    }

    private static final String REDDIT_BASE_URL = "http://www.reddit.com";


    private ArrayList<JSONRedditItem> currentList;

    public static SubRedditListFragment newInstance(String url) {
        SubRedditListFragment fragment = new SubRedditListFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    public SubRedditListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement SubRedditListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (checkNetwork()) {
            fetchSubreddit(getArguments().getString("url"));
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_no_network)
                    .setMessage(R.string.message_no_network)
                    .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reddit_list, container, false);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        listener.onSubRedditItemSelected(currentList.get(position));
    }

    private boolean checkNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void fetchSubreddit(String url) {
        if (url.lastIndexOf("/") == url.length()-1) {
            url = url.substring(0, url.length()-1);
        }
        try {
            getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
            new HttpGetTask().execute(new URL(REDDIT_BASE_URL + url + ".json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void updateList(String jsonSrc) {
        JSONSubreddit subReddit = new JSONSubreddit(jsonSrc);
        currentList = subReddit.getItemList();

        RedditItemAdapter adapter = new RedditItemAdapter(getActivity());
        setListAdapter(adapter);
        adapter.addAll(currentList);
    }

        /*
        *
        * ASYNC TASK FOR GETTING A SUBREDDIT
        * */

    public class HttpGetTask extends AsyncTask<URL, Integer, String> {

        StringBuffer result;

        @Override
        protected String doInBackground(URL... params) {
            URL targetURL = params[0];
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) targetURL.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                result = new StringBuffer();
                String line = reader.readLine();
                result.append(line);

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return (String) result.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
            updateList(s);
        }
    }
}
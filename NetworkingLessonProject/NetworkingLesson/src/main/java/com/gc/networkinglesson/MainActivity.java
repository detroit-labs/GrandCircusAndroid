package com.gc.networkinglesson;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;

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

import fragments.RedditItemFragment;
import fragments.StartupFragment;
import fragments.SubRedditListFragment;

public class MainActivity extends Activity implements SubRedditListFragment.OnItemSelectedListener {

    private static final String REDDIT_BASE_URL = "http://www.reddit.com";
    private static final String ANDROID_SUBREDDIT = "/r/android";
    private static final String SUBREDDITS = "/subreddits";

    private Spinner subredditSelector;
    private ArrayList<JSONRedditItem> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StartupFragment())
                    .commit();
        }

        subredditSelector = (Spinner) findViewById(R.id.spnr_select_subreddit);
        subredditSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JSONRedditItem redditItem = currentList.get(position);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, SubRedditListFragment.newInstance(redditItem.url))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fetchSubreddit(SUBREDDITS);
    }

    @Override
    public void onSubRedditItemSelected(JSONRedditItem redditItem) {
        Fragment useFragment = null;
        useFragment = RedditItemFragment.newInstance(redditItem);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, useFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateList(String jsonSrc) {
        JSONSubreddit subReddit = new JSONSubreddit(jsonSrc);
        currentList = subReddit.getItemList();

        RedditItemAdapter adapter = new RedditItemAdapter(this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subredditSelector.setAdapter(adapter);
        adapter.addAll(currentList);

    }

       /*
        *
        * ASYNC TASK FOR GETTING A SUBREDDIT
        *
        * this is exactly the same Async Task used in the fragment
        * It is here in this activity for this lesson, but in a future
        * lesson we will move this task into a service where both the
        * activity and fragment can use it.  Keep your code DRY!
        *
        * */

    private void fetchSubreddit(String url) {
        if (url.lastIndexOf("/") == url.length()-1) {
            url = url.substring(0, url.length()-1);
        }
        try {
            this.setProgressBarIndeterminateVisibility(Boolean.TRUE);
            new HttpGetTask().execute(new URL(REDDIT_BASE_URL + url + ".json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

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
            setProgressBarIndeterminateVisibility(Boolean.FALSE);
            updateList(s);
        }
    }
}

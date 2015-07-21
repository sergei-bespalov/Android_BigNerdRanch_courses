package com.bespalov.sergey.photogallery;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bespalov.sergey.photogallery.model.FlickrFetchr;
import com.bespalov.sergey.photogallery.model.GalleryItem;
import com.bespalov.sergey.photogallery.model.TumbnailDownloader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends VisibleFragment {
    public static final String TAG = "PhotoGalleryFragment";
    TumbnailDownloader<ImageView> mTumbnailThread;
    private GridView mGridView;
    private ArrayList<GalleryItem> mItems;
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //save fragment
        setRetainInstance(true);
        setHasOptionsMenu(true);

        updateItems();

        mTumbnailThread = new TumbnailDownloader<>(new Handler());
        mTumbnailThread.setListener(new TumbnailDownloader.Listener<ImageView>() {

            @Override
            public void onTumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()) {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mTumbnailThread.start();
        mTumbnailThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTumbnailThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridView);

        setupAdapter();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem item = mItems.get(position);
                Uri uri = Uri.parse(item.getPhotoPageUrl());
                Intent intent = new Intent(getActivity(), PhotoPageActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTumbnailThread.clearQueue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchItem = menu.findItem(R.id.menu_search);
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getActivity().getComponentName();
            SearchableInfo info = manager.getSearchableInfo(name);

            mSearchView.setSearchableInfo(info);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toggleMenuItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleMenuItem.setTitle(R.string.stop_polling);
        } else {
            toggleMenuItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetchr.PREF_SEARCH_QUERY, null)
                        .commit();
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mItems != null) {
            mGridView.setAdapter(new GalleryAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }
    }

    void updateItems() {
        new FetchFlickrItemTask().execute();
    }

    void searchStarted() {
        String query =
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
        if (query != null && mSearchView != null) {
            mSearchView.setIconified(false);
            mSearchView.setQueryHint(query);
            mSearchView.clearFocus();
        }
    }

    private class FetchFlickrItemTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
        private int mTotal = 0;

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {

            Activity activity = getActivity();
            if (activity == null) {
                return new ArrayList<>();
            }

            String query =
                    PreferenceManager.getDefaultSharedPreferences(activity)
                            .getString(FlickrFetchr.PREF_SEARCH_QUERY, null);

            if (query != null) {
                FlickrFetchr fetchr = new FlickrFetchr();
                ArrayList<GalleryItem> vItems = fetchr.search(query);
                mTotal = fetchr.getResultsCount();
                return vItems;
            } else return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mItems = items;
            setupAdapter();
            if (mTotal > 0) {
                Toast.makeText(getActivity(), "Found " + String.valueOf(mTotal), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class GalleryAdapter extends ArrayAdapter<GalleryItem> {

        public GalleryAdapter(List<GalleryItem> objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brian_up_close);
            GalleryItem item = getItem(position);
            mTumbnailThread.queueThumbnail(imageView, item.getUrl());

            //preloading
            int preposition = position - 1;
            while (preposition >= 0 && preposition >= position - 10) {
                item = getItem(preposition);
                mTumbnailThread.queuePreload(item.getUrl());
                --preposition;
            }
            preposition = position + 1;
            while (preposition < getCount() && preposition <= position + 10) {
                item = getItem(preposition);
                mTumbnailThread.queuePreload(item.getUrl());
                ++preposition;
            }

            return convertView;
        }
    }
}

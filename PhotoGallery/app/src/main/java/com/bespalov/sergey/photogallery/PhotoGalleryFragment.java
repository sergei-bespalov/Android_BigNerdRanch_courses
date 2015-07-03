package com.bespalov.sergey.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bespalov.sergey.photogallery.model.FlickrFetchr;
import com.bespalov.sergey.photogallery.model.GalleryItem;
import com.bespalov.sergey.photogallery.model.TumbnailDownloader;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    public static final String TAG = "PhotoGalleryFragment";

    private GridView mGridView;
    private ArrayList<GalleryItem> mItems;
    TumbnailDownloader<ImageView> mTumbnailThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //save fragment
        setRetainInstance(true);

        new FetchFlickrItemTask().execute();

        mTumbnailThread = new TumbnailDownloader<>(new Handler());
        mTumbnailThread.setListener(new TumbnailDownloader.Listener<ImageView>() {

            @Override
            public void onTumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()){
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

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTumbnailThread.clearQueue();
    }

    private class FetchFlickrItemTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mItems = items;
            setupAdapter();
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

    private class GalleryAdapter extends ArrayAdapter<GalleryItem> {

        public GalleryAdapter(List<GalleryItem> objects) {
            super(getActivity(), 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brian_up_close);
            GalleryItem item = getItem(position);
            mTumbnailThread.queueThumbnail(imageView, item.getUrl());

            //preloading
            int preposition = position - 1;
            while (preposition >= 0 && preposition >= position - 10){
                item = getItem(preposition);
                mTumbnailThread.queuePreload(item.getUrl());
                --preposition;
            }
            preposition = position + 1;
            while (preposition < getCount() && preposition <= position + 10){
                item = getItem(preposition);
                mTumbnailThread.queuePreload(item.getUrl());
                ++preposition;
            }

            return convertView;
        }
    }
}

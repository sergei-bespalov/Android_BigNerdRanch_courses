package com.bespalov.sergey.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.bespalov.sergey.photogallery.model.FlickrFetchr;

import java.io.IOException;

public class PhotoGalleryFragment extends Fragment {
    private GridView mGridView;

    public static final String TAG = "PhotoGalleryFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //save fragment
        setRetainInstance(true);

        new FetchFlickrItemTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridView);

        return view;
    }

    private class FetchFlickrItemTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String result = new FlickrFetchr().getUrl("http://www.google.com");
                Log.i(TAG, "Fetch content of URL result: " + result);
            }catch (IOException e){
                Log.e(TAG, "Failed to fetch url: ", e);
            }
            return null;
        }
    }
}

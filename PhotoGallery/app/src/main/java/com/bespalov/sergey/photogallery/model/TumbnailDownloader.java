package com.bespalov.sergey.photogallery.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.support.v4.util.LruCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "TumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    Listener<Token> mListener;
    int cacheSize = 10 * 1024 * 1024; // 10MiB
    BitmapCache cache = new BitmapCache(cacheSize);

    public TumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public interface Listener<Token> {
        void onTumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        mHandler.removeMessages(MESSAGE_PRELOAD);
        requestMap.clear();
    }


    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }else if (msg.what == MESSAGE_PRELOAD){
                    String url = (String) msg.obj;
                    Log.i(TAG, "Got a request for preload: " + url);
                    handlePreload(url);
                }
            }
        };
    }

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);

        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token)
                .sendToTarget();
    }

    public void queuePreload(String url) {
        Log.i(TAG, "Got an URL for preload: " + url);
        mHandler.obtainMessage(MESSAGE_PRELOAD, url)
                .sendToTarget();

    }

    private void handleRequest(final Token token) {
        final String url = requestMap.get(token);
        if (url == null)
            return;
        final Bitmap  bitmap = cache.get(url);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                //noinspection StringEquality
                if (requestMap.get(token) != url)
                    return;
                requestMap.remove(token);
                mListener.onTumbnailDownloaded(token, bitmap);
            }
        });
    }
    private void handlePreload(String url) {
        cache.get(url);
    }

    private class BitmapCache extends LruCache<String, Bitmap> {

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public BitmapCache(int maxSize) {
            super(maxSize);
        }

        protected int sizeOf(String key, Bitmap value) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                return value.getRowBytes() * value.getHeight();
            } else {
                return value.getByteCount();
            }
        }

        @Override
        protected Bitmap create(String key) {
            try {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(key);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "Bitmap created");
                return bitmap;
            } catch (IOException ioe) {
                Log.e(TAG, "Error downloading image", ioe);
                return null;
            }
        }
    }


}

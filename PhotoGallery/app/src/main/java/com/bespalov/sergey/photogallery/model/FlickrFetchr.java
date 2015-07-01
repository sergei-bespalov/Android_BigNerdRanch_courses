package com.bespalov.sergey.photogallery.model;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "03301b9a7cfb5dd7ad658b2fe9f9847a";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String PARAM_EXTRAS = "extra";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String PARAM_PAGE = "page";
    private static final String FARM = "farm";
    private static final String SERVER_ID = "server";
    private static final String PHOTO_ID = "id";
    private static final String SECRET = "secret";
    private static final String LARGE_SQUARE = "q";

    private static final int pageCount = 10;

    public static final String XML_PHOTO = "photo";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int byteRead = 0;
            byte[] buffer = new byte[1024];
            while ((byteRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, byteRead);
            }
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<GalleryItem> fetchItems() {
        ArrayList<GalleryItem> items = new ArrayList<>();

        try {
            String URL;
            String xmlString;
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            for (int page = 1; page <= pageCount; ++page) {
                URL = Uri.parse(ENDPOINT).buildUpon()
                        .appendQueryParameter("method", METHOD_GET_RECENT)
                        .appendQueryParameter("api_key", API_KEY)
                        .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                        .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                        .build().toString();

                xmlString = getUrl(URL);
                Log.i(TAG, "Recived xml: " + xmlString);

                parser.setInput(new StringReader(xmlString));
                parseItems(items, parser);
            }

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Failed to parse items", xppe);
        }
        return items;
    }

    public void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                //String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                String farm = parser.getAttributeValue(null,FARM);
                String serverId = parser.getAttributeValue(null, SERVER_ID);
                String photoId = parser.getAttributeValue(null, PHOTO_ID);
                String secret = parser.getAttributeValue(null, SECRET);
                String format = LARGE_SQUARE;
                String url = makePhotoUrl(farm, serverId, photoId, secret, format);
                GalleryItem item = new GalleryItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(url);
                items.add(item);
            }
            eventType = parser.next();
        }
    }

    private String makePhotoUrl(String farm, String serverId, String photoId, String secret, String format){
        return "https://farm" + farm + ".staticflickr.com/" + serverId + "/" + photoId + "_" + secret + "_" + format + ".jpg";
    }
}

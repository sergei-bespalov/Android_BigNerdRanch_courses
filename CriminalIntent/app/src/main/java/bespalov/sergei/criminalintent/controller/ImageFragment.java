package bespalov.sergei.criminalintent.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import bespalov.sergei.criminalintent.utils.PictureUtils;

/**
 * Created by sergei on 6/3/2015.
 */
public class ImageFragment extends DialogFragment {

    public static final String EXTRA_IMAGE_PATH = "bespalov.sergei.criminalintent.controller.ImageFragment.extra.imagePath";

    private ImageView mImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());

        String path = getArguments().getString(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);

        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView() {
        PictureUtils.cleanImageView(mImageView);
        super.onDestroyView();
    }

    public static ImageFragment newInstance(String path){
        ImageFragment imageFragment = new ImageFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_IMAGE_PATH, path);
        imageFragment.setArguments(args);

        imageFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return imageFragment;
    }
}

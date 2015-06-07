package bespalov.sergei.criminalintent.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by sergei on 6/3/2015.
 */
public class PictureUtils {

    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity activity, String path){
        Display display = activity.getWindowManager().getDefaultDisplay();
        float destHeight = display.getHeight();
        float destWidth = display.getWidth();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int sampleSize = 1;

        if (srcHeight > destHeight || srcWidth > destWidth){
            if(srcWidth > srcHeight){
                sampleSize = Math.round(srcHeight/destHeight);
            }else {
                sampleSize = Math.round(srcWidth/destWidth);
            }
        }

        options =new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void cleanImageView(ImageView imageView){
        if (!(imageView.getDrawable() instanceof BitmapDrawable)) return;

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable.getBitmap() == null) return;
        drawable.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }


    public static Bitmap rotateImage(Bitmap source, int angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    public static void ClearFile(String path){
        File file = new File(path);
        if (file.exists()){
            file.delete();
        }
    }
}

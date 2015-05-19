package bespalov.sergei.hellomoon;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by sergei on 5/19/2015.
 */
public class AudioPlayer {
    private MediaPlayer mMediaPlayer;
    private int mPosition = 0;

    public void play(Context context){
        mMediaPlayer = MediaPlayer.create(context, R.raw.one_small_step);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        mMediaPlayer.start();
        mMediaPlayer.seekTo(mPosition);
    }

    public void pause(){
        if (mMediaPlayer != null){
            mPosition = mMediaPlayer.getCurrentPosition();
            if (mPosition == mMediaPlayer.getDuration()) mPosition = 0;
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void stop(){
        if (mMediaPlayer != null){
            mPosition = 0;
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}

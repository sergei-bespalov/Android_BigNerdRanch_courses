package com.example.sergei.geoquiz;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

/**
 * GeoQuiz from Big Nerd Ranch Book
 */
public class QuizActivity extends ActionBarActivity {
    private static final String TAG = "QuizActivity";
    private Button mTrueButton;
    private Button mFalseButton;
    private View mNextButton;
    private View mPrevButton;
    private Button mCheatButton;
    private TextView mTextView;
    private static final String KEY_INDEX = "com.example.sergei.geoquiz.state.index";
    private static final String KEY_SAVED_ARRAY = "questions";


    private TrueFalse[] mQuestionBank = new TrueFalse[]{
            new TrueFalse(R.string.question_oceans, true),
            new TrueFalse(R.string.question_mideast, false),
            new TrueFalse(R.string.question_africa, false),
            new TrueFalse(R.string.question_americas, true),
            new TrueFalse(R.string.question_asia, true),};

    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mQuestionBank = (TrueFalse[]) savedInstanceState.getParcelableArray(KEY_SAVED_ARRAY);
        }

        setContentView(R.layout.activity_main);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
        }

        mTextView = (TextView) findViewById(R.id.text_question);
        updateQuestion();
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });

        mNextButton = findViewById(R.id.button_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });

        mPrevButton = findViewById(R.id.button_prev);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex > 0) --mCurrentIndex;
                else if (mCurrentIndex == 0) mCurrentIndex = mQuestionBank.length - 1;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.button_true);
        mFalseButton = (Button) findViewById(R.id.button_false);
        mCheatButton = (Button) findViewById(R.id.button_cheat);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheat();
            }
        });
    }

    private void nextQuestion(){
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getQuestion();
        mTextView.setText(question);
    }

    private void checkAnswer(boolean userPressTrue){
        int resultToast;
        if (mQuestionBank[mCurrentIndex].isCheater()) resultToast = R.string.judgment_toast;
        else if (mQuestionBank[mCurrentIndex].isTrueQuestion() == userPressTrue){
            resultToast = R.string.toast_correct;
        }else resultToast = R.string.toast_incorrect;
        Toast.makeText(QuizActivity.this, resultToast, Toast.LENGTH_SHORT).show();
    }

    private void cheat(){
        Intent intent = new Intent(this,CheatActivity.class);
        boolean isTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
        intent.putExtra(CheatActivity.EXTRA_KEY_IS_TRUE_QUESTION,isTrue);
        //startActivity(intent);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        boolean isCheater = data.getBooleanExtra(CheatActivity.EXTRA_KEY_SHOW_ANSWER, false);
        mQuestionBank[mCurrentIndex].setCheater(isCheater);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause() called ");
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy called");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG,"onSaveInstanceState(Bundle)");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putParcelableArray(KEY_SAVED_ARRAY, mQuestionBank);
    }
}

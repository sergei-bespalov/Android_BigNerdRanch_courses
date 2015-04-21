package com.example.sergei.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sergei on 4/20/2015.
 */
public class CheatActivity extends ActionBarActivity{

    public static final String EXTRA_KEY_IS_TRUE_QUESTION = "com.example.sergei.geoquiz.CheatActivity.istrue";
    public static final String EXTRA_KEY_SHOW_ANSWER = "com.example.sergei.geoquiz.CheatActivity.showAnswer";

    private Button mCheatButton;
    private TextView mCheatText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        mCheatText = (TextView) findViewById(R.id.textview_cheat);
        mCheatButton = (Button) findViewById(R.id.button_show_cheat);

        final boolean isTrue = getIntent().getBooleanExtra(EXTRA_KEY_IS_TRUE_QUESTION, false);

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheat(isTrue);
            }
        });
    }

    private void cheat(boolean isTrue){
        if(isTrue) mCheatText.setText(R.string.button_true_text);
        else mCheatText.setText(R.string.button_false_text);
        setAnswerShowResult(true);
    }

    public void setAnswerShowResult(boolean isShow){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_SHOW_ANSWER, isShow);
        setResult(RESULT_OK,intent);
    }
}

package bespalov.sergei.criminalintent.controller;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bespalov.sergei.criminalintent.R;

/**
 * Created by sergei on 5/11/2015.
 */
public class TimePickerFragment extends SherlockDialogFragment {

    public static final String EXTRA_TIME = "bespalov.sergei.criminalintent.controller.extra.time";
    Date mTime;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time,null);

        mTime = (Date) getArguments().getSerializable(EXTRA_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int min = calendar.get(Calendar.MINUTE);

        TimePicker picker = (TimePicker) view.findViewById(R.id.dialog_timePiker);
        picker.setIs24HourView(true);
        picker.setCurrentHour(hour);
        picker.setCurrentMinute(min);
        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                mTime = new GregorianCalendar(0,0,0,hour,minute).getTime();
                getArguments().putSerializable(EXTRA_TIME,mTime);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    public static TimePickerFragment newInstance(Date time){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, time);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode){
        if(getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,mTime);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

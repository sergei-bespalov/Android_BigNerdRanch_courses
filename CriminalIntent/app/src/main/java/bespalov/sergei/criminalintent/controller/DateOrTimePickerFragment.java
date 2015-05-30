package bespalov.sergei.criminalintent.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bespalov.sergei.criminalintent.R;

/**
 * Created by sergei on 5/11/2015.
 */
public class DateOrTimePickerFragment extends SherlockDialogFragment {

    public static final String EXTAR_KEY_DATE = "bespalov.sergei.criminalintent.controller.dateortime.extra.key.date";

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Date mDate;
    private Button mDateButton;
    private Button mTimeButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_or_time, null);

        mDate = (Date) getArguments().getSerializable(EXTAR_KEY_DATE);

        mDateButton = (Button) view.findViewById(R.id.dialog_change_date_button);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = DatePickerFragment.newInstance(mDate);
                dialog.setTargetFragment(DateOrTimePickerFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) view.findViewById(R.id.dialog_change_time_buttton);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = TimePickerFragment.newInstance(mDate);
                dialog.setTargetFragment(DateOrTimePickerFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.change_date_or_time_title)
                .create();
    }

    public static DateOrTimePickerFragment newInstance(Date date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTAR_KEY_DATE, date);
        DateOrTimePickerFragment fragment = new DateOrTimePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_DATE){

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mDate);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            mDate = new GregorianCalendar(year,month,day,hour,minute).getTime();
            sendResult(Activity.RESULT_OK);
        }

        if (requestCode == REQUEST_TIME){

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mDate);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            calendar.setTime(date);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            mDate = new GregorianCalendar(year,month,day,hour,minute).getTime();
            sendResult(Activity.RESULT_OK);
        }
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTAR_KEY_DATE, mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

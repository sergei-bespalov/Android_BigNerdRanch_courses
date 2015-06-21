package com.bespalov.sergey.remotecontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RemoteControlFragment extends Fragment {
    TextView mWorkingText;
    TextView mSelectedText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remote_control, container, false);

        mWorkingText = (TextView) view.findViewById(R.id.fragment_remote_control_workingTextView);
        mSelectedText = (TextView) view.findViewById(R.id.fragment_remote_control_selectedTextView);

        View.OnClickListener numbersClicksListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                String workingText = mWorkingText.getText().toString();
                String newText = textView.getText().toString();
                if(workingText.equals("0")){
                    mWorkingText.setText(newText);
                }else {
                    mWorkingText.setText(workingText + newText);
                }
            }
        };

        TableLayout root = (TableLayout) view.findViewById(R.id.fragment_remote_control_tableLayout);
        int number = 0;
        for (int i = 2; i < root.getChildCount() - 1; i++){
            TableRow row = (TableRow) root.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++){
                TextView text = (TextView) row.getChildAt(j);
                text.setText(String.valueOf(++number));
                text.setOnClickListener(numbersClicksListner);
            }
        }

        int lastRowIndex = root.getChildCount() - 1;
        TableRow lastRow = (TableRow) root.getChildAt(lastRowIndex);

        //Delete Button
        TextView deleteButton = (TextView) lastRow.getChildAt(0);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkingText.setText("0");
                mSelectedText.setText("0");
            }
        });
        deleteButton.setTextAppearance(getActivity(), R.style.RemoteButtons_ControlButtons);

        //Zero
        TextView zero = (TextView) lastRow.getChildAt(1);
        zero.setOnClickListener(numbersClicksListner);
        zero.setText("0");

        //Enter
        TextView enter = (TextView) lastRow.getChildAt(2);
        enter.setText("Enter");
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String working = mWorkingText.getText().toString();
                if (working.length() > 0) {
                    mSelectedText.setText(working);
                }
                mWorkingText.setText("0");
            }
        });
        enter.setTextAppearance(getActivity(), R.style.RemoteButtons_ControlButtons);

        return view;
    }
}

package bespalov.sergei.criminalintent.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import bespalov.sergei.criminalintent.R;
import bespalov.sergei.criminalintent.model.Crime;
import bespalov.sergei.criminalintent.model.CrimeLab;
import bespalov.sergei.criminalintent.model.Photo;
import bespalov.sergei.criminalintent.utils.PictureUtils;

/**
 * Fragment for Crime
 */
public class CrimeFragment extends Fragment{
    private Crime mCrime;
    private EditText mEditText;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mTimeOrDateButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private CheckBox mSolvedCheckBox;
    private ImageView mPhotoView;

    public static final String EXTRA_CRIME_ID = "sergei.bespalov.criminalintent.extra.crime.id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final String DIALOG_DATE_OR_TIME = "date_or_time";
    private static final String DIALOG_SHOW_PHOTO = "photo";
    private static final String TAG = "CrimeFragment";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_DATE_OR_TIME = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_CONTACT = 4;

    private Callback mCallback;

    /**
     * required callback for host-activity
     */
    public interface Callback{
        void onCrimeUpdated(Crime crime);
    }

    public static CrimeFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, id);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback =null;
    }

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeID = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(crimeID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mEditText = (EditText) view.findViewById(R.id.crime_title);
        mEditText.setText(mCrime.getTitle());
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mCrime.setTitle(charSequence.toString());
                mCallback.onCrimeUpdated(mCrime);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = (Button) view.findViewById(R.id.crime_date);
        updateDateButtonText();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) view.findViewById(R.id.crime_time);
        updateTimeButtonText();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        mTimeOrDateButton = (Button) view.findViewById(R.id.crime_date_time);
        updateDateOrTimeButton();
        mTimeOrDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = DateOrTimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE_OR_TIME);
                dialog.show(fm, DIALOG_DATE_OR_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
                mCallback.onCrimeUpdated(mCrime);
            }
        });

        mPhotoButton = (ImageButton) view.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });

        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView) view.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo photo = mCrime.getPhoto();
                if (photo == null) return;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_SHOW_PHOTO);
            }
        });

        mReportButton = (Button) view.findViewById(R.id.crime_reportButton);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                if (checkImplicitIntent(intent)) startActivity(intent);
            }
        });

        mSuspectButton = (Button) view.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                if (checkImplicitIntent(intent)) startActivityForResult(intent, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallButton = (Button) view.findViewById(R.id.crime_callButton);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCrime.getSuspectTel() != null){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.putExtra(Intent.EXTRA_PHONE_NUMBER, mCrime.getSuspectTel());
                    intent.setData(Uri.parse("tel:"+mCrime.getSuspectTel()));
                    if (checkImplicitIntent(intent)) startActivity(intent);
                }else Toast.makeText(getActivity(),R.string.crime_no_phone_number, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop(){
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getCrimeLab(getActivity()).saveCrimes();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_crime:
                CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime);
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_DATE) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mCrime.getDate());

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            date = new GregorianCalendar(year, month, day, hour, minute).getTime();
            mCrime.setDate(date);

            updateDateButtonText();
            updateDateOrTimeButton();
            mCallback.onCrimeUpdated(mCrime);
        } else if (requestCode == REQUEST_TIME) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mCrime.getDate());

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            calendar.setTime(date);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            date = new GregorianCalendar(year, month, day, hour, minute).getTime();
            mCrime.setDate(date);

            updateTimeButtonText();
            updateDateOrTimeButton();
            mCallback.onCrimeUpdated(mCrime);
        } else if (requestCode == REQUEST_DATE_OR_TIME) {

            Date date = (Date) data.getSerializableExtra(DateOrTimePickerFragment.EXTAR_KEY_DATE);
            mCrime.setDate(date);

            updateDateButtonText();
            updateTimeButtonText();
            updateDateOrTimeButton();
            mCallback.onCrimeUpdated(mCrime);
        }else if (requestCode == REQUEST_PHOTO){
            String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_NAME);
            if (fileName != null){
                if (mCrime.getPhoto() != null){
                    Photo photo = mCrime.getPhoto();
                    PictureUtils.ClearFile(getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath());
                    mCrime.setPhoto(null);
                }
                Photo photo = new Photo(fileName);
                mCrime.setPhoto(photo);
                Log.i(TAG,"Crime " + mCrime.getTitle() + " has a photo");
                showPhoto();
                mCallback.onCrimeUpdated(mCrime);
            }
        }else if (requestCode == REQUEST_CONTACT){
            Uri uri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.Contacts._ID
            };
            Cursor cursor = getActivity().getContentResolver().query(uri, queryFields, null, null, null);

            if (cursor.getCount() == 0){
                cursor.close();
                return;
            }

            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);

            if (cursor.getString(1).equals("1")){
                String contactId = cursor.getString(2);
                String[] queryPhoneField = new String[]{
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                String queryPhone =  ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId;
                Cursor cPhones = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        queryPhoneField,
                        queryPhone,
                        null,
                        null
                        );
                if (cPhones.getCount() == 0){
                    cursor.close();
                    cPhones.close();
                    return;
                }
                cPhones.moveToFirst();
                mCrime.setSuspectTel(cPhones.getString(1));
                cPhones.close();
            }

            cursor.close();
            mCallback.onCrimeUpdated(mCrime);
        }



    }

    private void updateDateButtonText() {
        String date = DateFormat.format("EEEE, MMM d, yyyy", mCrime.getDate()).toString();
        mDateButton.setText(date);
    }

    private void updateTimeButtonText() {
        String date = DateFormat.format("kk:mm", mCrime.getDate()).toString();
        mTimeButton.setText(date);
    }

    private void updateDateOrTimeButton() {
        String date = DateFormat.format("EEEE, MMM d, yyyy kk:mm", mCrime.getDate()).toString();
        mTimeOrDateButton.setText(date);
    }

    private void showPhoto(){
        Photo photo = mCrime.getPhoto();
        BitmapDrawable drawable = null;
        if (photo != null){
            String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
            drawable = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(drawable);
    }

    private String getCrimeReport(){
        String solved = null;
        if (mCrime.isSolved()){
            solved = getString(R.string.crime_report_solved);
        }else {
            solved =getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM, dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect != null){
            suspect = getString(R.string.crime_report_suspect, suspect);
        }else {
            suspect = getString(R.string.crime_report_no_suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solved, suspect);
        return  report;
    }

    private boolean checkImplicitIntent(Intent intent){
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (activities.size() > 0) return true;
        else return false;
    }
}

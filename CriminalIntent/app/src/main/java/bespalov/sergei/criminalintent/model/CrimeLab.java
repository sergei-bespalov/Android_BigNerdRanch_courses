package bespalov.sergei.criminalintent.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sergei on 5/3/2015.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJsonSerializer mSerializer;

    static private CrimeLab sCrimeLab;

    private Context appContext;

    private CrimeLab(Context appContext){
        this.appContext = appContext;
        this.mSerializer = new CriminalIntentJsonSerializer(this.appContext, FILENAME);
        try{
            mCrimes = mSerializer.loadCrimes();
        }catch (Exception e){
            mCrimes = new ArrayList<>();
            Log.e(TAG,"Error loading crimes", e);
        }
    }

    public static CrimeLab getCrimeLab(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for (Crime x: mCrimes){
            if (x.getId().equals(id)) return x;
        }
        return null;
    }

    public void addCrime(Crime crime){
        mCrimes.add(crime);
    }

    public boolean saveCrimes(){
        try {
            mSerializer.SaveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        }catch (Exception e){
            Log.e(TAG, "error saving crimes", e);
            return false;
        }

    }

}

package bespalov.sergei.criminalintent.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sergei on 5/3/2015.
 */
public class CrimeLab {
    private ArrayList<Crime> mCrimes;

    static private CrimeLab sCrimeLab;
    private Context appContext;

    private CrimeLab(Context appContext){
        this.appContext = appContext;
        mCrimes = new ArrayList<>();
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

}

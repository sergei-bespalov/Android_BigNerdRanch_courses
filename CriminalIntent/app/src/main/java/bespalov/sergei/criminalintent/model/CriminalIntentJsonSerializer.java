package bespalov.sergei.criminalintent.model;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by sergei on 5/26/2015.
 */
public class CriminalIntentJsonSerializer {
    private Context mContext;
    private String mFileName;

    public CriminalIntentJsonSerializer(Context context, String fileMame) {
        mContext = context;
        mFileName = fileMame;
    }

    public void SaveCrimes(ArrayList<Crime> crimes) throws IOException, JSONException {

        //json array building
        JSONArray jsonArray = new JSONArray();
        for (Crime crime : crimes) {
            jsonArray.put(crime.toJson());
        }

        //writing file on a disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jsonArray.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(mFileName);
            Scanner scanner = new Scanner(in);
            StringBuilder jsonString = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime((array.getJSONObject(i))));
            }
        } catch (FileNotFoundException e) {
            //nevermind
        } finally {
            if (reader != null){
                reader.close();
            }
            return crimes;
        }

    }

}

package bespalov.sergei.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Crime model
 */
public class Crime {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_PHOTO = "photo";

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;

    public Crime(){
        mDate = new Date();
        mId = UUID.randomUUID();
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public String toString(){
        return mTitle;
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO)){
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        }
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, this.mId);
        json.put(JSON_TITLE, this.mTitle);
        json.put(JSON_SOLVED, this.mSolved);
        json.put(JSON_DATE, this.mDate.getTime());
        if (mPhoto != null){
            json.put(JSON_PHOTO, mPhoto.toJSON());
        }
        return json;
    }

    public Photo getPhoto(){
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }
}

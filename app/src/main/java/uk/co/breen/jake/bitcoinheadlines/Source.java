package uk.co.breen.jake.bitcoinheadlines;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Jake on 04/03/2017.
 */

public class Source implements Parcelable {

    private String id, title, description, image, dateArray, rssURL;

    public Source (String idIn, String titleIn, String descriptionIn, String dateArrayIn, String imageIn, String rssURLIn) {
        id = idIn;
        title = titleIn;
        description = descriptionIn;
        dateArray = dateArrayIn;
        image = imageIn;
        rssURL = rssURLIn;
    }

    public Source() {

    }

    protected Source(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        dateArray = in.readString();
        rssURL = in.readString();
    }

    public static final Creator<Source> CREATOR = new Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public String getDateArray() {
        return dateArray;
    }

    public void setDateArray(String dateArray) {
        this.dateArray = dateArray;
    }

    public String getRssURL() {
        return rssURL;
    }

    public void setRssURL(String rssURL) {
        this.rssURL = rssURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(dateArray);
        parcel.writeString(rssURL);
    }
}
package com.leongao.magtime.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class MagCoverImg implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    protected MagCoverImg(Parcel in) {
        id = in.readInt();
        url = in.readString();
    }

    public static final Creator<MagCoverImg> CREATOR = new Creator<MagCoverImg>() {
        @Override
        public MagCoverImg createFromParcel(Parcel in) {
            return new MagCoverImg(in);
        }

        @Override
        public MagCoverImg[] newArray(int size) {
            return new MagCoverImg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(url);
    }
}

package com.leongao.magtime.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class MagazineInfo implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("magName")
    private String magName;

    @SerializedName("magPublishDate")
    private String magPublishDate;

    @SerializedName("magCoverImg")
    private MagCoverImg magCoverImg;

    public int getId() {
        return id;
    }

    public String getMagName() {
        return magName;
    }

    public String getMagPublishDate() {
        return magPublishDate;
    }

    public MagCoverImg getMagCoverImg() {
        return magCoverImg;
    }

    protected MagazineInfo(Parcel in) {
        id = in.readInt();
        magName = in.readString();
        magPublishDate = in.readString();
        magCoverImg = in.readParcelable(MagCoverImg.class.getClassLoader());
    }

    public static final Creator<MagazineInfo> CREATOR = new Creator<MagazineInfo>() {
        @Override
        public MagazineInfo createFromParcel(Parcel in) {
            return new MagazineInfo(in);
        }

        @Override
        public MagazineInfo[] newArray(int size) {
            return new MagazineInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(magName);
        parcel.writeString(magPublishDate);
        parcel.writeParcelable(magCoverImg, 0);
    }
}

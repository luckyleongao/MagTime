package com.leongao.magtime.home.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Magazine implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("magInfo")
    private MagazineInfo magazineInfo;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public MagazineInfo getMagazineInfo() {
        return magazineInfo;
    }

    public void setMagazineInfo(MagazineInfo magazineInfo) {
        this.magazineInfo = magazineInfo;
    }

    public static final Creator<Magazine> CREATOR = new Creator<Magazine>() {
        @Override
        public Magazine createFromParcel(Parcel in) {
            return new Magazine(in);
        }

        @Override
        public Magazine[] newArray(int size) {
            return new Magazine[size];
        }
    };

    protected Magazine(Parcel in) {
        id = in.readInt();
        // 对象读取需要提供一个类加载器
        magazineInfo = in.readParcelable(MagazineInfo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        // 这里的flag几乎都是0,除非标识当前对象需要作为返回值返回,不能立即释放资源
        parcel.writeParcelable(magazineInfo, 0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Magazine) {
            Magazine magazine = (Magazine) obj;
            return this.id == magazine.getId()
                    && this.magazineInfo.getId() == magazine.getMagazineInfo().getId()
                    && this.magazineInfo.getMagName().equals(magazine.getMagazineInfo().getMagName())
                    && this.magazineInfo.getMagPublishDate().equals(magazine.getMagazineInfo().getMagPublishDate())
                    && this.magazineInfo.getMagCoverImg().getId() == magazine.getMagazineInfo().getMagCoverImg().getId()
                    && this.magazineInfo.getMagCoverImg().getUrl().equals(magazine.getMagazineInfo().getMagCoverImg().getUrl());
        }

        return false;
    }
}

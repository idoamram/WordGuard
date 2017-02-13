package com.io.wordguard.ui.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class RevealAnimationSetting implements Parcelable {

    private static final String TAG = "RevealAnimationSetting";

    private int centerX;

    private int centerY;

    private int width;

    private int height;

    public RevealAnimationSetting(int centerX, int centerY, int width, int height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static RevealAnimationSetting with(int centerX, int centerY, int width, int height) {
        return new RevealAnimationSetting(centerX, centerY, width, height);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.centerX);
        dest.writeInt(this.centerY);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected RevealAnimationSetting(Parcel in) {
        this.centerX = in.readInt();
        this.centerY = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<RevealAnimationSetting> CREATOR = new Parcelable.Creator<RevealAnimationSetting>() {
        @Override
        public RevealAnimationSetting createFromParcel(Parcel source) {
            return new RevealAnimationSetting(source);
        }

        @Override
        public RevealAnimationSetting[] newArray(int size) {
            return new RevealAnimationSetting[size];
        }
    };
}
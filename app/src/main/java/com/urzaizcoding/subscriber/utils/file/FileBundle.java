package com.urzaizcoding.subscriber.utils.file;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Objects;

public class FileBundle implements Parcelable {
    private Uri providedUri;
    private String localeAbsolutePath;

    public final static Creator<FileBundle> CREATOR;

    static {
        CREATOR = new Creator<FileBundle>() {
            @Override
            public FileBundle createFromParcel(Parcel parcel) {
                return new FileBundle(parcel);
            }

            @Override
            public FileBundle[] newArray(int size) {
                return new FileBundle[size];
            }
        };
    }

    public FileBundle() {
    }

    public FileBundle(Uri providedUri, String localeAbsolutePath) {
        this.providedUri = providedUri;
        this.localeAbsolutePath = localeAbsolutePath;
    }

    public FileBundle(Parcel in){
        providedUri = in.readParcelable(Uri.class.getClassLoader());
        localeAbsolutePath = in.readString();
    }


    public Uri getProvidedUri(){
        return providedUri;
    }
    public String getLocaleAbsolutePath(){
        return localeAbsolutePath;
    }
    public String getFileName(){
        return localeAbsolutePath.substring(localeAbsolutePath.lastIndexOf(File.separator)+1);
    }

    public void setProvidedUri(Uri providedUri) {
        this.providedUri = providedUri;
    }

    public void setLocaleAbsolutePath(String localeAbsolutePath) {
        this.localeAbsolutePath = localeAbsolutePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(providedUri,flags);
        out.writeString(localeAbsolutePath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileBundle)) return false;
        FileBundle that = (FileBundle) o;
        return getProvidedUri().equals(that.getProvidedUri()) && getLocaleAbsolutePath().equals(that.getLocaleAbsolutePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProvidedUri(), getLocaleAbsolutePath());
    }
}

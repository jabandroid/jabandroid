package com.global.vtg.appview.home.event;

import android.os.Parcel;
import android.os.Parcelable;


public class ContactItem implements Parcelable {

    public String name;
    public int id;
    public String number;
    public String photo;

    public String getName() {
        return name==null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number==null ? "" : number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContactItem() {
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    protected ContactItem(Parcel in) {
        name = in.readString();
        number = in.readString();
        photo = in.readString();
        id = in.readInt();
    }

    public static final Creator<ContactItem> CREATOR = new Creator<ContactItem>() {
        @Override
        public ContactItem createFromParcel(Parcel in) {
            return new ContactItem(in);
        }

        @Override
        public ContactItem[] newArray(int size) {
            return new ContactItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(photo);
        dest.writeInt(id);
    }
}

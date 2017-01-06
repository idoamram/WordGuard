package com.io.wordguard.word;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.io.wordguard.ui.util.DateHelper;
import com.io.wordguard.db.simplite.DBObject;
import com.io.wordguard.db.simplite.annotations.Column;
import com.io.wordguard.db.simplite.annotations.Entity;
import com.io.wordguard.db.simplite.annotations.PrimaryKey;
import java.util.Date;
import static com.io.wordguard.word.Word.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
public class Word extends DBObject implements Parcelable{

    public static final int TYPE_PRIVATE = 0;
    public static final int TYPE_PUBLIC = 1;

    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_TRASH = 2;

    static final String TABLE_NAME = "words";

    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    public static final String COL_TYPE = "type";
    private static final String COL_CREATION_TIME = "creation_time";
    public static final String COL_DEAD_LINE = "dead_line";
    private static final String COL_CONTACT_ID = "contact_id";
    private static final String COL_CONTACT_NAME = "contact_name";
    private static final String COL_CONTACT_PHONE_NUMBER = "contact_phone_number";
    private static final String COL_CONTACT_EMAIL = "contact_email";
    public static final String COL_STATUS = "status";
    private static final String COL_LOCATION_LONGITUDE = "location_longitude";
    private static final String COL_LOCATION_LATITUDE = "location_latitude";
    private static final String COL_LOCATION_ADDRESS = "location_address";

    public Word(Context context) {
        super(context);
    }

    @PrimaryKey(columnName = COL_ID, options = {OPTION_AUTOINCREAMENT})
    private long id;

    @Column(name = COL_TITLE)
    private String title;

    @Column(name = COL_DESCRIPTION)
    private String description;

    @Column(name = COL_TYPE)
    private int type;

    @Column(name = COL_STATUS)
    private int status;

    @Column(name = COL_CREATION_TIME)
    private Date creationTime;

    @Column(name = COL_DEAD_LINE)
    private Date deadLine;

    @Column(name = COL_CONTACT_ID)
    private String contactId;

    @Column(name = COL_CONTACT_NAME)
    private String contactName;

    @Column(name = COL_CONTACT_PHONE_NUMBER)
    private String contactPhoneNumber;

    @Column(name = COL_CONTACT_EMAIL)
    private String contactEmail;

    @Column(name = COL_LOCATION_LATITUDE)
    private double locationLatitude;

    @Column(name = COL_LOCATION_LONGITUDE)
    private double locationLongitude;

    @Column(name = COL_LOCATION_ADDRESS)
    private String locationAddress;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Date deadLine) {
        this.deadLine = deadLine;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Storing the Word data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(type);
        parcel.writeString(DateHelper.dateToString(creationTime));
        parcel.writeString(DateHelper.dateToString(deadLine));
        parcel.writeString(contactId);
        parcel.writeString(contactName);
        parcel.writeString(contactPhoneNumber);
        parcel.writeString(contactEmail);
        parcel.writeInt(status);
        parcel.writeDouble(locationLatitude);
        parcel.writeDouble(locationLongitude);
        parcel.writeString(locationAddress);
    }

    /**
     * Retrieving Word data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private Word(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.description = in.readString();
        this.type = in.readInt();
        this.creationTime = DateHelper.stringToDate(in.readString());
        this.deadLine = DateHelper.stringToDate(in.readString());
        this.contactId = in.readString();
        this.contactName = in.readString();
        this.contactPhoneNumber = in.readString();
        this.contactEmail = in.readString();
        this.status = in.readInt();
        this.locationLatitude = in.readDouble();
        this.locationLongitude = in.readDouble();
        this.locationAddress = in.readString();
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {

        @Override
        public Word createFromParcel(Parcel source) {
            return new Word(source);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}

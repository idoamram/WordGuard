package com.io.wordguard.db;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.io.wordguard.ui.util.DateHelper;
import com.simplite.orm.DBObject;
import com.simplite.orm.annotations.Column;
import com.simplite.orm.annotations.Entity;
import com.simplite.orm.annotations.PrimaryKey;
import java.util.Date;
import static com.io.wordguard.db.Word.TABLE_NAME;

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
    private static final String COL_TYPE = "type";
    private static final String COL_CREATION_TIME = "creation_time";
    private static final String COL_DEAD_LINE = "dead_line";
    private static final String COL_CONTACT_ID = "contact_id";
    private static final String COL_CONTACT_NAME = "contact_name";
    private static final String COL_CONTACT_NUMBER = "contact_number";
    private static final String COL_CONTACT_EMAIL = "contact_email";
    private static final String COL_STATUS = "status";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_LATITUDE = "latitude";

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

    @Column(name = COL_CONTACT_NUMBER)
    private String contactNumber;

    @Column(name = COL_CONTACT_EMAIL)
    private String contactEmail;

    @Column(name = COL_LATITUDE)
    private long latitude;

    @Column(name = COL_LONGITUDE)
    private long longitude;

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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
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
        parcel.writeString(contactNumber);
        parcel.writeString(contactEmail);
        parcel.writeInt(status);
        parcel.writeLong(latitude);
        parcel.writeLong(longitude);
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
        this.contactNumber = in.readString();
        this.contactEmail = in.readString();
        this.status = in.readInt();
        this.latitude = in.readLong();
        this.longitude = in.readLong();
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

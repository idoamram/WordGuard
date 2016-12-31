package com.io.wordguard.db;

import com.simplite.orm.DBObject;
import com.simplite.orm.annotations.Column;
import com.simplite.orm.annotations.Entity;
import com.simplite.orm.annotations.PrimaryKey;
import java.util.Date;
import static com.io.wordguard.db.Word.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
class Word extends DBObject {

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
    private static final String COL_ATTACHMENT = "attachment";
    private static final String COL_CONTACT_ID = "contact_id";
    private static final String COL_CONTACT_NAME = "contact_name";
    private static final String COL_CONTACT_NUMBER = "contact_number";
    private static final String COL_CONTACT_EMAIL = "contact_email";
    private static final String COL_STATUS = "status";

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

//    @ForeignKey(valueColumnName = COL_ID, fkColumnName = Contact.COL_WORD_ID, entityClass = Contact.class)
//    private Contact contact;

    @Column(name = COL_ATTACHMENT)
    private String attachment;

    @Column(name = COL_CONTACT_ID)
    private String contactId;

    @Column(name = COL_CONTACT_NAME)
    private String contactName;

    @Column(name = COL_CONTACT_NUMBER)
    private String contactNumber;

    @Column(name = COL_CONTACT_EMAIL)
    private String contactEmail;
}

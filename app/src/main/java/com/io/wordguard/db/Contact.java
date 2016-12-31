package com.io.wordguard.db;

import com.simplite.orm.DBObject;
import com.simplite.orm.annotations.Column;
import com.simplite.orm.annotations.Entity;
import com.simplite.orm.annotations.PrimaryKey;
import static com.io.wordguard.db.Contact.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
class Contact extends DBObject {

    static final String TABLE_NAME = "contacts";

    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_NUMBER = "number";
    private static final String COL_EMAIL = "email";
    static final String COL_WORD_ID = "word_id";

    @PrimaryKey(columnName = COL_ID)
    private String id;

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_NUMBER)
    private String number;

    @Column(name = COL_EMAIL)
    private String email;

    @Column(name = COL_WORD_ID)
    private long wordId;
}

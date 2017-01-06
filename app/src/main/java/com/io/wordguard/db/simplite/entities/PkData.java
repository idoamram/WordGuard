package com.io.wordguard.db.simplite.entities;

import com.io.wordguard.db.simplite.DBObject;
import com.io.wordguard.db.simplite.annotations.PrimaryKey;
import com.io.wordguard.db.simplite.entities.ColumnData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PkData extends ColumnData {

    public PkData(Field field, PrimaryKey annotation, Method setter) {
        super(field, setter, annotation);
    }

    public boolean isAutoIncrement() {
        String[] options = getOptions();
        if (options != null) {
            for (String option : options) {
                if (option.equals(DBObject.OPTION_AUTOINCREAMENT))
                    return true;
            }
        }
        return false;
    }
}

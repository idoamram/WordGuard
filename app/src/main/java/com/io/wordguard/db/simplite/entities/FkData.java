package com.io.wordguard.db.simplite.entities;

import com.io.wordguard.db.simplite.DBObject;
import com.io.wordguard.db.simplite.annotations.ForeignKey;
import com.io.wordguard.db.simplite.annotations.ForeignKeyArray;
import com.io.wordguard.db.simplite.entities.ColumnData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FkData extends ColumnData {

    private Class<? extends DBObject> fkClass;
    private String valueColumn;

    public FkData(Field field, ForeignKey annotation, Method setter) {
        super(field, setter, annotation);
        this.fkClass = annotation.entityClass();
        this.valueColumn = annotation.valueColumnName();
    }

    public FkData(Field field, ForeignKeyArray annotation, Method setter) {
        super(field, setter, annotation);
        this.fkClass = annotation.entityClass();
        this.valueColumn = annotation.valueColumnName();
    }

    public Class<? extends DBObject> getFkClass() {
        return fkClass;
    }

    public String getValueColumn() {
        return valueColumn;
    }
}

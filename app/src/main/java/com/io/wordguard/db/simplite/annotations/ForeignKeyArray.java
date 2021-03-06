package com.io.wordguard.db.simplite.annotations;

import com.io.wordguard.db.simplite.DBObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKeyArray {
    String valueColumnName();

    String fkColumnName();

    Class<? extends DBObject> entityClass();

    String[] options() default {};
}

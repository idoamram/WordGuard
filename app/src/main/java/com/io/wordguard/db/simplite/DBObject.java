package com.io.wordguard.db.simplite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.io.wordguard.db.simplite.annotations.Column;
import com.io.wordguard.db.simplite.annotations.Entity;
import com.io.wordguard.db.simplite.annotations.ForeignKey;
import com.io.wordguard.db.simplite.annotations.ForeignKeyArray;
import com.io.wordguard.db.simplite.annotations.PrimaryKey;
import com.io.wordguard.db.simplite.entities.ColumnData;
import com.io.wordguard.db.simplite.entities.Condition;
import com.io.wordguard.db.simplite.entities.FkData;
import com.io.wordguard.db.simplite.entities.PkData;
import com.io.wordguard.db.simplite.interfaces.CRUDCallback;
import com.io.wordguard.db.simplite.interfaces.FindAllCallback;
import com.io.wordguard.db.simplite.interfaces.SelectCallback;
import com.io.wordguard.db.simplite.tasks.CRUDBackgroundTask;
import com.io.wordguard.db.simplite.tasks.FindAllBackgroundTask;
import com.io.wordguard.db.simplite.tasks.SelectBackgroundTask;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class DBObject {

    public static final String OPTION_AUTOINCREAMENT = "AUTOINCREMENT";
    public static final String OPTION_UNIQUE = "UNIQUE";

    private static final String SUCCESS_FLAG = "Done";
    private static final String ERROR_FLAG = "Error";

    private Context mContext;

    private String tableName;
    private PkData primaryKey;
    private ArrayList<ColumnData> mColumns;
    private ArrayList<FkData> mForeignKeys;
    private ArrayList<FkData> mArrayForeignKeys;

    protected DBObject() {
    }

    public DBObject(Context context) {
        this.mContext = context;
        initialize();
    }

    public void Ctor(Context context) {
        this.mContext = context;
        initialize();
    }

    public Context getContext() {
        return mContext;
    }

    private void initialize() {
        getFields();
        tableName = getTableName(getClass());
    }

    protected static String prepareStringForSql(String value) {
        if (value != null && !value.equals("")) {
            return "'" + value.replace("'", "''") + "'";
        } else {
            return "''";
        }
    }

    protected static Object prepareValueForSql(Object value) {
        if (value instanceof String)
            return prepareStringForSql(String.valueOf(value));
        else if (value instanceof Boolean)
            return (boolean) value ? 1 : 0;
        else if (value instanceof Date)
            return ((Date) value).getTime();
        else if (value instanceof Integer || value instanceof Long || value instanceof Double ||
                value instanceof Float)
            return value;
        else return null;
    }

    public static String getTableName(Class<? extends DBObject> targetClass) {
        if (targetClass.isAnnotationPresent(Entity.class)) {
            return targetClass.getAnnotation(Entity.class).tableName();
        } else {
            return null;
        }
    }

    public String getTableName() {
        if (getClass().isAnnotationPresent(Entity.class)) {
            return getClass().getAnnotation(Entity.class).tableName();
        } else {
            return null;
        }
    }

    private void getFields() {
        Field[] declaredFields = getClass().getDeclaredFields();

        mColumns = new ArrayList<>();
        mForeignKeys = new ArrayList<>();
        mArrayForeignKeys = new ArrayList<>();

        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                primaryKey = new PkData(field, field.getAnnotation(PrimaryKey.class), getSetter(field));
            } else if (field.isAnnotationPresent(Column.class)) {
                mColumns.add(new ColumnData(field, getSetter(field), field.getAnnotation(Column.class)));
            } else if (field.isAnnotationPresent(ForeignKey.class)) {
                mForeignKeys.add(new FkData(field, field.getAnnotation(ForeignKey.class), getSetter(field)));
            } else if (field.isAnnotationPresent(ForeignKeyArray.class)) {
                mArrayForeignKeys.add(new FkData(field, field.getAnnotation(ForeignKeyArray.class), getSetter(field)));
            }
        }
    }

    private Field getFieldByColumn(String valueColumn) {
        for (ColumnData column : mColumns) {
            if (column.getName().equals(valueColumn))
                return column.getField();
        }
        return null;
    }

    private Method getSetter(Field field) {
        String fieldName = field.getName();
        String methodName = "set";

        if (fieldName.length() > 1) {
            methodName = methodName + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        } else {
            methodName = methodName + fieldName.toUpperCase();
        }

        try {
            return getClass().getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setObjectId(Object id) throws Exception {
        try {
            Method setter = primaryKey.getSetter();
            setter.setAccessible(true);
            setter.invoke(this, id);
        } catch (Exception e) {
            throw e;
        }
    }

    public Object getObjectId() {
        try {
            Field field = getClass().getDeclaredField(primaryKey.getField().getName());
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ContentValues getContentValues() throws Exception {
        // Get all values into a ContentValues object
        ContentValues values = new ContentValues();

        if (!primaryKey.isAutoIncrement()) {
            Object id = getObjectId();
            if (id instanceof String) {
                values.put(primaryKey.getName(), (String) id);
            }
            if (id instanceof Integer) {
                values.put(primaryKey.getName(), (Integer) id);
            } else if (id instanceof Long) {
                values.put(primaryKey.getName(), (Long) id);
            }
        }

        for (ColumnData column : mColumns) {
            Field field = getClass().getDeclaredField(column.getField().getName());
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value instanceof Integer) {
                    values.put(column.getName(), (Integer) value);
                } else if (value instanceof Long) {
                    values.put(column.getName(), (Long) value);
                } else if (value instanceof Double) {
                    values.put(column.getName(), (Double) value);
                } else if (value instanceof String) {
                    values.put(column.getName(), (String) value);
                } else if (value instanceof Boolean) {
                    values.put(column.getName(), (Boolean) value);
                } else if (value instanceof Date) {
                    values.put(column.getName(), (((Date) value)).getTime());
                }
            } catch (Exception e) {
                throw e;
            }
        }

        return values;
    }

    protected Object cursorToObject(Cursor cursor) throws Exception {
        if (cursor.getCount() > 0) {
            if (primaryKey != null) {
                Method setter = primaryKey.getSetter();
                Class type = primaryKey.getField().getType();
                setter.setAccessible(true);

                if (type == Integer.TYPE) {
                    setter.invoke(this, cursor.getInt(cursor.getColumnIndex(primaryKey.getName())));
                } else if (type == Long.TYPE) {
                    setter.invoke(this, cursor.getLong(cursor.getColumnIndex(primaryKey.getName())));
                } else if (type.getName().equals("java.lang.String")) {
                    setter.invoke(this, cursor.getString(cursor.getColumnIndex(primaryKey.getName())));
                }
            }

            for (ColumnData column : mColumns) {
                Method setter = column.getSetter();
                Class type = column.getField().getType();
                setter.setAccessible(true);

                setter.setAccessible(true);
                if (type == Integer.TYPE) {
                    setter.invoke(this, cursor.getInt(cursor.getColumnIndex(column.getName())));
                } else if (type == Long.TYPE) {
                    setter.invoke(this, cursor.getLong(cursor.getColumnIndex(column.getName())));
                } else if (type == Double.TYPE) {
                    setter.invoke(this, (cursor.getDouble(cursor.getColumnIndex(column.getName()))));
                } else if (type == Boolean.TYPE) {
                    setter.invoke(this, (cursor.getInt(cursor.getColumnIndex(column.getName()))) != 0);
                } else if (type.getName().equals("java.lang.String")) {
                    setter.invoke(this, cursor.getString(cursor.getColumnIndex(column.getName())));
                } else if (type.getName().equals("java.util.Date")) {
                    setter.invoke(this,
                            new Date(cursor.getLong(cursor.getColumnIndex(column.getName()))));

                }
            }

            for (FkData foreignKey : mForeignKeys) {
                Object entityInstance;

                Field filterValueField = getFieldByColumn(foreignKey.getValueColumn());
                filterValueField.setAccessible(true);

                entityInstance = findOne(foreignKey.getValueColumn(), filterValueField.get(this),
                        mContext, foreignKey.getFkClass());

                foreignKey.getFkClass().cast(entityInstance).Ctor(mContext);
                Method setter = foreignKey.getSetter();
                setter.setAccessible(true);
                setter.invoke(this, entityInstance);
            }

            for (FkData foreignKey : mArrayForeignKeys) {
                ArrayList<Object> entityInstance;

                Field filterValueField = getFieldByColumn(foreignKey.getValueColumn());
                filterValueField.setAccessible(true);

                entityInstance = (ArrayList<Object>) findAllByColumn(foreignKey.getValueColumn(), filterValueField.get(this),
                        foreignKey.getName(), mContext, foreignKey.getFkClass());

                for (Object object : entityInstance) {
                    foreignKey.getFkClass().cast(object).Ctor(mContext);
                }

                Method setter = foreignKey.getSetter();
                setter.setAccessible(true);
                setter.invoke(this, entityInstance);
            }
        }

        return this.getClass();
    }

    public Object create() throws Exception {
        try {
            ContentValues values = getContentValues();
            long objectId = DatabaseHelper.getWritableDatabaseInstance(mContext).insertOrThrow(tableName, null, values);
            setObjectId(objectId);
            createOrUpdateForeignKeys();
            return objectId;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int save() throws Exception {
        try {
            ContentValues values = getContentValues();
            Object id = getObjectId();
            if (id != null && id instanceof String) id = "'" + id + "'";
            int rowsAffected = DatabaseHelper.getWritableDatabaseInstance(mContext).update(tableName, values, primaryKey.getName() + " = " + id, null);
            createOrUpdateForeignKeys();
            return rowsAffected;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void createOrUpdateForeignKeys() throws Exception {
        for (FkData foreignKey : mForeignKeys) {
            Field field = getClass().getDeclaredField(foreignKey.getField().getName());
            field.setAccessible(true);
            Object fieldValue = field.get(this);

            foreignKey.getFkClass().cast(fieldValue).Ctor(mContext);
            Object objectId = foreignKey.getFkClass().cast(fieldValue).getObjectId();

            if (isNullOrZero(objectId)) {
                foreignKey.getFkClass().cast(fieldValue).create();
            } else {
                foreignKey.getFkClass().cast(fieldValue).save();
            }
        }

        for (FkData foreignKey : mArrayForeignKeys) {
            Field field = getClass().getDeclaredField(foreignKey.getField().getName());
            field.setAccessible(true);
            ArrayList<? extends DBObject> objectsArrayList =
                    (ArrayList<? extends DBObject>) field.get(this);

            for (Object object : objectsArrayList) {
                foreignKey.getFkClass().cast(object).Ctor(mContext);
                Object objectId = foreignKey.getFkClass().cast(object).getObjectId();

                if (isNullOrZero(objectId)) {
                    foreignKey.getFkClass().cast(object).create();
                } else {
                    foreignKey.getFkClass().cast(object).save();
                }
            }
        }
    }

    private boolean isNullOrZero(Object object) {
        return object == null ||
                object instanceof Integer && (int) object == 0 ||
                object instanceof Double && (double) object == 0 ||
                object instanceof Long && (long) object == 0 ||
                object instanceof String && object.equals("");
    }

    public void findById(Object id, Context context) throws Exception {
        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).query(tableName,
                null, primaryKey.getName() + " = " + prepareValueForSql(id), null,
                null, null, null);
        cursor.moveToFirst();
        try {
            cursorToObject(cursor);
        } catch (Exception e) {
            throw e;
        } finally {
            cursor.close();
        }

    }

    public int delete() throws Exception {
        Object id = getObjectId();
        if (id != null && id instanceof String) id = "'" + id + "'";
        int rowsAffected = DatabaseHelper.getWritableDatabaseInstance(mContext).delete(tableName, primaryKey.getName() + " = " + id, null);
        return rowsAffected;
    }

    private static int count(Context context, Class<? extends DBObject> targetClass) throws Exception {
        String query = "SELECT COUNT(*) FROM " + getTableName(targetClass);
        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).rawQuery(query, null);
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private static int countByColumn(Context context, Class<? extends DBObject> targetClass,
                                     String filterColumn, Object filterValue) throws Exception {
        if (filterValue instanceof String) filterValue = "'" + filterValue + "'";
        String query = "SELECT COUNT(*) FROM " + getTableName(targetClass) +
                " WHERE " + filterColumn + "=" + filterValue;
        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private static float getAverageByColumn(Context context, Class<? extends DBObject> targetClass,
                                            String avgColumn, String filterColumn, Object filterValue) throws Exception {
        if (filterValue instanceof String) filterValue = "'" + filterValue + "'";
        String query = "SELECT AVG(" + avgColumn + ") FROM " + getTableName(targetClass) +
                " WHERE " + filterColumn + "=" + filterValue;
        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).rawQuery(query, null);
        cursor.moveToFirst();
        float average = cursor.getFloat(0);
        cursor.close();
        return average;
    }

    public static List<Object> findAll(String orderByColumnName,
                                       Context context,
                                       Class<? extends DBObject> targetClass) throws Exception {

        List<Object> allObjects = new ArrayList<>();

        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).query(getTableName(targetClass),
                null, null, null, null, null, orderByColumnName);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            try {
                while (!cursor.isAfterLast()) {
                    Object currObject = targetClass.getConstructor(Context.class)
                            .newInstance(context);
                    (targetClass.cast(currObject)).cursorToObject(cursor);
                    allObjects.add(currObject);
                    cursor.moveToNext();
                }

                cursor.close();
                return allObjects;
            } catch (Exception e) {
                cursor.close();
                throw e;
            }
        } else {
            cursor.close();
            return allObjects;
        }
    }

    public static Object findOne(String filterColumnName,
                                 Object filterValue,
                                 Context context,
                                 Class<? extends DBObject> targetClass) throws Exception {
        if (filterValue instanceof Boolean)
            filterValue = (boolean) filterValue ? 1 : 0;
        else if (filterValue instanceof String)
            filterValue = "'" + filterValue + "'";

        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).query(getTableName(targetClass),
                null, filterColumnName + " = " + filterValue, null, null, null, null);

        cursor.moveToFirst();

        try {
            Object currObject = targetClass.getConstructor(Context.class).newInstance(context);
            (targetClass.cast(currObject)).cursorToObject(cursor);

            cursor.close();
            return currObject;
        } catch (Exception e) {
            cursor.close();
            throw e;
        }
    }

    public static List<Object> findAllByColumn(String columnName,
                                               Object value,
                                               String orderByColumnName,
                                               Context context,
                                               Class<? extends DBObject> targetClass) throws Exception {
        List<Object> allObjects = new ArrayList<>();

        value = prepareValueForSql(value);
        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).query(getTableName(targetClass),
                null, columnName + " = " + value, null, null, null, orderByColumnName);

        cursor.moveToFirst();

        try {
            while (!cursor.isAfterLast()) {
                Object currObject = targetClass.getConstructor(Context.class).newInstance(context);
                (targetClass.cast(currObject)).cursorToObject(cursor);
                allObjects.add(currObject);
                cursor.moveToNext();
            }

            cursor.close();
            return allObjects;
        } catch (Exception e) {
            cursor.close();
            throw e;
        }
    }

    public static List<Object> query(Context context,
                                     Class<? extends DBObject> targetClass,
                                     String[] whereColumns,
                                     String[] whereArgs,
                                     String orderByColumn) throws Exception {

        List<Object> allObjects = new ArrayList<>();
        String whereColumnsWithQuestionMark = null;

        if (whereColumns != null) {
            whereColumnsWithQuestionMark = "";
            for (int position = 0; position < whereColumns.length; position++) {
                if (position < whereColumns.length - 1) {
                    whereColumnsWithQuestionMark += (whereColumns[position] + "=? AND ");
                } else {
                    whereColumnsWithQuestionMark += (whereColumns[position] + "=?");
                }
            }
        }

        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).query(getTableName(targetClass), null,
                whereColumnsWithQuestionMark, whereArgs, null, null, orderByColumn);

        cursor.moveToFirst();

        try {
            while (!cursor.isAfterLast()) {
                Object currObject = targetClass.getConstructor(Context.class).newInstance(context);
                (targetClass.cast(currObject)).cursorToObject(cursor);
                allObjects.add(currObject);
                cursor.moveToNext();
            }

            cursor.close();
            return allObjects;
        } catch (Exception e) {
            cursor.close();
            throw e;
        }
    }

    public void createInBackground(final Context context,
                                   final boolean showProgressBar,
                                   final CRUDCallback callBack) {

//        new AsyncTask<Void, Void, Object>() {
//
//            ProgressDialog progressDialog;
//            Exception exception = null;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//
//                if (showProgressBar) {
//                    progressDialog = getProgressDialog(context);
//                    progressDialog.show();
//                }
//            }
//
//
//            @Override
//            protected Object doInBackground(Void... voids) {
//                try {
//                    return create();
//                } catch (Exception e) {
//                    exception = new Exception(e);
//                    return null;
//                }
//
//            }
//
//            @Override
//            protected void onPostExecute(Object result) {
//                super.onPostExecute(result);
//
//                if (showProgressBar) {
//                    progressDialog.dismiss();
//                }
//
//                callBack.onFinish(result, exception);
//            }
//        }.execute();
        new CRUDBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    return create();
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public void saveInBackground(final Context context,
                                 final boolean showProgressBar,
                                 final CRUDCallback callBack) {

        new CRUDBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    return save();
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public void deleteInBackground(final Context context,
                                   final boolean showProgressBar,
                                   final CRUDCallback callBack) {

        new CRUDBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    return delete();
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public void findByIdInBackground(final Context context,
                                     final boolean showProgressBar,
                                     final long objectId,
                                     final SelectCallback callBack) {
        new SelectBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    findById(objectId, context);
                    return null;
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public void countInBackground(final Context context,
                                  final boolean showProgressBar,
                                  final Class<? extends DBObject> targetClass,
                                  final SelectCallback callBack) {
        new SelectBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    return count(context, targetClass);
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public static void findAllInBackground(final String orderByColumn,
                                           final Class<? extends DBObject> targetClass,
                                           final Context context,
                                           final boolean showProgressBar,
                                           final FindAllCallback callBack) {
        new FindAllBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    setData((ArrayList)findAll(orderByColumn, context, targetClass));
                    return getData().size();
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public static void findByColumnInBackground(final boolean showProgressBar,
                                                final String columnName,
                                                final Object value,
                                                final String orderByColumnName,
                                                final Context context,
                                                final Class<? extends DBObject> targetClass,
                                                final FindAllCallback callBack) {
        new FindAllBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    setData((ArrayList)findAllByColumn(columnName, value, orderByColumnName, context, targetClass));
                    return getData().size();
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public static void queryInBackground(final Class<? extends DBObject> targetClass,
                                         final Context context,
                                         final boolean showProgressBar,
                                         final String[] whereColumns,
                                         final String[] whereArgs,
                                         final String orderByColumn,
                                         final FindAllCallback callBack) {
        new FindAllBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    setData((ArrayList)query(context, targetClass, whereColumns, whereArgs, orderByColumn));
                    return getData().size();
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public static void countByColumnInBackground(final Class<? extends DBObject> targetClass,
                                                 final Context context,
                                                 final boolean showProgressBar,
                                                 final String filterColumn,
                                                 final Object filterValue,
                                                 final SelectCallback callBack) {
        new SelectBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    return countByColumn(context, targetClass, filterColumn, filterValue);
                } catch (Exception e) {
                    setException(e);
                    return null;
                }
            }
        }.execute();
    }

    public static void getAverageByColumnInBackground(final Class<? extends DBObject> targetClass,
                                                      final Context context,
                                                      final boolean showProgressBar,
                                                      final String avgColumn,
                                                      final String filterColumn,
                                                      final Object filterValue,
                                                      final SelectCallback callBack) {
            new SelectBackgroundTask(context, showProgressBar, callBack) {
                @Override
                protected Object doInBackground(Void... voids) {
                    try {
                        return getAverageByColumn(context, targetClass, avgColumn, filterColumn, filterValue);
                    } catch (Exception e) {
                        setException(e);
                        return null;
                    }
                }
            }.execute();
    }

    public static void rawQueryInBackground(final String query, final Context context, final Class<? extends DBObject> targetClass,
                                            final boolean showProgressBar, FindAllCallback callBack) {
        new FindAllBackgroundTask(context, showProgressBar, callBack) {
            @Override
            protected Object doInBackground(Void... voids) {
                try {
                    setData((ArrayList)rawQuery(context, query, targetClass));
                    return getData().size();
                } catch (Exception e) {
                    setException(e);
                    return false;
                }
            }
        }.execute();
    }

    public static List<Object> rawQuery(Context context, String query, Class<? extends DBObject> targetClass) throws Exception {
        List<Object> allObjects = new ArrayList<>();

        Cursor cursor = DatabaseHelper.getWritableDatabaseInstance(context).rawQuery(query, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            try {
                while (!cursor.isAfterLast()) {
                    Object currObject = targetClass.getConstructor(Context.class)
                            .newInstance(context);
                    (targetClass.cast(currObject)).cursorToObject(cursor);
                    allObjects.add(currObject);
                    cursor.moveToNext();
                }

                cursor.close();
                return allObjects;
            } catch (Exception e) {
                cursor.close();
                throw e;
            }
        } else {
            cursor.close();
            return allObjects;
        }
    }

    public String getInsertCommand(Context context) {
        Ctor(context);

        String command;
        String columns = "";
        String values = "";

        if (!primaryKey.isAutoIncrement()) {
            columns = primaryKey.getName();
            values = String.valueOf(getValueFromField(primaryKey.getField(), true));
        }

        for (ColumnData columnData : mColumns) {
            if (values.equals("")) {
                columns = columnData.getName();
                Object value = getValueFromField(columnData.getField(), true);
                values = String.valueOf(value);
            } else {
                columns = columns + "," + columnData.getName();
                Object value = getValueFromField(columnData.getField(), true);
                values = values + "," + String.valueOf(value);
            }
        }

        command = "INSERT INTO " + getTableName(this.getClass()) + "(" + columns + ") VALUES (" +
                values + ");";

        return command;
    }

    public String getUpdateCommand(Context context,
                                   Condition[] setConditions,
                                   Condition[] whereConditions) {
        Ctor(context);

        String command;
        String values = "";
        String pkClause;
        String whereClause;

        pkClause = primaryKey.getName() + " = " + String.valueOf(getValueFromField(primaryKey.getField(), true));

        if (!primaryKey.isAutoIncrement()) {
            values = pkClause;
        }

        if (setConditions == null || setConditions.length == 0) {
            for (ColumnData columnData : mColumns) {
                if (values.equals("")) {
                    values = columnData.getName() + " = " +
                            String.valueOf(getValueFromField(columnData.getField(), true));
                } else {
                    values = values + "," + columnData.getName() + " = " +
                            String.valueOf(getValueFromField(columnData.getField(), true));
                }
            }
        } else {
            int index = 0;
            for (Condition condition : setConditions) {
                if (index > 0)
                    values = values + ",";
                values = values + condition.getColumn() + condition.getOperator().toString()
                        + prepareValueForSql(condition.getValue());
                index++;
            }
        }

        if (whereConditions == null || whereConditions.length == 0) {
            whereClause = " WHERE " + pkClause;
        } else {
            whereClause = " WHERE ";
            int index = 0;
            for (Condition condition : whereConditions) {
                if (index > 0)
                    whereClause = whereClause + ",";
                whereClause = whereClause + condition.getColumn() + condition.getOperator().toString()
                        + prepareValueForSql(condition.getValue());
                index++;
            }
        }
        command = "UPDATE " + getTableName(getClass()) + " SET " + values + whereClause + ";";
        return command;
    }

    private Object getValueFromField(Field field, boolean prepareStringForCommand) {
        field.setAccessible(true);
        try {
            Object value = field.get(this);
            if (value != null) {
                if (value instanceof String && prepareStringForCommand) {
                    value = prepareStringForSql((String) value);
                } else if (value instanceof Boolean) {
                    value = ((boolean) value ? 1 : 0);
                } else if (value instanceof Date) {
                    value = ((Date) value).getTime();
                }
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCreateTableCommand() throws Exception {
        String command;

        command = "CREATE TABLE " + getTableName(getClass()) + "(";
        command = command + primaryKey.getName() + " " + getColumnSQLiteType(primaryKey.getField()) +
                " PRIMARY KEY " + primaryKey.getOptionsString();

        String columns = "";
        for (ColumnData column : mColumns) {
            columns = columns + ",";
            columns = columns + column.getName() + " " + getColumnSQLiteType(column.getField()) +
                    " " + column.getOptionsString();
        }

        command = command + columns + ");";
        return command;
    }

    private String getColumnSQLiteType(Field field) throws Exception {
        if (String.class.isAssignableFrom(field.getType()))
            return "TEXT";
        else if (int.class.isAssignableFrom(field.getType()) ||
                long.class.isAssignableFrom(field.getType()) ||
                boolean.class.isAssignableFrom(field.getType()) ||
                Date.class.isAssignableFrom(field.getType()))
            return "INTEGER";
        else if (double.class.isAssignableFrom(field.getType()))
            return "REAL";
        else throw new Exception("Incompatible field type");
    }

    public static String getDropTableIfExistCommand(Class<? extends DBObject> cClass) {
        return "DROP TABLE IF EXISTS " + getTableName(cClass) + ";";
    }

    public String getDropTableIfExistCommand() {
        return "DROP TABLE IF EXISTS " + getTableName() + ";";
    }

    public static String getDeleteCommand(String tableName, Condition[] whereConditions) {
        String command = "DELETE FROM " + tableName;

        if (whereConditions != null && whereConditions.length > 0) {
            command = command + " WHERE ";
            int index = 0;
            for (Condition condition : whereConditions) {
                if (index > 0)
                    command = command + ",";
                command = command + condition.getColumn() + condition.getOperator().toString() +
                        prepareValueForSql(condition.getValue());
                index++;
            }
            command = command + ";";
        }
        return command;
    }

    public static String getUpdateCommand(String tableName,
                                          Condition[] setConditions,
                                          Condition[] whereConditions) {
        String command = null;
        if (setConditions != null && setConditions.length > 0) {
            command = "UPDATE " + tableName;
            command = command + " SET ";
            int index = 0;
            for (Condition condition : setConditions) {
                if (index > 0)
                    command = command + ",";
                command = command + condition.getColumn() + condition.getOperator().toString()
                        + prepareValueForSql(condition.getValue());
                index++;
            }

            if (whereConditions != null && whereConditions.length > 0) {
                command = command + " WHERE ";
                index = 0;
                for (Condition condition : whereConditions) {
                    if (index > 0)
                        command = command + ",";
                    command = command + condition.getColumn() + condition.getOperator().toString()
                            + prepareValueForSql(condition.getValue());
                    index++;
                }
            }
            command = command + ";";
        }
        return command;
    }
}
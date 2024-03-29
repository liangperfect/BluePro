package com.vitalong.bluetest2.greendaodb;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.vitalong.bluetest2.bean.RealDataCached;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "REAL_DATA_CACHED".
*/
public class RealDataCachedDao extends AbstractDao<RealDataCached, Long> {

    public static final String TABLENAME = "REAL_DATA_CACHED";

    /**
     * Properties of entity RealDataCached.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FormName = new Property(1, String.class, "formName", false, "FORM_NAME");
        public final static Property Time = new Property(2, String.class, "time", false, "TIME");
        public final static Property Direction = new Property(3, String.class, "direction", false, "DIRECTION");
        public final static Property RawFirst = new Property(4, String.class, "rawFirst", false, "RAW_FIRST");
        public final static Property RawSecond = new Property(5, String.class, "rawSecond", false, "RAW_SECOND");
        public final static Property DegFirst = new Property(6, String.class, "degFirst", false, "DEG_FIRST");
        public final static Property DegSecond = new Property(7, String.class, "degSecond", false, "DEG_SECOND");
        public final static Property Include = new Property(8, String.class, "include", false, "INCLUDE");
        public final static Property RealIncline = new Property(9, String.class, "realIncline", false, "REAL_INCLINE");
        public final static Property IsChecked = new Property(10, boolean.class, "isChecked", false, "IS_CHECKED");
    }


    public RealDataCachedDao(DaoConfig config) {
        super(config);
    }
    
    public RealDataCachedDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"REAL_DATA_CACHED\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"FORM_NAME\" TEXT," + // 1: formName
                "\"TIME\" TEXT," + // 2: time
                "\"DIRECTION\" TEXT," + // 3: direction
                "\"RAW_FIRST\" TEXT," + // 4: rawFirst
                "\"RAW_SECOND\" TEXT," + // 5: rawSecond
                "\"DEG_FIRST\" TEXT," + // 6: degFirst
                "\"DEG_SECOND\" TEXT," + // 7: degSecond
                "\"INCLUDE\" TEXT," + // 8: include
                "\"REAL_INCLINE\" TEXT," + // 9: realIncline
                "\"IS_CHECKED\" INTEGER NOT NULL );"); // 10: isChecked
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"REAL_DATA_CACHED\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RealDataCached entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String formName = entity.getFormName();
        if (formName != null) {
            stmt.bindString(2, formName);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(3, time);
        }
 
        String direction = entity.getDirection();
        if (direction != null) {
            stmt.bindString(4, direction);
        }
 
        String rawFirst = entity.getRawFirst();
        if (rawFirst != null) {
            stmt.bindString(5, rawFirst);
        }
 
        String rawSecond = entity.getRawSecond();
        if (rawSecond != null) {
            stmt.bindString(6, rawSecond);
        }
 
        String degFirst = entity.getDegFirst();
        if (degFirst != null) {
            stmt.bindString(7, degFirst);
        }
 
        String degSecond = entity.getDegSecond();
        if (degSecond != null) {
            stmt.bindString(8, degSecond);
        }
 
        String include = entity.getInclude();
        if (include != null) {
            stmt.bindString(9, include);
        }
 
        String realIncline = entity.getRealIncline();
        if (realIncline != null) {
            stmt.bindString(10, realIncline);
        }
        stmt.bindLong(11, entity.getIsChecked() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RealDataCached entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String formName = entity.getFormName();
        if (formName != null) {
            stmt.bindString(2, formName);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(3, time);
        }
 
        String direction = entity.getDirection();
        if (direction != null) {
            stmt.bindString(4, direction);
        }
 
        String rawFirst = entity.getRawFirst();
        if (rawFirst != null) {
            stmt.bindString(5, rawFirst);
        }
 
        String rawSecond = entity.getRawSecond();
        if (rawSecond != null) {
            stmt.bindString(6, rawSecond);
        }
 
        String degFirst = entity.getDegFirst();
        if (degFirst != null) {
            stmt.bindString(7, degFirst);
        }
 
        String degSecond = entity.getDegSecond();
        if (degSecond != null) {
            stmt.bindString(8, degSecond);
        }
 
        String include = entity.getInclude();
        if (include != null) {
            stmt.bindString(9, include);
        }
 
        String realIncline = entity.getRealIncline();
        if (realIncline != null) {
            stmt.bindString(10, realIncline);
        }
        stmt.bindLong(11, entity.getIsChecked() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RealDataCached readEntity(Cursor cursor, int offset) {
        RealDataCached entity = new RealDataCached( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // formName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // time
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // direction
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // rawFirst
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // rawSecond
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // degFirst
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // degSecond
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // include
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // realIncline
            cursor.getShort(offset + 10) != 0 // isChecked
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RealDataCached entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFormName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDirection(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRawFirst(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRawSecond(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDegFirst(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDegSecond(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setInclude(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setRealIncline(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIsChecked(cursor.getShort(offset + 10) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RealDataCached entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RealDataCached entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(RealDataCached entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

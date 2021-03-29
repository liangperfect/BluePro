package com.vitalong.inclinometer.greendaodb;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.vitalong.inclinometer.bean.RealDataCached;
import com.vitalong.inclinometer.bean.BoreholeInfoTable;
import com.vitalong.inclinometer.bean.SurveyDataTable;

import com.vitalong.inclinometer.greendaodb.RealDataCachedDao;
import com.vitalong.inclinometer.greendaodb.BoreholeInfoTableDao;
import com.vitalong.inclinometer.greendaodb.SurveyDataTableDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig realDataCachedDaoConfig;
    private final DaoConfig boreholeInfoTableDaoConfig;
    private final DaoConfig surveyDataTableDaoConfig;

    private final RealDataCachedDao realDataCachedDao;
    private final BoreholeInfoTableDao boreholeInfoTableDao;
    private final SurveyDataTableDao surveyDataTableDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        realDataCachedDaoConfig = daoConfigMap.get(RealDataCachedDao.class).clone();
        realDataCachedDaoConfig.initIdentityScope(type);

        boreholeInfoTableDaoConfig = daoConfigMap.get(BoreholeInfoTableDao.class).clone();
        boreholeInfoTableDaoConfig.initIdentityScope(type);

        surveyDataTableDaoConfig = daoConfigMap.get(SurveyDataTableDao.class).clone();
        surveyDataTableDaoConfig.initIdentityScope(type);

        realDataCachedDao = new RealDataCachedDao(realDataCachedDaoConfig, this);
        boreholeInfoTableDao = new BoreholeInfoTableDao(boreholeInfoTableDaoConfig, this);
        surveyDataTableDao = new SurveyDataTableDao(surveyDataTableDaoConfig, this);

        registerDao(RealDataCached.class, realDataCachedDao);
        registerDao(BoreholeInfoTable.class, boreholeInfoTableDao);
        registerDao(SurveyDataTable.class, surveyDataTableDao);
    }
    
    public void clear() {
        realDataCachedDaoConfig.clearIdentityScope();
        boreholeInfoTableDaoConfig.clearIdentityScope();
        surveyDataTableDaoConfig.clearIdentityScope();
    }

    public RealDataCachedDao getRealDataCachedDao() {
        return realDataCachedDao;
    }

    public BoreholeInfoTableDao getBoreholeInfoTableDao() {
        return boreholeInfoTableDao;
    }

    public SurveyDataTableDao getSurveyDataTableDao() {
        return surveyDataTableDao;
    }

}

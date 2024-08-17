package com.vitalong.inclinometer.Utils;

import com.vitalong.inclinometer.bean.BoreholeInfoTable;
import com.vitalong.inclinometer.greendaodb.BoreholeInfoTableDao;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class GreenDaoUtil {


    /**
     * 批量插入数据
     *
     * @param dao      DAO 对象，用于操作数据库
     * @param dataList 要插入的数据列表
     */
    public static void batchInsert(BoreholeInfoTableDao dao, List<BoreholeInfoTable> dataList) {
        // 获取可写的数据库实例
        Database db = dao.getDatabase();
        // 开始事务
        db.beginTransaction();
        try {
            for (BoreholeInfoTable data : dataList) {
                // 插入数据
                dao.insert(data);
            }
            // 提交事务
            db.setTransactionSuccessful();
        } finally {
            // 结束事务
            db.endTransaction();
        }
    }
}

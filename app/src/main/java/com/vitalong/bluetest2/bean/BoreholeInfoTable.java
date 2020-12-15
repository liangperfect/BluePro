package com.vitalong.bluetest2.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @Package: com.vitalong.bluetest2.bean
 * @Description:
 * @Author: 亮
 * @CreateDate: 2020/12/12 13:47
 * @UpdateUser: 更新者
 */
@Entity
public class BoreholeInfoTable {
    @Id
    private Long id; //自增id
    private String constructionSite; //工地名称
    private String holeName; //孔号
    private String a0Des; //A0方向描述
    private float topValue;//顶部深度
    private float bottomValue;//底部深度
    private int pointsNumber;//测量点数
    private float duration;
    private float csvFileName; //关键Key

    @Generated(hash = 1068935535)
    public BoreholeInfoTable(Long id, String constructionSite, String holeName,
                             String a0Des, float topValue, float bottomValue, int pointsNumber,
                             float duration, float csvFileName) {
        this.id = id;
        this.constructionSite = constructionSite;
        this.holeName = holeName;
        this.a0Des = a0Des;
        this.topValue = topValue;
        this.bottomValue = bottomValue;
        this.pointsNumber = pointsNumber;
        this.duration = duration;
        this.csvFileName = csvFileName;
    }

    @Generated(hash = 2015056221)
    public BoreholeInfoTable() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConstructionSite() {
        return constructionSite;
    }

    public void setConstructionSite(String constructionSite) {
        this.constructionSite = constructionSite;
    }

    public String getHoleName() {
        return holeName;
    }

    public void setHoleName(String holeName) {
        this.holeName = holeName;
    }

    public String getA0Des() {
        return a0Des;
    }

    public void setA0Des(String a0Des) {
        this.a0Des = a0Des;
    }

    public float getTopValue() {
        return topValue;
    }

    public void setTopValue(float topValue) {
        this.topValue = topValue;
    }

    public float getBottomValue() {
        return bottomValue;
    }

    public void setBottomValue(float bottomValue) {
        this.bottomValue = bottomValue;
    }

    public int getPointsNumber() {
        return pointsNumber;
    }

    public void setPointsNumber(int pointsNumber) {
        this.pointsNumber = pointsNumber;
    }

    public float getDuration() {
        return this.duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(float csvFileName) {
        this.csvFileName = csvFileName;
    }
}

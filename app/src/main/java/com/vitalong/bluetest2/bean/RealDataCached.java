package com.vitalong.bluetest2.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 缓存实时数据的Table
 */
@Entity
public class RealDataCached {
    @Id
    private Long id;
    private String formName;
    private String time;
    private String direction; // 1-3的direction
    private String rawFirst;
    private String rawSecond;
    private String include;

    @Generated(hash = 376361347)
    public RealDataCached(Long id, String formName, String time, String direction,
                          String rawFirst, String rawSecond, String include) {
        this.id = id;
        this.formName = formName;
        this.time = time;
        this.direction = direction;
        this.rawFirst = rawFirst;
        this.rawSecond = rawSecond;
        this.include = include;
    }

    @Generated(hash = 1334578809)
    public RealDataCached() {
    }

    public RealDataCached(String formName, String time, String direction,
                          String rawFirst, String rawSecond, String include) {
        this.formName = formName;
        this.time = time;
        this.direction = direction;
        this.rawFirst = rawFirst;
        this.rawSecond = rawSecond;
        this.include = include;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRawFirst() {
        return rawFirst;
    }

    public void setRawFirst(String rawFirst) {
        this.rawFirst = rawFirst;
    }

    public String getRawSecond() {
        return rawSecond;
    }

    public void setRawSecond(String rawSecond) {
        this.rawSecond = rawSecond;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String[] toStrArray() {

        return new String[]{time, direction, rawFirst, rawSecond, include, "", "", "", ""};
    }
}


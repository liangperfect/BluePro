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
    private String realIncline;
    private boolean isChecked = false;//用于手动合并CSV的时候，给item做判断

    @Generated(hash = 1035144428)
    public RealDataCached(Long id, String formName, String time, String direction, String rawFirst,
            String rawSecond, String include, String realIncline, boolean isChecked) {
        this.id = id;
        this.formName = formName;
        this.time = time;
        this.direction = direction;
        this.rawFirst = rawFirst;
        this.rawSecond = rawSecond;
        this.include = include;
        this.realIncline = realIncline;
        this.isChecked = isChecked;
    }

    @Generated(hash = 1334578809)
    public RealDataCached() {
    }

    public RealDataCached(String formName, String time, String direction,
                          String rawFirst, String rawSecond, String include, String realIncline) {
        this.formName = formName;
        this.time = time;
        this.direction = direction;
        this.rawFirst = rawFirst;
        this.rawSecond = rawSecond;
        this.include = include;
        this.realIncline = realIncline;
    }

    public String[] toStrArray() {

        return new String[]{time, direction, rawFirst, rawSecond, include, "", "", "", ""};
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormName() {
        return this.formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRawFirst() {
        return this.rawFirst;
    }

    public void setRawFirst(String rawFirst) {
        this.rawFirst = rawFirst;
    }

    public String getRawSecond() {
        return this.rawSecond;
    }

    public void setRawSecond(String rawSecond) {
        this.rawSecond = rawSecond;
    }

    public String getInclude() {
        return this.include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getRealIncline() {
        return this.realIncline;
    }

    public void setRealIncline(String realIncline) {
        this.realIncline = realIncline;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}

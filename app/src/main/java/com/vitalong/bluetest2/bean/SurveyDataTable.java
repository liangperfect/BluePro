package com.vitalong.bluetest2.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @Package: com.vitalong.bluetest2.bean
 * @Description:
 * @Author: 亮
 * @CreateDate: 2020/12/15 15:33
 * @UpdateUser: 更新者
 */
@Entity
public class SurveyDataTable {

    @Id
    private Long id; //自增id
    private String csvFileName;
    private String depth;//当前高度
    private String rawA0;
    private String rawA180;
    private String rawB0;
    private String rawB180;
    private String A0mm;
    private String A180mm;
    private String B0mm;
    private String B180mm;
    private String checkSumA;
    private String checkSumB;

    @Generated(hash = 1962405112)
    public SurveyDataTable(Long id, String csvFileName, String depth, String rawA0,
                           String rawA180, String rawB0, String rawB180, String A0mm,
                           String A180mm, String B0mm, String B180mm, String checkSumA,
                           String checkSumB) {
        this.id = id;
        this.csvFileName = csvFileName;
        this.depth = depth;
        this.rawA0 = rawA0;
        this.rawA180 = rawA180;
        this.rawB0 = rawB0;
        this.rawB180 = rawB180;
        this.A0mm = A0mm;
        this.A180mm = A180mm;
        this.B0mm = B0mm;
        this.B180mm = B180mm;
        this.checkSumA = checkSumA;
        this.checkSumB = checkSumB;
    }

    public SurveyDataTable(String csvFileName, String depth, String rawA0,
                           String rawA180, String rawB0, String rawB180, String A0mm,
                           String A180mm, String B0mm, String B180mm, String checkSumA,
                           String checkSumB) {
        this.csvFileName = csvFileName;
        this.depth = depth;
        this.rawA0 = rawA0;
        this.rawA180 = rawA180;
        this.rawB0 = rawB0;
        this.rawB180 = rawB180;
        this.A0mm = A0mm;
        this.A180mm = A180mm;
        this.B0mm = B0mm;
        this.B180mm = B180mm;
        this.checkSumA = checkSumA;
        this.checkSumB = checkSumB;
    }

    @Generated(hash = 1972498340)
    public SurveyDataTable() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCsvFileName() {
        return this.csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public String getDepth() {
        return this.depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getRawA0() {
        return this.rawA0;
    }

    public void setRawA0(String rawA0) {
        this.rawA0 = rawA0;
    }

    public String getRawA180() {
        return this.rawA180;
    }

    public void setRawA180(String rawA180) {
        this.rawA180 = rawA180;
    }

    public String getRawB0() {
        return this.rawB0;
    }

    public void setRawB0(String rawB0) {
        this.rawB0 = rawB0;
    }

    public String getRawB180() {
        return this.rawB180;
    }

    public void setRawB180(String rawB180) {
        this.rawB180 = rawB180;
    }

    public String getA0mm() {
        return this.A0mm;
    }

    public void setA0mm(String A0mm) {
        this.A0mm = A0mm;
    }

    public String getA180mm() {
        return this.A180mm;
    }

    public void setA180mm(String A180mm) {
        this.A180mm = A180mm;
    }

    public String getB0mm() {
        return this.B0mm;
    }

    public void setB0mm(String B0mm) {
        this.B0mm = B0mm;
    }

    public String getB180mm() {
        return this.B180mm;
    }

    public void setB180mm(String B180mm) {
        this.B180mm = B180mm;
    }

    public String getCheckSumA() {
        return this.checkSumA;
    }

    public void setCheckSumA(String checkSumA) {
        this.checkSumA = checkSumA;
    }

    public String getCheckSumB() {
        return this.checkSumB;
    }

    public void setCheckSumB(String checkSumB) {
        this.checkSumB = checkSumB;
    }

}

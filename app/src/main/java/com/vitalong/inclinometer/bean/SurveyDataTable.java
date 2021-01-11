package com.vitalong.inclinometer.bean;

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
    private String csvFileName;//主键
    private String depth;//当前高度
    private String A0mm;
    private String A180mm;
    private String B0mm;
    private String B180mm;
    private String A0Deg;
    private String A180Deg;
    private String B0Deg;
    private String B180Deg;
    private String A0Raw;
    private String A180Raw;
    private String B0Raw;
    private String B180Raw;
    private String CheckSumA;
    private String CheckSumB;

    @Generated(hash = 142778864)
    public SurveyDataTable(Long id, String csvFileName, String depth, String A0mm,
                           String A180mm, String B0mm, String B180mm, String A0Deg, String A180Deg,
                           String B0Deg, String B180Deg, String A0Raw, String A180Raw,
                           String B0Raw, String B180Raw, String CheckSumA, String CheckSumB) {
        this.id = id;
        this.csvFileName = csvFileName;
        this.depth = depth;
        this.A0mm = A0mm;
        this.A180mm = A180mm;
        this.B0mm = B0mm;
        this.B180mm = B180mm;
        this.A0Deg = A0Deg;
        this.A180Deg = A180Deg;
        this.B0Deg = B0Deg;
        this.B180Deg = B180Deg;
        this.A0Raw = A0Raw;
        this.A180Raw = A180Raw;
        this.B0Raw = B0Raw;
        this.B180Raw = B180Raw;
        this.CheckSumA = CheckSumA;
        this.CheckSumB = CheckSumB;
    }

    public SurveyDataTable(String csvFileName, String depth, String A0mm,
                           String A180mm, String B0mm, String B180mm, String A0Deg, String A180Deg,
                           String B0Deg, String B180Deg, String A0Raw, String A180Raw,
                           String B0Raw, String B180Raw, String CheckSumA, String CheckSumB) {
        this.csvFileName = csvFileName;
        this.depth = depth;
        this.A0mm = A0mm;
        this.A180mm = A180mm;
        this.B0mm = B0mm;
        this.B180mm = B180mm;
        this.A0Deg = A0Deg;
        this.A180Deg = A180Deg;
        this.B0Deg = B0Deg;
        this.B180Deg = B180Deg;
        this.A0Raw = A0Raw;
        this.A180Raw = A180Raw;
        this.B0Raw = B0Raw;
        this.B180Raw = B180Raw;
        this.CheckSumA = CheckSumA;
        this.CheckSumB = CheckSumB;
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

    public String getA0Deg() {
        return this.A0Deg;
    }

    public void setA0Deg(String A0Deg) {
        this.A0Deg = A0Deg;
    }

    public String getA180Deg() {
        return this.A180Deg;
    }

    public void setA180Deg(String A180Deg) {
        this.A180Deg = A180Deg;
    }

    public String getB0Deg() {
        return this.B0Deg;
    }

    public void setB0Deg(String B0Deg) {
        this.B0Deg = B0Deg;
    }

    public String getB180Deg() {
        return this.B180Deg;
    }

    public void setB180Deg(String B180Deg) {
        this.B180Deg = B180Deg;
    }

    public String getA0Raw() {
        return this.A0Raw;
    }

    public void setA0Raw(String A0Raw) {
        this.A0Raw = A0Raw;
    }

    public String getA180Raw() {
        return this.A180Raw;
    }

    public void setA180Raw(String A180Raw) {
        this.A180Raw = A180Raw;
    }

    public String getB0Raw() {
        return this.B0Raw;
    }

    public void setB0Raw(String B0Raw) {
        this.B0Raw = B0Raw;
    }

    public String getB180Raw() {
        return this.B180Raw;
    }

    public void setB180Raw(String B180Raw) {
        this.B180Raw = B180Raw;
    }

    public String getCheckSumA() {
        return this.CheckSumA;
    }

    public void setCheckSumA(String CheckSumA) {
        this.CheckSumA = CheckSumA;
    }

    public String getCheckSumB() {
        return this.CheckSumB;
    }

    public void setCheckSumB(String CheckSumB) {
        this.CheckSumB = CheckSumB;
    }

}

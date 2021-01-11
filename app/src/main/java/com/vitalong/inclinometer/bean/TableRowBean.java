package com.vitalong.inclinometer.bean;

public class TableRowBean {

    private String column1;
    private String column2;
    private String column3;
    private String column4;
    private String column5;
    private String column6;
    private String column7;
    private String column8;
    private String column9;

    public TableRowBean(String c1, String c2, String c3, String c4, String c5, String c6, String c7, String c8, String c9) {
        this.column1 = c1;
        this.column2 = c2;
        this.column3 = c3;
        this.column4 = c4;
        this.column5 = c5;
        this.column6 = c6;
        this.column7 = c7;
        this.column8 = c8;
        this.column9 = c9;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getColumn4() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4 = column4;
    }

    public String getColumn5() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5 = column5;
    }

    public String getColumn6() {
        return column6;
    }

    public void setColumn6(String column6) {
        this.column6 = column6;
    }

    public String getColumn7() {
        return column7;
    }

    public void setColumn7(String column7) {
        this.column7 = column7;
    }

    public String getColumn8() {
        return column8;
    }

    public void setColumn8(String column8) {
        this.column8 = column8;
    }

    public String getColumn9() {
        return column9;
    }

    public void setColumn9(String column9) {
        this.column9 = column9;
    }

    public String[] toStrArray() {

        return new String[]{column1, column2, column3, column4, column5, column6, column7, column8, column9};
    }

    public String toSaveString() {
        return column1 + "," + column2 + "," + column3 + "," + column4 + "," + column5 + "," + column6 + "," + column7 + "," + column8 + "," + column9 + "/n";
    }

}

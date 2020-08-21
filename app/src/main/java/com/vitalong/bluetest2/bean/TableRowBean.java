package com.vitalong.bluetest2.bean;

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
        this.column8 = c9;
    }

    public String[] toStrArray() {

        return new String[]{column1, column2, column3, column4, column5, column6, column7, column8, column9};
    }
}

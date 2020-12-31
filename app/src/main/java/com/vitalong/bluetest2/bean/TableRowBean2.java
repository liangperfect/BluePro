package com.vitalong.bluetest2.bean;

public class TableRowBean2 {

    private String column1;
    private String column2;
    private String column3;
    private String column4;
    private String column5;
    private String column6;
    private String column7;
    private String column8;
    private String column9;
    private String column10;
    private String column11;
    private String column12;
    private String column13;
    private String column14;
    private String column15;

    public TableRowBean2(String c1, String c2, String c3, String c4, String c5, String c6, String c7, String c8, String c9, String c10, String c11, String c12, String c13, String c14, String c15) {
        this.column1 = c1;
        this.column2 = c2;
        this.column3 = c3;
        this.column4 = c4;
        this.column5 = c5;
        this.column6 = c6;
        this.column7 = c7;
        this.column8 = c8;
        this.column9 = c9;
        this.column10 = c10;
        this.column11 = c11;
        this.column12 = c12;
        this.column13 = c13;
        this.column14 = c14;
        this.column15 = c15;
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

    public String getColumn10() {
        return column10;
    }

    public void setColumn10(String column10) {
        this.column10 = column10;
    }

    public String getColumn11() {
        return column11;
    }

    public void setColumn11(String column11) {
        this.column11 = column11;
    }

    public String getColumn12() {
        return column12;
    }

    public void setColumn12(String column12) {
        this.column12 = column12;
    }

    public String getColumn13() {
        return column13;
    }

    public void setColumn13(String column13) {
        this.column13 = column13;
    }

    public String getColumn14() {
        return column14;
    }

    public void setColumn14(String column14) {
        this.column14 = column14;
    }

    public String getColumn15() {
        return column15;
    }

    public void setColumn15(String column15) {
        this.column15 = column15;
    }

    public String[] toStrArray() {

        return new String[]{column1, column2, column3, column4, column5, column6, column7, column8, column9, column10, column11, column12, column13, column14, column15};
    }

    public String toSaveString() {
        return column1 + "," + column2 + "," + column3 + "," + column4 + "," + column5 + "," + column6 + "," + column7 + "," + column8 + "," + column9 + "," + column10 + "," + column11 + "," + column12 + "," + column13 + "," + column14 + "," + column15+
        "/n";
    }

}

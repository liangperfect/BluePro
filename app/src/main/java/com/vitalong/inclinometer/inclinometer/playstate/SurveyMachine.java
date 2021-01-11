package com.vitalong.inclinometer.inclinometer.playstate;

public interface SurveyMachine {
    /**
     * 改变界面上的数值 例如井深 90 - 0.5 = 89.5
     *
     * @param isZero 0度的测量，180度的测量
     */
    public void changeDepthNumber(boolean isZero);

    /**
     * 将数据保存到数据库中去
     */
    public void saveDatasInDB();

    /**
     * 将数据保存到csv文件中去
     */
    public void saveCsvData();
}

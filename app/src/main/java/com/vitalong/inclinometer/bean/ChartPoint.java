package com.vitalong.inclinometer.bean;

/**
 * @Package: com.vitalong.inclinometer.bean
 * @Description:
 * @Author: 亮
 * @CreateDate: 2021/1/14 15:58
 * @UpdateUser: 更新者
 */
public class ChartPoint {

    private int x;
    private int y;
    private String name;
    private String title;

    public ChartPoint(int x, int y, String name, String title) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.title = title;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

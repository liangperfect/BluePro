package com.vitalong.bluetest2.bean;

/**
 * @Package: com.vitalong.bluetest2.bean
 * @Description:
 * @Author: 亮
 * @CreateDate: 2021/1/5 17:03
 * @UpdateUser: 更新者
 */
public class HoleBean {

    String holeName;

    boolean isChecked;

    public HoleBean(String holeName, boolean isChecked) {
        this.holeName = holeName;
        this.isChecked = isChecked;
    }

    public String getHoleName() {
        return holeName;
    }

    public void setHoleName(String holeName) {
        this.holeName = holeName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

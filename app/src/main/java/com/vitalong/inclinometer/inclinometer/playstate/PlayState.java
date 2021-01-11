package com.vitalong.inclinometer.inclinometer.playstate;

/**
 * @Package: com.vitalong.inclinometer.inclinometer
 * @Description: 倾度管，实时界面的数据，数据获取状态切换接口
 * @Author: 亮
 * @CreateDate: 2021/1/11 13:57
 * @UpdateUser: 更新者
 */
public interface PlayState {

    public void play();

    public void saveData();

    public void changeState();

    public void stop();
}
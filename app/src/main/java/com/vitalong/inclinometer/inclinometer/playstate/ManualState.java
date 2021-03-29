package com.vitalong.inclinometer.inclinometer.playstate;

import com.vitalong.inclinometer.inclinometer.Survey2Activity;

/**
 * @Package: com.vitalong.inclinometer.inclinometer.playstate
 * @Description: 手动状态下play及数据存储
 * @Author: 亮
 * @CreateDate: 2021/1/11 14:31
 * @UpdateUser: 更新者
 */
public class ManualState implements PlayState {
    Survey2Activity surveyMachine;

    public ManualState(Survey2Activity surveyMachine) {
        this.surveyMachine = surveyMachine;
    }

    @Override
    public void play() {

    }

    @Override
    public void saveData() {

    }

    @Override
    public void changeState() {

//        surveyMachine.currState = surveyMachine.getManualState();
    }

    @Override
    public void stop() {

    }
}

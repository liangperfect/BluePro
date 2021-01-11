package com.vitalong.inclinometer.inclinometer.playstate;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.vitalong.inclinometer.inclinometer.Survey2Activity;

/**
 * @Package: com.vitalong.inclinometer.inclinometer.playstate
 * @Description: 自动状态下数据展示及存储
 * @Author: 亮
 * @CreateDate: 2021/1/11 14:31
 * @UpdateUser: 更新者
 */
public class AutoState implements PlayState {

    Survey2Activity surveyMachine;

    public AutoState(Survey2Activity surveyMachine) {
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

    //宣传关闭
    @Override
    public void stop() {

    }

    class TimerHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //内部循环
        }
    }
}

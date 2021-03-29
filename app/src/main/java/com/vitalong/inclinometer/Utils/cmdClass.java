package com.vitalong.inclinometer.Utils;


public class cmdClass {
    static String sn="0005";

    /**
     * 获取sn
     * @return
     */
    public static String getSn(){
        String cmd=Read(sn);
        return getCrcString(cmd);
    }

    /**
     * 命令加上crc码
     * @param cmd
     * @return
     */
    public static String getCrcString(String cmd){
        int length=cmd.length();
        byte[] data= ByteTransformUtil.hexToByteArray(cmd);
        int crc= CRC16CheckUtil.CRC16_Check(data,data.length);
        String md=CRC16CheckUtil.bytesToHexString(CRC16CheckUtil.intToBytes2(crc));
        if(md.length()>4)md=md.substring(md.length()-4);
        cmd+=md.substring(2)+md.substring(0,2);
        return cmd;
    }

    /**
     * 读取寄存器命令
     * @param cmd
     * @return
     */
    static String Read(String cmd){
        return "0103"+cmd+"0002";
    }
    static String range="000B";
    /**
     * 量程范围
     * @return
     */
    public static String getRange(){
        String cmd=Read(range);
        return getCrcString(cmd);
    }

    static String sensorType="000A";
    /**
     * 单轴双轴
     * @return
     */
    public static String getSensorType(){
        String cmd=Read(sensorType);
        return getCrcString(cmd);
    }

    static String angle1="0000";
    /**
     * 角度1
     * @return
     */
    public static String getAngle1(){
        String cmd=Read(angle1);
        return getCrcString(cmd);
    }
    public static String getMode(){
        String cmd="010300060002240A";
        return cmd;
    }
    static String angle2="0002";
    /**
     * 角度2
     * @return
     */
    public static String getAngle2(){
        String cmd=Read(angle2);
        return getCrcString(cmd);
    }
    public static String getAngle(){
        String cmd="010300000006";
        return getCrcString(cmd);
    }
    static String address="0004";
    /**
     * 通信地址
     * @return
     */
    public static String getAddress(){
        String cmd=Read(address);
        return getCrcString(cmd);
    }


    /**
     * 寄存器A4个值
     * @param Nm 寄存器编号 00 01 02 03分别四个地址
     * @return
     */
    public static String getParamsA(String Nm){
        String paramsA="010301"+Nm+"0002";
        return getCrcString(paramsA);
    }

    /**
     * 寄存器B4个值
     * @param Nm 寄存器编号
     * @return
     */
    public static String getParamsB(String Nm){
        String paramsB="010302"+Nm+"0002";
        return getCrcString(paramsB);
    }

    /**
     * 写参数A
     * @param Cmd
     * @return
     */
    public static String getParamsCmdA(String Cmd){
        String paramsB="011501000004"+Cmd;
        return getCrcString(paramsB);
    }
    public static String getParamsCmd(String Cmd,String num){
        String paramsB="0115010"+num+"0001"+Cmd;//获取参数
        return getCrcString(paramsB);//crc校验
    }
    /***
     * 写参数B
     * @param Cmd
     * @return
     */
    public static String getParamsCmdB(String Cmd){
        String paramsB="011501040004"+Cmd;
        return getCrcString(paramsB);
    }
}

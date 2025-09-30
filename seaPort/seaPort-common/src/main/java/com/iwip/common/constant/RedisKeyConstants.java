package com.iwip.common.constant;

/**
 * @author taoqz
 * @create 2025-04-13
 */
public class RedisKeyConstants {

    /**
     * 上次累计重量 %s 泊位
     * PLC_1#_lastTimeWeight
     */
    public static final String LAST_TIME_WEIGHT = "PLC_" + "%s" + "_lastTimeWeight";

    /**
     * PLC读取计算计划单的实时重量
     * PLC:22810_1#
     */
    public static final String PLC_PLAN_REAL_WEIGHT = "PLC:" + "%s" + "_" + "%s";

    /**
     * PLC读取每小时的重量
     * PLC:22810_1#
     */
    public static final String PLC_HOUR_PLAN_REAL_WEIGHT = "PLC_Hour:" + "%s" + "_" + "%s";

    /**
     * 大屏进度异常原因
     */
    public static final String PROGRESS_EXCEPTION_REASON = "EXCEPTION:REASON:"+ "%s:"+ "%s" + "_" + "%s";

    public static void main(String[] args) {
        System.out.println(String.format(LAST_TIME_WEIGHT, "1#"));
        System.out.println(String.format(PLC_PLAN_REAL_WEIGHT, "28100", "1#"));
        System.out.println(String.format(PROGRESS_EXCEPTION_REASON, "type1","28100", "1#"));
    }

}

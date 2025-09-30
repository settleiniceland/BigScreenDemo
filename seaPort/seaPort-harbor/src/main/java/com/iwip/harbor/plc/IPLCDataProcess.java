package com.iwip.harbor.plc;

import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Types.OperateResultExOne;
import com.iwip.common.constant.RedisKeyConstants;
import com.iwip.common.core.redis.RedisCache;
import com.iwip.common.utils.spring.SpringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author taoqz
 * @create 2025-04-14
 */
public interface IPLCDataProcess {

    /**
     * 读取 plc数据
     * @return
     */
    Map<String, BigDecimal> readPlc();


    /**
     * 读取 PLC 指定地址的 Float 并转换为 BigDecimal
     */
    default BigDecimal readFloatValue(NetworkDeviceBase networkDeviceBase, String variableKey) {
        OperateResultExOne<Float> result = networkDeviceBase.ReadFloat(variableKey);
        return Optional.ofNullable(result.Content)
                .map(BigDecimal::new)
                .map(BigDecimal::longValue)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }


    /**
     * 读取plc最新累计数据 存入到Redis中
     * @param berthName
     */
    default void setLastTimeWeight(String berthName) {
        Map<String, BigDecimal> plcData = readPlc();
        BigDecimal plcSumWeightDate = plcData.get(PLCConstans.SUM_WEIGHT_DATE);
        String lastTimeWeightRedisKey = String.format(RedisKeyConstants.LAST_TIME_WEIGHT, berthName);

        RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
        if (plcSumWeightDate.compareTo(BigDecimal.ZERO) ==0){
            return;
        }
        redisCache.setCacheObject(lastTimeWeightRedisKey, plcSumWeightDate);
    }

}

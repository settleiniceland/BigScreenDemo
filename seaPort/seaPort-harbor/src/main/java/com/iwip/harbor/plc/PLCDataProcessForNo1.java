package com.iwip.harbor.plc;

import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Profinet.Siemens.SiemensPLCS;
import HslCommunication.Profinet.Siemens.SiemensS7Net;
import com.iwip.common.constant.RedisKeyConstants;
import com.iwip.common.core.redis.RedisCache;
import com.iwip.common.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 1# 泊位读取 PLC
 *
 * @author taoqz
 * @create 2025-04-14
 */
@Slf4j
@Service
public class PLCDataProcessForNo1 implements IPLCDataProcess {

    private static final String PLC_NO = "1#";
    private static final String PLC_IP = "10.60.0.4";
    private static final String PLC_REAL_TIME_ADDRESS = "DB2.DBD4"; // 实时流量
    private static final String PLC_SUM_WEIGHT_ADDRESS = "DB2.DBD12"; // 累计吨量
    private static final int PLC_PORT = 102;

    private static final SiemensS7Net siemensS7Net;

    static {
        // 静态代码块初始化，避免重复创建实例
        siemensS7Net = new SiemensS7Net(SiemensPLCS.S1500);
        siemensS7Net.setIpAddress(PLC_IP);
        siemensS7Net.setPort(PLC_PORT);
    }

    @Override
    public Map<String, BigDecimal> readPlc() {
        EnvironmentSwitch environmentSwitch = SpringUtils.getBean(EnvironmentSwitch.class);
        if (!environmentSwitch.isProd()) {
            return Collections.emptyMap();
        }
        OperateResult result = siemensS7Net.ConnectServer();
        if (result == null || !result.IsSuccess) {
            log.error("PLC {} 连接失败: {}", PLC_NO, result != null ? result.ToMessageShowString() : "未知错误");
            return Collections.emptyMap(); // 连接失败，返回空数据
        }

        log.info("PLC: {} 连接成功", PLC_NO);

        // 读取数据
        // 实时流量
        BigDecimal realTimeDate = readFloatValue(siemensS7Net, PLC_REAL_TIME_ADDRESS);
        // 累计吨量
        BigDecimal sumWeightDate = readFloatValue(siemensS7Net, PLC_SUM_WEIGHT_ADDRESS);

        // 返回数据
        Map<String, BigDecimal> dataMap = new HashMap<>();
        dataMap.put(PLCConstans.REAL_TIME_DATE, realTimeDate);  // 实时流量
        dataMap.put(PLCConstans.SUM_WEIGHT_DATE, sumWeightDate); // 累计吨量

        return dataMap;
    }

}

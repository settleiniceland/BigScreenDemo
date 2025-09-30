package com.iwip.harbor.plc;

import com.iwip.common.utils.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class PLCDataProcessForNo4 implements IPLCDataProcess {

    private static final String PLC_NO = "4#";

    private static final String OPC_ADDRESS = "opc.tcp://192.168.93.14:18399";
    // 累计重量
    private static final int OPC_SUM_WEIGHT_NAMESPACE_INDEX = 1;
    private static final String OPC_SUM_WEIGHT_IDENTIFIER = "gw1";
    // 实时流量
    private static final int OPC_REAL_TIME_NAMESPACE_INDEX = 1;
    private static final String OPC_REAL_TIME_IDENTIFIER = "gw2";

    private OpcUaClient client;

    @Override
    public Map<String, BigDecimal> readPlc() {
        EnvironmentSwitch environmentSwitch = SpringUtils.getBean(EnvironmentSwitch.class);
        if (!environmentSwitch.isProd()) {
            return Collections.emptyMap();
        }

        try {
            ensureConnected(); // 确保连接活跃

            Map<String, BigDecimal> dataMap = new HashMap<>();
            dataMap.put(PLCConstans.SUM_WEIGHT_DATE, readOpcValue(OPC_SUM_WEIGHT_NAMESPACE_INDEX, OPC_SUM_WEIGHT_IDENTIFIER, "累计吨量"));
            dataMap.put(PLCConstans.REAL_TIME_DATE, readOpcValue(OPC_REAL_TIME_NAMESPACE_INDEX, OPC_REAL_TIME_IDENTIFIER, "实时流量"));

            return dataMap;
        } catch (Exception e) {
            log.error("泊位 OPC {} 读取失败: {}", PLC_NO, e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * 通用读取方法，返回 BigDecimal 值，失败返回 BigDecimal.ZERO
     */
    private BigDecimal readOpcValue(int namespaceIndex, String identifier, String desc) {
        try {
            NodeId nodeId = new NodeId(namespaceIndex, identifier);
            DataValue dataValue = client.readValue(0, TimestampsToReturn.Both, nodeId).get();
            Object value = dataValue.getValue().getValue();
            if (value instanceof Double doubleValue && doubleValue != 0.0) {
                return BigDecimal.valueOf(doubleValue);
            }
            return BigDecimal.ZERO;
        } catch (Exception ex) {
            log.warn("读取 {} 失败: {}", desc, ex.getMessage());
            return BigDecimal.ZERO;
        }
    }


    /**
     * 保证 client 已连接
     */
    private synchronized void ensureConnected() throws Exception {
        if (client == null || client.getSession() == null) {
            client = OpcUaClient.create(OPC_ADDRESS);
            client.connect().get();
            log.info("泊位 OPC {} 连接成功", PLC_NO);
        }
    }
}
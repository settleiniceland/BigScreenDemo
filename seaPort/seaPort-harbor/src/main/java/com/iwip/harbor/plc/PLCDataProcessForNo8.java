package com.iwip.harbor.plc;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class PLCDataProcessForNo8 {

    private static final String PLC_NO = "8#";

    private static final String OPC_ADDRESS = "opc.tcp://192.168.93.14:18399";
    // A皮带累计量GW8A_1
    private static final int OPC_SUM_WEIGHT_NAMESPACE_INDEX_A = 1;
    private static final String OPC_SUM_WEIGHT_IDENTIFIER_A = "GW8A_1";
    // A瞬时流量GW8A_2
    private static final int OPC_REAL_TIME_NAMESPACE_INDEX_A = 1;
    private static final String OPC_REAL_TIME_IDENTIFIER_A = "gw2";


    // B皮带累计量GW8A_1
    private static final int OPC_SUM_WEIGHT_NAMESPACE_INDEX_B = 1;
    private static final String OPC_SUM_WEIGHT_IDENTIFIER_B = "GW8B_1";
    // B瞬时流量GW8A_2
    private static final int OPC_REAL_TIME_NAMESPACE_INDEX_B = 1;
    private static final String OPC_REAL_TIME_IDENTIFIER_B = "GW8B_2";

    public static void main(String[] args) throws UaException, ExecutionException, InterruptedException {

        OpcUaClient client = OpcUaClient.create("opc.tcp://192.168.93.14:18399");
        client.connect().get();
        try {
//            ensureConnected(); // 确保连接活跃
            while (true){
                Map<String, BigDecimal> dataMap = new HashMap<>();
                dataMap.put("A_weight", readOpcValue(OPC_SUM_WEIGHT_NAMESPACE_INDEX_A, OPC_SUM_WEIGHT_IDENTIFIER_A, client,"A累计吨量"));
                dataMap.put("A_REALTIME", readOpcValue(OPC_REAL_TIME_NAMESPACE_INDEX_A, OPC_REAL_TIME_IDENTIFIER_A,client, "A实时流量"));
                dataMap.put("B_weight", readOpcValue(OPC_SUM_WEIGHT_NAMESPACE_INDEX_B, OPC_SUM_WEIGHT_IDENTIFIER_B,client, "B累计吨量"));
                dataMap.put("B_REALTIME", readOpcValue(OPC_REAL_TIME_NAMESPACE_INDEX_B, OPC_REAL_TIME_IDENTIFIER_B,client, "B实时流量"));

                System.out.println(dataMap);
                Thread.sleep(1000); // 节流
            }


        }  catch (Exception e) {
            log.error("OPC读取失败: {}", e.getMessage(), e);
        } finally {
            client.disconnect().get(); // 确保断开连接
        }
    }


    /**
     * 通用读取方法，返回 BigDecimal 值，失败返回 BigDecimal.ZERO
     */
    private static BigDecimal readOpcValue(int namespaceIndex, String identifier,OpcUaClient client, String desc) {
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



}

package com.iwip.harbor.plc;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author taoqz
 * @create 2025-04-14
 */
@Service
public class PLCDataProcessContext {

    private static final String NO1 = "1#";

    private static final String NO4 = "4#";

    private static final Map<String, IPLCDataProcess> map = new HashMap<>();

    static {
        map.put(NO1, new PLCDataProcessForNo1());
        map.put(NO4, new PLCDataProcessForNo4());
    }

    public static IPLCDataProcess getByBerthName(String berthName) {
        return map.get(berthName);
    }

}

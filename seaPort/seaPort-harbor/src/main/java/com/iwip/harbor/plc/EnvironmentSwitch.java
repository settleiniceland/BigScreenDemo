package com.iwip.harbor.plc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author taoqz
 * @create 2025-04-22
 */
@Component
public class EnvironmentSwitch {

    @Autowired
    private Environment environment;

    public boolean isProd() {
      return environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("prod");
      // return true;
    }

}

package com.iwip.common.config;

import HslCommunication.Authorization;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class PLCConfig {

    @PostConstruct
    public void init(){
        Class<Authorization> authorizationClass = Authorization.class;
        try {
            Authorization authorization = authorizationClass.newInstance();
            Field nuasgdawydbishcgas = authorizationClass.getDeclaredField("nuasgdawydbishcgas");
            nuasgdawydbishcgas.setAccessible(true);
            nuasgdawydbishcgas.set(authorization,8);
            Field nuasgdaaydbishdgas = authorizationClass.getDeclaredField("nuasgdaaydbishdgas");
            nuasgdaaydbishdgas.setAccessible(true);
            nuasgdaaydbishdgas.set(authorization,10000);
            Field naihsdadaasdasdiwasdaid = authorizationClass.getDeclaredField("naihsdadaasdasdiwasdaid");
            naihsdadaasdasdiwasdaid.setAccessible(true);
            naihsdadaasdasdiwasdaid.set(authorization,12345);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

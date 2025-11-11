package com.iwip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 *
 * @author Fei
 */
@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SeaPortApplication {
    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(SeaPortApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  港口系统启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "        ~~~~        \n" +
                "   ~~     ~~        \n" +
                "  ~       ~~        \n" +
                "      ___     ~~     \n" +
                "   __|___|__   ~~    \n" +
                "   \\_____/   ~~     \n" +
                "  ~~~~~~~~~~~~~~~    \n" +
                "  ~~~~~~~~~~~~~~~    \n" +
                "     港口系统         ");
    }
}
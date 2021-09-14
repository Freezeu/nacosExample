package com.nacosexample.confignacos;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.spring.util.parse.DefaultYamlConfigParse;
import com.nacosexample.confignacos.entity.OrderProperties;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Properties;

@SpringBootApplication
public class ConfigNacosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigNacosApplication.class, args);
    }

    /**
     * 实体类值注入
     */
    @Component
    @Log4j2
    public static class OrderPropertiesCommandLineRunner implements CommandLineRunner {
        @Autowired
        private OrderProperties orderProperties;
        
        @Override
        public void run(String... args){
            log.warn("orderProperties - payTimeoutSeconds ：" + orderProperties.getPayTimeoutSeconds());
            log.warn("orderProperties - CreateFrequencySeconds ：" + orderProperties.getCreateFrequencySeconds());
        }
    }
    
    @Component
    @Log4j2
    public static class ValueCommandLineRunner implements CommandLineRunner {

        //        @NacosValue(value = "${order.pay-timeout-seconds}")
        @Value(value = "${order.pay-timeout-seconds}")
        private Integer payTimeoutSeconds;

        //        @NacosValue(value = "${order.create-frequency-seconds}")
        @Value(value = "${order.create-frequency-seconds}")
        private Integer createFrequencySeconds;

        
        @Override
        public void run(String... args) {
            log.warn("orderProperties - payTimeoutSeconds ：" + payTimeoutSeconds);
            log.warn("orderProperties - CreateFrequencySeconds ：" + createFrequencySeconds);

        }
    }
    /**
     * 动态更新配置
     */
    @Component
    @Log4j2
    public static class ConfigListener  {

        /**
         * 日志配置项的前缀
         */
        private static final String LOGGER_TAG = "logging.level";
        
        @Resource
        private LoggingSystem loggingSystem;
        
        @NacosConfigListener(dataId = "${nacos.config.data-id}", type = ConfigType.YAML, timeout = 5000)
        public void onChange(String newLog) {
            Properties properties = new DefaultYamlConfigParse().parse(newLog);
            for (Object item : properties.keySet()) {
                String key = String.valueOf(item);
                if (key.startsWith(LOGGER_TAG)) {
                    String strLevel = properties.getProperty(key, "info");
                    LogLevel logLevel = LogLevel.valueOf(strLevel);
                    loggingSystem.setLogLevel(key.replace(LOGGER_TAG, ""), logLevel);
                }
            }
        }
    }
    @RestController
    @Slf4j
    static class TestRequest {

        @RequestMapping("/logLoggingLevel")
        public void logLoggingLevel() {
            log.error("error");
            log.debug("debug");
            log.info("info");
            log.warn("warn");
        }
    }

}

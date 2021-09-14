package com.nacosexample.confignacos.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "order")
@Data
// @NacosConfigurationProperties(prefix = "order", dataId = "${nacos.config.data-id}", type = ConfigType.YAML)
public class OrderProperties {
    /**
     * 订单支付超时时长，单位：秒。
     */
    private Integer payTimeoutSeconds;

    /**
     * 订单创建频率，单位：秒
     */
    private Integer createFrequencySeconds;
}

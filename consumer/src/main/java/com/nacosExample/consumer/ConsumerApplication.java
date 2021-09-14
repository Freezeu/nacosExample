package com.nacosExample.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @RestController
    static class Consumer {

        @Autowired
        private DiscoveryClient discoveryClient;
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private LoadBalancerClient loadBalancerClient;

        @RequestMapping("/consumer")
        public String consumerApi(String name, Boolean balanceFlag) {
            ServiceInstance instance;
            if (balanceFlag) {
                List<ServiceInstance> instances = discoveryClient.getInstances("demo-provider");
                instance = instances.size() > 0 ? instances.get(0) : null;
            }else {
                instance = loadBalancerClient.choose("demo-provider");
            }

            if (instance == null) {
                throw new IllegalStateException("获取不到实例");
            }
            String targetUrl = instance.getUri() + "provider?name=" + name;
            String response = restTemplate.getForObject(targetUrl, String.class);
            return "consumer : " + name + "，" + response ;
        }
    }
    
    @Configuration
    static class RestTemplateConfiguration {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

}

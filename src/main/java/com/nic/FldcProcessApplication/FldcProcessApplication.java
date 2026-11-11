package com.nic.FldcProcessApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.nic",
    "config",
    "exception",
    "log",
    "middleware",
    "processWorkflow",
    "redis",
    "repository",
    "reqDTO",
    "resDTO",
    "responseHandler",
    "security",
    "service",
    "util"
})
@EntityScan(basePackages = {
    "com.nic.fldc.workflow.entity",
    "com.nic.fldc.lot.entity",
    "repository.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.nic.fldc.workflow.repository",
    "com.nic.fldc.lot.repository",
    "repository"
})
public class FldcProcessApplication {

    public static void main(String[] args) {
        SpringApplication.run(FldcProcessApplication.class, args);
    }
}

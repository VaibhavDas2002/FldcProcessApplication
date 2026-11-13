package com.nic.fldc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
    "com.nic.fldc.workflow.entity",
    "com.nic.fldc.lot.entity",
    "com.nic.fldc.legacy.repository.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.nic.fldc.workflow.repository",
    "com.nic.fldc.lot.repository",
    "com.nic.fldc.legacy.repository"
})
public class FldcProcessApplication {

    public static void main(String[] args) {
        SpringApplication.run(FldcProcessApplication.class, args);
    }
}

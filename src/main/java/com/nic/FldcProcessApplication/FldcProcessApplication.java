package com.nic.FldcProcessApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.nic.FldcProcessApplication",
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
public class FldcProcessApplication {

	public static void main(String[] args) {
		SpringApplication.run(FldcProcessApplication.class, args);
	}

}

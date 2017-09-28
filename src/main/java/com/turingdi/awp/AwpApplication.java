package com.turingdi.awp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"io.gitlab.leibnizhu", "com.turingdi.awp"})
@EnableScheduling
public class AwpApplication extends SpringBootServletInitializer {
    public AwpApplication() {
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AwpApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(AwpApplication.class, args);
    }
}

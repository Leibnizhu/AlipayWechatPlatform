package com.turingdi.awp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AwpApplication extends SpringBootServletInitializer {
    public AwpApplication() {
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(new Class[]{AwpApplication.class});
    }

    public static void main(String[] args) {
        SpringApplication.run(AwpApplication.class, args);
    }
}
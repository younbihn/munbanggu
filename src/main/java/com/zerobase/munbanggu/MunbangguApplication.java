package com.zerobase.munbanggu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MunbangguApplication {

    public static void main(String[] args) {
        SpringApplication.run(MunbangguApplication.class, args);
    }

}

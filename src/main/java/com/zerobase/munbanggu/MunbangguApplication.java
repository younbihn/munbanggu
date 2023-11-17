package com.zerobase.munbanggu;

import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MunbangguApplication {

    public static void main(String[] args) {
        SpringApplication.run(MunbangguApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                executeSqlScript(connection, new ClassPathResource("sql/initial-data.sql"));
            }
        };

    }

}

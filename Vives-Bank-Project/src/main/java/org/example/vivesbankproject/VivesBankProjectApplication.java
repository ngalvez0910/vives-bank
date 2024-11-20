package org.example.vivesbankproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VivesBankProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(VivesBankProjectApplication.class, args);
    }

}

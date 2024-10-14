package org.trailerexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableAsync;

@Modulith
@EnableAsync
@SpringBootApplication(scanBasePackages = {"org.trailerexchange"})
public class TrailerExchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run( TrailerExchangeApplication.class, args);
    }

}

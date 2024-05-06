package de.metallistdev.contractcollection.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.metallistdev.contractcollection")
public class ContractCollectionBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractCollectionBackendApplication.class, args);
    }
}

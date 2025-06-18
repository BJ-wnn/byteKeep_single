package org.wnn.bytekeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class ByteKeepSingleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ByteKeepSingleApplication.class, args);
    }

}

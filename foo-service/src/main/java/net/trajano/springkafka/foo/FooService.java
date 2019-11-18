package net.trajano.springkafka.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FooService {

	public static void main(String[] args) {
		SpringApplication.run(FooService.class, args);
	}

}
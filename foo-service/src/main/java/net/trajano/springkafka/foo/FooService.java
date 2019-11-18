package net.trajano.springkafka.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;

@SpringBootApplication
@EnableBinding(KafkaStreamsProcessor.class)
public class FooService {

	public static void main(String[] args) {
		SpringApplication.run(FooService.class, args);
	}

}
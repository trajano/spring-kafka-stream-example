package net.trajano.springkafka.bar;

import lombok.extern.slf4j.Slf4j;
import net.trajano.springkafka.model.ReverseRequest;
import net.trajano.springkafka.model.ReverseResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableKafkaStreams
@Slf4j
public class BarService {

    @KafkaListener(topics = "reverse")
    @SendTo
    public ReverseResponse reverse(ReverseRequest request) {
        final String text = new StringBuilder(request.getText()).reverse().toString();
        return new ReverseResponse(text);
    }


    public static void main(String[] args) {
        SpringApplication.run(BarService.class, args);
    }

}

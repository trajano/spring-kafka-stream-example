package net.trajano.springkafka.bar;

import lombok.extern.slf4j.Slf4j;
import net.trajano.springkafka.model.ReverseRequest;
import net.trajano.springkafka.model.ReverseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableKafkaStreams
@Slf4j
public class BarService {

    @Value("${sleep:0}")
    private long sleepInMillis;

    @Value("${spring.application.name}")
    private String source;

    @KafkaListener(
            topics = "reverse",
            groupId = "reverse-consumer"
    )
    @SendTo
    public ReverseResponse reverse(ReverseRequest request) throws InterruptedException {
        final String text = new StringBuilder(request.getText()).reverse().toString();
        if (sleepInMillis > 0) {
            log.info("sleeping for {}ms", sleepInMillis);
            Thread.sleep(sleepInMillis);
        }
        return new ReverseResponse(text, source);
    }


    public static void main(String[] args) {
        SpringApplication.run(BarService.class, args);
    }

}

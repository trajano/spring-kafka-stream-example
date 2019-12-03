package net.trajano.springkafka.foo;

import net.trajano.springkafka.model.ReverseRequest;
import net.trajano.springkafka.model.ReverseResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

@SpringBootApplication
@EnableKafkaStreams
public class FooService {

    @Bean
    public ValidatingReplyingKafkaTemplate<String, ReverseRequest, ReverseResponse> replyKafkaTemplate(ProducerFactory<String, ReverseRequest> pf, KafkaMessageListenerContainer<String, ReverseResponse> lc) {
        final ValidatingReplyingKafkaTemplate<String, ReverseRequest, ReverseResponse> template = new ValidatingReplyingKafkaTemplate<String, ReverseRequest, ReverseResponse>(
                pf,
                lc,
                (k,v) -> v
                        .getSource()
                        .contains("slow"));
        return template;
    }

    @Bean
    public KafkaMessageListenerContainer<String, ReverseResponse> replyContainer(ConsumerFactory<String, ReverseResponse> cf) {
        ContainerProperties containerProperties = new ContainerProperties("myreplies");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);

    }

    public static void main(String[] args) {
        SpringApplication.run(FooService.class, args);
    }

}

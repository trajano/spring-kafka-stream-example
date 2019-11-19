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
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableKafkaStreams
public class FooService {

    @Bean
    public ReplyingKafkaTemplate<String, ReverseRequest, ReverseResponse> replyKafkaTemplate(ProducerFactory<String, ReverseRequest> pf, KafkaMessageListenerContainer<String, ReverseResponse> lc) {
        return new ReplyingKafkaTemplate<>(pf, lc);
    }

//    @Bean
//    public ProducerFactory<String, ReverseRequest> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(Map.of(), new StringSerializer(), new JsonSerializer<>());
//    }

    @Bean
    public KafkaMessageListenerContainer<String, ReverseResponse> replyContainer(ConsumerFactory<String, ReverseResponse> cf) {
        ContainerProperties containerProperties = new ContainerProperties("myreplies");
//        containerProperties.setGroupId("mygroup");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);

    }

    public static void main(String[] args) {
        SpringApplication.run(FooService.class, args);
    }

}

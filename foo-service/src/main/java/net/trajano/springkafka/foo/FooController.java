package net.trajano.springkafka.foo;

import net.trajano.springkafka.model.ReverseRequest;
import net.trajano.springkafka.model.ReverseResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FooController {

    @Autowired
    private ValidatingReplyingKafkaTemplate<?, ReverseRequest, ReverseResponse> replyingKafkaTemplate;

    @GetMapping("/hello")
    public Mono<ReverseResponse> hello() {
        System.out.println(replyingKafkaTemplate);
        final RequestReplyFuture<?, ReverseRequest, ReverseResponse> future = replyingKafkaTemplate.sendAndReceive(new ProducerRecord<>("reverse", new ReverseRequest("Archie")));
        return Mono.fromFuture(future.completable())
                .map(ConsumerRecord::value);
    }
}

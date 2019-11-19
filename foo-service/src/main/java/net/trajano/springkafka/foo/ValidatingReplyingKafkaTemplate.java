package net.trajano.springkafka.foo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.GenericMessageListenerContainer;
import org.springframework.kafka.requestreply.CorrelationKey;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidatingReplyingKafkaTemplate<K, V, R> extends ReplyingKafkaTemplate<K, V, R> {

    private final Function<ConsumerRecord<K, R>, Boolean> validationMethod;

    public ValidatingReplyingKafkaTemplate(ProducerFactory<K, V> producerFactory,
                                           GenericMessageListenerContainer<K, R> replyContainer,
                                           Function<ConsumerRecord<K, R>, Boolean> validationMethod) {
        super(producerFactory, replyContainer);
        this.validationMethod = validationMethod;
    }

    public ValidatingReplyingKafkaTemplate(ProducerFactory<K, V> producerFactory, GenericMessageListenerContainer<K, R> replyContainer, boolean autoFlush,
                                           Function<ConsumerRecord<K, R>, Boolean> validationMethod) {
        super(producerFactory, replyContainer, autoFlush);
        this.validationMethod = validationMethod;
    }

    @Override
    public void onMessage(List<ConsumerRecord<K, R>> data) {
        super.onMessage(data.stream()
                .filter(this::isCorrelationIdMatch)
                .peek(krConsumerRecord -> System.out.println("hello" + krConsumerRecord.value()))
                .filter(record -> validationMethod.apply(record))
                .peek(krConsumerRecord -> System.out.println("filtered" + krConsumerRecord.value()))
                .collect(Collectors.toList()));
    }

    private boolean isCorrelationIdMatch(ConsumerRecord<K, R> record) {
        Iterator<Header> iterator = record.headers().iterator();
        CorrelationKey correlationId = null;
        while (correlationId == null && iterator.hasNext()) {
            Header next = iterator.next();
            if (next.key().equals(KafkaHeaders.CORRELATION_ID)) {
                correlationId = new CorrelationKey(next.value());
            }
        }
        if (correlationId == null) {
            this.logger.error("No correlationId found in reply: " + record
                    + " - to use request/reply semantics, the responding server must return the correlation id "
                    + " in the '" + KafkaHeaders.CORRELATION_ID + "' header");
        }
        return correlationId != null;
    }
}

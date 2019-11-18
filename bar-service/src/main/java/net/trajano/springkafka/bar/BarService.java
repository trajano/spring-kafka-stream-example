package net.trajano.springkafka.bar;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.annotations.KafkaStreamsProcessor;
import org.springframework.messaging.handler.annotation.SendTo;

import java.time.Instant;
import java.util.Arrays;

@SpringBootApplication
@EnableBinding(KafkaStreamsProcessor.class)
public class BarService {

    @StreamListener("input")
    @SendTo("output")
    public KStream<?, WordCount> process(KStream<?, String> input) {
        return input
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("WordCounts-multi"))
                .toStream()
                .map((key, value) -> new KeyValue<>(null, new WordCount(
                		key.key(),
						value,
						Instant.ofEpochMilli(key.window().start()),
						Instant.ofEpochMilli(key.window().end()))));
    }

    public static void main(String[] args) {
        SpringApplication.run(BarService.class, args);
    }

}
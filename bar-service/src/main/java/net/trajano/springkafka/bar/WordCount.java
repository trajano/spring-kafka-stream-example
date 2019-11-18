package net.trajano.springkafka.bar;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class WordCount {
    private final String key;
    private final Long value;
    private final Instant start;
    private final Instant end;
}

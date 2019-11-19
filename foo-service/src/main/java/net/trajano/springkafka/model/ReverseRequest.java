package net.trajano.springkafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReverseRequest {
    private final String text;
}

package net.trajano.springkafka.foo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FooController {
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello" + this.getClass());
    }
}

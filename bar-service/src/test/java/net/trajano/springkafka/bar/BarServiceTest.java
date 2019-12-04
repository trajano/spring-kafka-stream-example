package net.trajano.springkafka.bar;

import net.trajano.springkafka.model.ReverseRequest;
import net.trajano.springkafka.model.ReverseResponse;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        controlledShutdown = true,
        topics = {
                "reverse",
                "myreplies"
        })
public class BarServiceTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private ProducerFactory<Integer, ReverseRequest> pf;

    @Autowired
    private ConsumerFactory<Integer, ReverseResponse> cf;

    private ReplyingKafkaTemplate<Integer, ReverseRequest, ReverseResponse> replyKafkaTemplate;

    @Before
    public void setup() {
        ContainerProperties containerProperties = new ContainerProperties("myreplies");
        KafkaMessageListenerContainer<Integer, ReverseResponse> container = new KafkaMessageListenerContainer<>(cf,
                containerProperties);
        replyKafkaTemplate = new ReplyingKafkaTemplate<>(
                pf,
                container);
        replyKafkaTemplate.start();

    }

    @After
    public void tearDown() {
        replyKafkaTemplate.stop();
        replyKafkaTemplate.destroy();
    }

    @Test
    public void test() throws Exception {
        assertEquals(
                "eihcrA",
                replyKafkaTemplate
                        .sendAndReceive(new ProducerRecord<>("reverse", new ReverseRequest("Archie")))
                        .get(5L, TimeUnit.SECONDS)
                        .value()
                        .getText()
        );
    }

    @Test
    public void testNoTimeout() throws Exception {
        assertEquals(
                "sedemihcrA",
                replyKafkaTemplate
                        .sendAndReceive(new ProducerRecord<>("reverse", new ReverseRequest("Archimedes")))
                        .get()
                        .value()
                        .getText()
        );
    }
}

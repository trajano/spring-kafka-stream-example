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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
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
    private ProducerFactory<String, ReverseRequest> pf;

    @Before
    public void setup() {
        ContainerProperties containerProperties = new ContainerProperties("myreplies");
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("mygroup", "false",
                embeddedKafka);
        consumerProps.put("value.deserializer", org.springframework.kafka.support.serializer.JsonDeserializer.class);
        consumerProps.put("spring.json.trusted.packages", "net.trajano.springkafka.model");
        DefaultKafkaConsumerFactory<String, ReverseResponse> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        KafkaMessageListenerContainer<String, ReverseResponse> container = new KafkaMessageListenerContainer<>(cf,
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

    private ReplyingKafkaTemplate<String, ReverseRequest, ReverseResponse> replyKafkaTemplate;

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
                "eihcrA",
                replyKafkaTemplate
                        .sendAndReceive(new ProducerRecord<>("reverse", new ReverseRequest("Archie")))
                        .get()
                        .value()
                        .getText()
        );
    }
}

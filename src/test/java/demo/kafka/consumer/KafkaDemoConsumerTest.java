package demo.kafka.consumer;

import demo.kafka.event.DemoEvent;
import demo.kafka.mapper.JsonMapper;
import demo.kafka.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class KafkaDemoConsumerTest {

    private KafkaDemoConsumer consumer;

    @BeforeEach
    public void setUp() {
        consumer = new KafkaDemoConsumer();
    }
    @Test
    public void testListen() {
        String key = "test-key";
        DemoEvent testEvent = TestData.buildDemoEvent(randomUUID().toString());
        String payload = JsonMapper.writeToJson(testEvent);

        consumer.listen(key, payload);

        assertThat(consumer.counter.get(), equalTo(1));
    }
}

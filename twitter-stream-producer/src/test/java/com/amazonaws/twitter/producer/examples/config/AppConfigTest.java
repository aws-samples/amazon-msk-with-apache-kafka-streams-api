package com.amazonaws.twitter.producer.examples.config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

  private AppConfig subject;

  @BeforeEach
  void setUp() {
    subject = new AppConfig();
  }

  @Test
  void returnsConfiguredKafkaProperties() {

    Properties props = subject.kafkaProps();

    assertEquals("localhost:9092", props.getProperty(BOOTSTRAP_SERVERS_CONFIG));
    assertEquals(StringSerializer.class.getName(), props.getProperty(KEY_SERIALIZER_CLASS_CONFIG));
    assertEquals(
        StringSerializer.class.getName(), props.getProperty(VALUE_SERIALIZER_CLASS_CONFIG));
  }

  @Test
  void returnsNonNullHttpClient() {
    assertNotNull(subject.httpClient());
  }

  @Test
  void returnsNonNullKafkaProducer() {
    assertNotNull(subject.kafkaProducer());
  }
}

package com.amazonaws.kafka.examples.configs;

import org.apache.kafka.common.serialization.Serdes;

import java.util.Properties;

import static java.lang.System.getProperty;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.*;

public class AppConfigs {

  private AppConfigs() {}

  /**
   * Read from CLI a list of host/port pairs to use for establishing the initial connection to the
   * Kafka cluster.
   */
  public static final String KAFKA_BRK = getProperty("kafka.broker", "localhost:9092");

  public static final String INPUT_TOPIC = "twitter_input";
  public static final String OUTPUT_TOPIC = "twitter_output";

  public static Properties streamProps() {

    /* Configuring basic kafka properties */
    Properties props = new Properties();
    props.put(APPLICATION_ID_CONFIG, "kafka-streams-msk-2");
    props.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_BRK);
    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE);

    return props;
  }
}

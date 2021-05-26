// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

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
  public static final String INPUT_TOPIC = getProperty("topic.input", "twitter_input");
  public static final String OUTPUT_TOPIC = getProperty("topic.output", "twitter_output");

  public static Properties streamProps() {

    /* Configuring basic kafka properties */
    var props = new Properties();
    props.put(APPLICATION_ID_CONFIG, "msk-streams-processing");
    props.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_BRK);
    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE);
    props.put(REPLICATION_FACTOR_CONFIG, 3);

    return props;
  }
}

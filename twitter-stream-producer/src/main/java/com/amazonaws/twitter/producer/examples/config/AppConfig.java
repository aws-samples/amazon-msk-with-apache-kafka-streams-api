// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples.config;


import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static java.lang.System.getProperty;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class AppConfig {

  /**
   * Read from CLI a list of host/port pairs to use for establishing the initial connection to the
   * Kafka cluster.
   */
  public static final String KAFKA_BRK = getProperty("kafka.broker", "localhost:9092");
  public static final String TOPIC = getProperty("topic.input", "twitter_input");

  public Properties kafkaProps() {
    var props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_BRK);
    props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
    props.put(SaslConfigs.SASL_MECHANISM, "AWS_MSK_IAM");
    props.put(SaslConfigs.SASL_JAAS_CONFIG, "software.amazon.msk.auth.iam.IAMLoginModule required;");
    props.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS, "software.amazon.msk.auth.iam.IAMClientCallbackHandler");

    return props;
  }

  public KafkaProducer<String, String> kafkaProducer() {
    return new KafkaProducer<>(kafkaProps());
  }
}

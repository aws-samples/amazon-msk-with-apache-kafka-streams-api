// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples.config;


import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.clients.producer.KafkaProducer;
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
  public static final String TWITTER_API_URL =
      getProperty("twitter.api", "https://api.twitter.com/2/tweets/sample/stream");
  public static final String BEARER_TOKEN = getProperty("bearer.token");
  public static final String TOPIC = getProperty("topic.input", "twitter_input");

  public Properties kafkaProps() {
    var props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_BRK);
    props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    return props;
  }

  public KafkaProducer<String, String> kafkaProducer() {
    return new KafkaProducer<>(kafkaProps());
  }

  public HttpClient httpClient() {
    return HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
        .build();
  }
}

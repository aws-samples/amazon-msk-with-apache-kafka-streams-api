// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.kafka.examples;

import com.amazonaws.kafka.examples.service.StreamBuilderService;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.amazonaws.kafka.examples.configs.AppConfigs.streamProps;

public class App {
  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {

    /* Create stream */
    final var builder = new StreamsBuilder();
    var tweetStream = StreamBuilderService.createStream(builder);

    /* Print out each record of the stream */
    tweetStream.foreach((k, v) -> LOG.info("{} : {}", k, v));

    var streams = new KafkaStreams(builder.build(), streamProps());
    /* Add shutdown hook to stop the App. */
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    /* Start the Kafka Streams threads */
    streams.start();
  }
}

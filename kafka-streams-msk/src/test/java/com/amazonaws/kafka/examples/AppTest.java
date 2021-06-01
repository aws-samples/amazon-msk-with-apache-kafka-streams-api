// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.kafka.examples;

import com.amazonaws.kafka.examples.service.StreamBuilderService;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.amazonaws.kafka.examples.configs.AppConfigs.*;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class AppTest {

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, String> inputTopic;
  private TestOutputTopic<String, Long> outputTopic;

  @Before
  public void setUp() {
    final StreamsBuilder builder = new StreamsBuilder();
    StreamBuilderService.createStream(builder);

    testDriver = new TopologyTestDriver(builder.build(), streamProps());
    inputTopic =
        testDriver.createInputTopic(INPUT_TOPIC, new StringSerializer(), new StringSerializer());
    outputTopic =
        testDriver.createOutputTopic(
            OUTPUT_TOPIC, new StringDeserializer(), new LongDeserializer());
  }

  @After
  public void tearDown() {
    try {
      testDriver.close();
    } catch (final RuntimeException e) {
      // https://issues.apache.org/jira/browse/KAFKA-6647 causes exception when executed in Windows,
      // ignoring it
      // Logged stacktrace cannot be avoided
      System.out.println(
          "Ignoring exception, test failing in Windows due this exception:"
              + e.getLocalizedMessage());
    }
  }

  @Test
  public void countHashTagsWithMinOccurrence() {
    inputTopic.pipeValueList(
        Arrays.asList(
            "Hello", "hello", "hello", "hello", "hello", "hello", "hola", "hola", "hola", "hola",
            "hola", "howdy", "135", "", "", "", "", "", ""),
        Instant.ofEpochMilli(0L),
        Duration.ofMillis(500));

    inputTopic.pipeInput(UUID.randomUUID().toString(), "testy", 5555555555L);

    Map<String, Long> expected = new HashMap<>();
    expected.put("hello", 6L);
    expected.put("hola", 5L);

    assertThat(outputTopic.readKeyValuesToMap()).isEqualTo(expected);
    assertTrue(outputTopic.isEmpty());
  }

  @Test
  public void countHashTagsWithMinOccurrenceAndLengthSize() {

    inputTopic.pipeValueList(
        Arrays.asList(
            "135", "Hello", "hello", "hello", "hello", "hello", "hola", "hola,", "howdy", "howdy",
            "howdy", "howdy", "howdy", "howdy", "_", "_", "_", "_", "_", "++", "++", "++", "++",
            "++", " ", " ", " ", " ", " ", " "),
        Instant.ofEpochMilli(0L),
        Duration.ofMillis(500));

    inputTopic.pipeInput(UUID.randomUUID().toString(), "testy", 5555555555L);

    Map<String, Long> expected = new HashMap<>();
    expected.put("hello", 5L);
    expected.put("howdy", 6L);
    expected.put("++", 5L);

    assertThat(outputTopic.readKeyValuesToMap()).isEqualTo(expected);
    assertTrue(outputTopic.isEmpty());

  }

  @Test
  public void shouldReturnStreamsConfig() {
    assertThat(streamProps().getProperty(BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("localhost:9092");
  }
}

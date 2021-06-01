// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.kafka.examples.service;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import static com.amazonaws.kafka.examples.configs.AppConfigs.INPUT_TOPIC;
import static com.amazonaws.kafka.examples.configs.AppConfigs.OUTPUT_TOPIC;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.apache.kafka.common.serialization.Serdes.Long;
import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.streams.kstream.Materialized.with;
import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;
import static org.apache.kafka.streams.kstream.Suppressed.untilWindowCloses;
import static org.apache.kafka.streams.kstream.TimeWindows.of;

public class StreamBuilderService {

  private static final TimeWindows WINDOW_20_SEC = of(ofSeconds(20)).grace(ofMillis(0));
  private static final int MIN_MENTIONED_IN_WINDOW = 4;
  private static final int MIN_CHAR_LENGTH = 1;

  private StreamBuilderService() {}

  public static KStream<String, Long> createStream(StreamsBuilder builder) {

    KStream<String, String> paragraphStream = builder.stream(INPUT_TOPIC);

    /* Stream manipulations */
    var tweetStream =
        paragraphStream
            .filter(
                (k, v) -> v.length() > MIN_CHAR_LENGTH) // filter hashtags with length less 1 char
            .mapValues((ValueMapper<String, String>) String::toLowerCase) // lowercase hashtags
            .mapValues(String::trim) // remove leading and trailing spaces
            .selectKey((k, v) -> v) // select hashtag as a key
            .groupByKey()
            .windowedBy(WINDOW_20_SEC) // apply 20 seconds window aggregation
            .count(with(String(), Long())) // count hashtags, materialized in state store as String & Long
            .suppress(untilWindowCloses(unbounded())) // suppression will emit only the "final results", buffer unconstrained by size(not recommended for prod)
            .toStream()
            .map((k, v) -> new KeyValue<>(k.key(), v))
            .filter(
                (k, v) -> v > MIN_MENTIONED_IN_WINDOW); // filter hashtags mentioned less than 4 times

    tweetStream.to(OUTPUT_TOPIC, Produced.with(String(), Long()));

    return tweetStream;
  }
}

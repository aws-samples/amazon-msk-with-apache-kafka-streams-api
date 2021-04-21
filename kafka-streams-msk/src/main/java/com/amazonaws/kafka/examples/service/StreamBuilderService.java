package com.amazonaws.kafka.examples.service;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import static com.amazonaws.kafka.examples.configs.AppConfigs.INPUT_TOPIC;
import static com.amazonaws.kafka.examples.configs.AppConfigs.OUTPUT_TOPIC;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded;

public class StreamBuilderService {

  private StreamBuilderService() {}

  public static KStream<String, Long> createStream(StreamsBuilder builder) {

    KStream<String, String> paragraphStream = builder.stream(INPUT_TOPIC);

    /* Stream manipulations */
    KStream<String, Long> tweetStream =
        paragraphStream
            .mapValues((ValueMapper<String, String>) String::toLowerCase)
            .mapValues(String::trim)
            .filter((k, v) -> v.length() > 1)
            .selectKey((k, v) -> v)
            .groupByKey()
            .windowedBy(TimeWindows.of(ofSeconds(20)).grace(ofMillis(0)))
            .count(Materialized.with(Serdes.String(), Serdes.Long()))
            .suppress(Suppressed.untilWindowCloses(unbounded()))
            .toStream()
            .map((k, v) -> new KeyValue<>(k.key(), v))
            .filter((k, v) -> v > 4);

    tweetStream.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

    return tweetStream;
  }
}

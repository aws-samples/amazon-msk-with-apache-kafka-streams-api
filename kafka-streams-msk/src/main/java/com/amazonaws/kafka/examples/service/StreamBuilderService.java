package com.amazonaws.kafka.examples.service;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import static com.amazonaws.kafka.examples.configs.AppConfigs.INPUT_TOPIC;
import static com.amazonaws.kafka.examples.configs.AppConfigs.OUTPUT_TOPIC;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.apache.kafka.common.serialization.Serdes.Long;
import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.streams.kstream.Produced.with;
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
            .mapValues((ValueMapper<String, String>) String::toLowerCase)
            .mapValues(String::trim)
            .filter((k, v) -> v.length() > MIN_CHAR_LENGTH)
            .selectKey((k, v) -> v)
            .groupByKey()
            .windowedBy(WINDOW_20_SEC)
            .count(Materialized.with(String(), Long()))
            .suppress(untilWindowCloses(unbounded()))
            .toStream()
            .map((k, v) -> new KeyValue<>(k.key(), v))
            .filter((k, v) -> v > MIN_MENTIONED_IN_WINDOW);

    tweetStream.to(OUTPUT_TOPIC, with(String(), Long()));

    return tweetStream;
  }
}

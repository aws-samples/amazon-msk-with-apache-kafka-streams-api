package com.amazonaws.kafka.examples;

import com.amazonaws.kafka.examples.service.StreamBuilderService;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.amazonaws.kafka.examples.configs.AppConfigs.streamProps;

public class App {
  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {

    /* Create stream */
    final StreamsBuilder builder = new StreamsBuilder();
    KStream<String, Long> tweetStream = StreamBuilderService.createStream(builder);

    /* Print out each record of the stream */
    tweetStream.foreach((k, v) -> LOG.info("{} : {}", k, v));

    KafkaStreams streams = new KafkaStreams(builder.build(), streamProps());
    /* Add shutdown hook to stop the Word Count App. */
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    /* Start the Kafka Streams threads */
    streams.start();
  }
}

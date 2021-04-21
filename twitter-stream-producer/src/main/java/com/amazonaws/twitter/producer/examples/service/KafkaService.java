package com.amazonaws.twitter.producer.examples.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Objects;
import java.util.UUID;

import static com.amazonaws.twitter.producer.examples.config.AppConfig.TOPIC;

public class KafkaService {

  private final KafkaProducer<String, String> kafkaProducer;

  public KafkaService(KafkaProducer<String, String> kafkaProducer) {
    Objects.requireNonNull(kafkaProducer);
    this.kafkaProducer = kafkaProducer;
  }

  public void send(String message) {
    kafkaProducer.send(new ProducerRecord<>(TOPIC, UUID.randomUUID().toString(), message));
  }
}

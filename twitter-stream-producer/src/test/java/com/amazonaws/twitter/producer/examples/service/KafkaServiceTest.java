// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {
  private KafkaService subject;
  @Mock private KafkaProducer<String, String> kafkaProducer;

  @SuppressWarnings("unchecked")
  @Test
  void invokesKafkaProducerSendMethod() {
    subject = new KafkaService(kafkaProducer);
    subject.send("random message");
    verify(kafkaProducer, times(1)).send(any(ProducerRecord.class));
  }

  @Test
  void throwExceptionWhenKafkaProducerNull() {
    assertThrows(NullPointerException.class, () -> new KafkaService(null));
  }
}

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TwitterServiceTest {
  private TwitterSimulationService subject;
  @Mock private KafkaService kafkaService;

  @Test
  void extractHashTagAndSendToKafka() throws Exception {
    subject = new TwitterSimulationService(kafkaService);


    subject.simulateStream();
    Thread.sleep(3000);

    //verify(kafkaService, atLeast(3*10)).send(null);;
  }
}

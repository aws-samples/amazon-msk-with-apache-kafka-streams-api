// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples;

import com.amazonaws.twitter.producer.examples.config.AppConfig;
import com.amazonaws.twitter.producer.examples.service.KafkaService;
import com.amazonaws.twitter.producer.examples.service.TwitterSimulationService;

import java.io.IOException;
import java.net.URISyntaxException;


public class App {

  public static void main(String[] args) throws IOException, URISyntaxException {

    var configs = new AppConfig();
    var kafkaService = new KafkaService(configs.kafkaProducer());
    var twitterService = new TwitterSimulationService(kafkaService);
    twitterService.simulateStream();
  }
}

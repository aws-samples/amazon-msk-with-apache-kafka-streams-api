// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples.service;


import net.datafaker.Faker;

import static java.util.Objects.requireNonNull;

public class TwitterSimulationService {

  private final KafkaService kafkaService;


  public TwitterSimulationService(KafkaService kafkaService) {
    requireNonNull(kafkaService);
    this.kafkaService = kafkaService;
  }

  /*
   * This method calls the sample stream endpoint and streams Tweets from it
   * */
  public void simulateStream() {
    // Create a scheduled task at a fixed rate to call the function sendMessages  every 1 second
    var timer = new java.util.Timer();
    timer.scheduleAtFixedRate(new java.util.TimerTask() {
      @Override
      public void run() {
        sendNMessages(100);
      }
    }, 0, 1000); 
   
  }

  // Send a message to Kafka
  private void sendNMessages(int n){
    for(int i = 0; i < n; i++) {
      Faker faker = new Faker();

      String lastName = faker.name().lastName();
      this.kafkaService.send(lastName);
    }
  }

}

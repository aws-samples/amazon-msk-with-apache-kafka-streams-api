// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitterServiceTest {
  private TwitterService subject;
  @Mock private KafkaService kafkaService;
  @Mock private HttpClient httpClient;
  @Mock private HttpResponse response;
  @Mock private HttpEntity httpEntity;

  @Test
  void extractHashTagAndSendToKafka() throws Exception {
    subject = new TwitterService(kafkaService, httpClient);

    String tweet =
        "{\"data\":{\"id\":\"1384666003150303237\",\"entities\":{\"mentions\":[{\"start\":72,\"end\":81,\"username\":\"user99\"}],\"hashtags\":[{\"start\":59,\"end\":70,\"tag\":\"my_awesome_hashtag\"}]},\"text\":\"Random tweet. #my_awesome_hashtag\"}}";

    InputStream stream = new ByteArrayInputStream(tweet.getBytes(StandardCharsets.UTF_8));

    when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
    when(response.getEntity()).thenReturn(httpEntity);
    when(httpEntity.getContent()).thenReturn(stream);
    doNothing().when(kafkaService).send(anyString());

    subject.connectStream("auth_token");

    verify(response, times(1)).getEntity();
    verify(kafkaService, times(1)).send("my_awesome_hashtag");
  }
}

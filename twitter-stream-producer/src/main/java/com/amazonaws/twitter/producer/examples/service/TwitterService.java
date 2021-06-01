// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.twitter.producer.examples.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.amazonaws.twitter.producer.examples.config.AppConfig.TWITTER_API_URL;
import static java.util.Objects.requireNonNull;

public class TwitterService {

  private final KafkaService kafkaService;
  private final HttpClient httpClient;

  public TwitterService(KafkaService kafkaService, HttpClient httpClient) {
    requireNonNull(kafkaService);
    requireNonNull(httpClient);
    this.kafkaService = kafkaService;
    this.httpClient = httpClient;
  }

  /*
   * This method calls the sample stream endpoint and streams Tweets from it
   * */
  public void connectStream(String authToken) throws IOException, URISyntaxException {

    var response = this.httpClient.execute(httpGet(authToken, uri()));
    var entity = response.getEntity();

    if (null != entity) {
      var reader = new BufferedReader(new InputStreamReader((entity.getContent())));
      var line = reader.readLine();

      while (null != line) {
        filterAndSendTweets(line);
        line = reader.readLine();
      }
    }
  }

  private HttpGet httpGet(String bearerToken, URIBuilder uriBuilder) throws URISyntaxException {
    var httpGet = new HttpGet(uriBuilder.build());
    httpGet.setHeader("Authorization", String.format("Bearer %s", bearerToken));
    httpGet.setHeader("Content-Type", "application/json");
    return httpGet;
  }

  private URIBuilder uri() throws URISyntaxException {
    var uriBuilder = new URIBuilder(TWITTER_API_URL);
    List<NameValuePair> queryParameters = new ArrayList<>();
    queryParameters.add(new BasicNameValuePair("tweet.fields", "entities"));
    uriBuilder.addParameters(queryParameters);
    return uriBuilder;
  }

  private void filterAndSendTweets(String line) {

    if (!line.isEmpty()) {

      var hashtags =
          Optional.of(new JSONObject(line))
              .map(j -> j.optJSONObject("data"))
              .map(j -> j.optJSONObject("entities"))
              .map(j -> j.optJSONArray("hashtags"))
              .orElse(new JSONArray());

      for (Object o : hashtags) {
        var hashtag = (JSONObject) o;
        var tag = (String) hashtag.get("tag");
        this.kafkaService.send(tag);
      }
    }
  }
}

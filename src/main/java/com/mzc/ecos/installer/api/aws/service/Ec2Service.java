package com.mzc.ecos.installer.api.aws.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class Ec2Service {

  @Value("${process.aws.ec2.describe-instance-types}")
  private String awsEc2DescribeInstanceTypes;


  public ResponseEntity<String> describeInstanceTypes() {
    ProcessBuilder builder = new ProcessBuilder();
    String instances = "{}";

    try {
      builder.command("sh", "-c", awsEc2DescribeInstanceTypes);
      Process process = builder.start();

      instances = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();

      JSONObject jsonObject = new JSONObject(instances);
      JSONArray jsonArray = jsonObject.getJSONArray("InstanceTypes");
      JSONArray sortedJsonArray = new JSONArray();
      List list = new ArrayList();
      int i = 0;
      int size = jsonArray.length();

      for(i = 0; i < size; i++) {
        list.add(jsonArray.getJSONObject(i));
      }

      Collections.sort(list, new Comparator<JSONObject>() {

        @Override
        public int compare(JSONObject a, JSONObject b) {
          String source = new String();
          String target = new String();

          try {
            source = (String)a.get("InstanceType");
            target = (String)b.get("InstanceType");
          } catch(Exception e) {
          }

          return source.compareTo(target);
        }
      });

      for(i = 0; i < size; i++) {
        sortedJsonArray.put(list.get(i));
      }

      instances = "{\"InstanceTypes\": " +sortedJsonArray.toString()+ "}";
    } catch(Exception e) {
      e.printStackTrace();
    }

    return ResponseEntity.ok(instances);
  }
}

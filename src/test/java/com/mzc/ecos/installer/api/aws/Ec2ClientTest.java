package com.mzc.ecos.installer.api.aws;

import java.util.Collections;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceTypesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceTypesResponse;


@Log4j2
public class Ec2ClientTest {

  @Test
  public void testDescribeInstancesByCli() {
    ProcessBuilder builder = new ProcessBuilder();

    try {
      builder.command("bash", "-c", "aws ec2 describe-instance-types | grep InstanceType\\\": | cut -f 2 -d : | sed -e 's/\\\"//g' -e 's/,//g'");
      Process process = builder.start();

      List<String> instances = IOUtils.readLines(process.getInputStream(), "UTF-8");
      Collections.sort(instances);

      for (String instance : instances) {
        System.out.println(instance);
      }

//      String ec2Instances = new BufferedReader(
//              new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
//              .collect(Collectors.joining("\n"));
//
//      System.out.println(ec2Instances);

//      JmesPathExpression ast = new JmesPathProjection(new JmesPathFlatten(new JmesPathField("InstanceTypes")), new JmesPathField("InstanceType"));
//
//      JsonNode queryNode = ObjectMapperSingleton.getObjectMapper().valueToTree(ec2Instances);
//      JsonNode finalResult = ast.accept(new JmesPathEvaluationVisitor(), queryNode);
//
//      System.out.println(finalResult.asText());

      int exitCode = process.waitFor();

    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testDescribeInstancesBySdk() {
    Ec2Client ec2Client = Ec2Client.builder().build();
    DescribeInstanceTypesRequest request = DescribeInstanceTypesRequest.builder()
      .maxResults(100)
      .build();

    DescribeInstanceTypesResponse response = ec2Client.describeInstanceTypes(request);
    log.debug("{}", response.instanceTypes());
  }
}

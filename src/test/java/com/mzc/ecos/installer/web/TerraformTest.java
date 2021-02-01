package com.mzc.ecos.installer.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

@Log4j2
public class TerraformTest {

  @Test
  public void testCreateTerraformDirectory() {
    File file = new File("/tmp/home/mzc/app/terraform/elasticsearch");

    if ( !file.exists() ) {
      if (file.mkdirs()) {
        log.debug("성공");
      } else {
        log.debug("실패");
      }
    }
  }

  @Test
  public void testReadTerraformConfigTemplate() throws Exception {
    File file = new File("src/main/resources/configurations/terraform/setup.tf.template");
    String strings = FileUtils.readFileToString(file, "UTF-8");

    log.debug("{}", strings);
  }

  @Test
  public void testCreateTerraformConfigFile() throws Exception {
    TerraformModel m = new TerraformModel();
    AllInOneModel allinone = new AllInOneModel();
    MasterNodeModel master = new MasterNodeModel();
    DataNodeModel data = new DataNodeModel();
    CoordNodeModel coord = new CoordNodeModel();
    File file = new File("configurations/elasticsearch/terraform/setup.tf.template");
    String strings = FileUtils.readFileToString(file, "UTF-8");

    strings = strings.replace("_VPCSECURITYGROUPIDS_", m.getVpcSecurityGroupIds());
    strings = strings.replace("_SUBNETID_", m.getSubnetId());
    strings = strings.replace("_ZONEID_", m.getZoneId());
    strings = strings.replace("_KEYPEM_", m.getKeyPem());
    strings = strings.replace("_AMIID_", m.getAmiId());

    // allinone
    strings = strings.replace("_ALLINONEINSTANCETYPE_", allinone.getInstanceType());
    strings = strings.replace("_ALLINONEINSTANCESIZE_", String.valueOf(allinone.getInstanceSize()));
    strings = strings.replace("_ALLINONEVOLUMESIZE_", String.valueOf(allinone.getVolumeSize()));

    // master
    strings = strings.replace("_MASTERINSTANCETYPE_", master.getInstanceType());
    strings = strings.replace("_MASTERINSTANCESIZE_", String.valueOf(master.getInstanceSize()));
    strings = strings.replace("_MASTERVOLUMESIZE_", String.valueOf(master.getVolumeSize()));

    // data
    strings = strings.replace("_DATAINSTANCETYPE_", data.getInstanceType());
    strings = strings.replace("_DATAINSTANCESIZE_", String.valueOf(data.getInstanceSize()));
    strings = strings.replace("_DATAVOLUMESIZE_", String.valueOf(data.getVolumeSize()));

    // coordinating
    strings = strings.replace("_COORDINSTANCETYPE_", coord.getInstanceType());
    strings = strings.replace("_COORDINSTANCESIZE_", String.valueOf(coord.getInstanceSize()));
    strings = strings.replace("_COORDVOLUMESIZE_", String.valueOf(coord.getVolumeSize()));

    log.debug("{}", strings);

    BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/home/mzc/app/terraform/elasticsearch/setup.tf"));
    writer.write(strings);

    writer.close();
  }

  @Test
  public void testTerraformRun() {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output = "";

    try {
      builder.command("bash", "-c", "cd /tmp/home/mzc/app/terraform/elasticsearch; terraform init");
      process = builder.start();

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();

      log.debug("{}", output);

      builder.command("bash", "-c", "cd /tmp/home/mzc/app/terraform/elasticsearch; terraform plan");
      process = builder.start();

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();

      log.debug("{}", output);
    } catch(Exception e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testHashSet() {
    HashSet<String> sets = new HashSet<>();

    sets.add("/aaaa");
    sets.add("/bbbb");
    sets.add("/cccc");
    sets.add("/dddd");

    log.debug("{}", sets.toArray());
    log.debug("{}", sets.toString());
  }

  @Getter
  @Setter
  @ToString
  class TerraformModel {
    String vpcSecurityGroupIds = "\"sg-0dbe9bf54e6889e72\"";
    String subnetId = "subnet-07326133b000c46da";
    String zoneId = "ap-northeast-2a";
    String keyPem = "ec2key-gw";
    String amiId = "ami-0d0bbf63";
  }

  @Getter
  @Setter
  @ToString
  class AllInOneModel {
    String nodeName = "allinone";
    int volumeSize = 50;
    String pathData = "/usr/share/elasticsearch/data";
    String pathLogs = "/usr/share/elasticsearch/logs";
    int httpPort = 9200;
    int transportPort = 9300;
    int instanceSize = 1;
    String instanceType = "m5.xlarge";
  }

  @Getter
  @Setter
  @ToString
  class MasterNodeModel {
    String nodeName = "master";
    int volumeSize = 10;
    String pathData = "/usr/share/elasticsearch/data";
    String pathLogs = "/usr/share/elasticsearch/logs";
    int httpPort = 9200;
    int transportPort = 9300;
    int instanceSize = 1;
    String instanceType = "c5.large";
  }

  @Getter
  @Setter
  @ToString
  class DataNodeModel {
    String nodeName = "data";
    int volumeSize = 50;
    String pathData = "/usr/share/elasticsearch/data";
    String pathLogs = "/usr/share/elasticsearch/logs";
    int httpPort = 9200;
    int transportPort = 9300;
    int instanceSize = 1;
    String instanceType = "m5.xlarge";
  }

  @Getter
  @Setter
  @ToString
  class CoordNodeModel {
    String nodeName = "coordinating";
    int volumeSize = 10;
    String pathData = "/usr/share/elasticsearch/data";
    String pathLogs = "/usr/share/elasticsearch/logs";
    int httpPort = 9200;
    int transportPort = 9300;
    int instanceSize = 1;
    String instanceType = "c5.large";
  }
}

package com.mzc.ecos.installer.api.aws.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NodeRequestModel {
  private String vendor = "";
  private String stack = "";
  private String topology = "";
  private String instanceType = "";
  private int coreSize = 0;
  private int memSize = 0;
  private int instanceSize = 0;
  private int volumeSize = 0;
  private List<String> privateIps = new ArrayList<>();
  private int httpPort = 9200;
  private int tcpPort = 9300;
  private String pathData = "";
  private String pathLogs = "";

}

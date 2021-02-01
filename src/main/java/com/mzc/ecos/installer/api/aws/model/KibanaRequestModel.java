package com.mzc.ecos.installer.api.aws.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KibanaRequestModel {
  private String clusterBackendBucket;
  private String clusterBackendKey;
  private String version;
  private String vendor;
  private String ip;
  private int port;
  private String elasticsearchHosts;
  private String instanceType;
  private int instanceSize;
  private int volumeSize;
}

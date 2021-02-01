package com.mzc.ecos.installer.api.aws.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ElasticsearchClusterRequestModel {
  private String clusterName;
  private String esVersion;
  private String backendBucket;
  private String backendKey;
  private String seedHosts;
}

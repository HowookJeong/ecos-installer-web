package com.mzc.ecos.installer.api.aws.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TerraformRequestModel {
  private String vpcSecurityGroupIds;
  private String subnetId;
  private String zoneId;
  private String keyName;
  private String keyPem;
  private String amiId;
  private String tfPath;
  private String env;
  private String backendBucket;
  private String backendKey;
  private boolean backend;
}

package com.mzc.ecos.installer.api.aws.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ManagementRequestModel {
  private String keyPem;
  private String bastionIp;
  private String nodeIps;
  private String elasticsearchHosts;
  private String temporaryAnsiblePath;
}

package com.mzc.ecos.installer.web.config.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
public class ApplicationConfigModel {

  @Value("${config.working-path}")
  private String workingPath;

  @Value("${config.terraform.aws.bastion-ip}")
  private String terraformAwsBastionIp;

  @Value("${config.terraform.aws.security-group}")
  private String terraformAwsSecurityGroup;

  @Value("${config.terraform.aws.az}")
  private String terraformAwsAz;

  @Value("${config.terraform.aws.ami}")
  private String terraformAwsAmi;

  @Value("${config.terraform.aws.subnet}")
  private String terraformAwsSubnet;

  @Value("${config.terraform.aws.key-name}")
  private String terraformAwsKeyName;
  @Value("${config.terraform.aws.pem-file}")
  private String terraformAwsPemFile;

  @Value("${config.terraform.aws.path.elasticsearch}")
  private String terraformAwsPathElasticsearch;

  @Value("${config.terraform.aws.path.kibana}")
  private String terraformAwsPathKibana;

  @Value("${config.terraform.aws.backend-bucket}")
  private String terraformAwsBackendBucket;

  @Value("${config.terraform.aws.backend-key.elasticsearch}")
  private String terraformAwsBackendKeyElasticsearch;

  @Value("${config.terraform.aws.backend-key.kibana}")
  private String terraformAwsBackendKeyKibana;

  @Value("${config.elasticsearch.http-port}")
  private int elasticsearchHttpPort;

  @Value("${config.elasticsearch.tcp-port}")
  private int elasticsearchTcpPort;

  @Value("${config.elasticsearch.path-data}")
  private String elasticsearchPathData;

  @Value("${config.elasticsearch.path-logs}")
  private String elasticsearchPathLogs;
}

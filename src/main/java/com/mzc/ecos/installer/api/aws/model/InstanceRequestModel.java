package com.mzc.ecos.installer.api.aws.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstanceRequestModel {
  private TerraformRequestModel terraformRequestModel;
  private ElasticsearchClusterRequestModel elasticsearchClusterRequestModel;
  private KibanaRequestModel kibanaRequestModel;
  private int nodeSize = 0; // allinone, master, data, coordinator 에 대한 종류의 크기
  private List<NodeRequestModel> instances; // 실제 노드 유형별 생성 되어야 하는 인스턴스의 크기
  private String workingPath;
  private String bastionIp;
}

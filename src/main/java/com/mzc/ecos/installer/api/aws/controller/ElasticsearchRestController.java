package com.mzc.ecos.installer.api.aws.controller;

import com.mzc.ecos.installer.api.aws.model.ManagementRequestModel;
import com.mzc.ecos.installer.api.aws.model.InstanceRequestModel;
import com.mzc.ecos.installer.api.aws.service.ElasticsearchService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aws/elasticsearch")
@Log4j2
@RequiredArgsConstructor
public class ElasticsearchRestController {

  private final ElasticsearchService elasticsearchService;

  @ApiOperation(value = "create elasticsearch cluster using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/create-cluster-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> createClusterByAnsible(
    @RequestBody InstanceRequestModel m
  ) {
    return elasticsearchService.createClusterByAnsible(m);
  }

  @ApiOperation(value = "append elasticsearch cluster using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/append-cluster-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> appendClusterByAnsible(
    @RequestBody InstanceRequestModel m
  ) {
    return elasticsearchService.appendClusterByAnsible(m);
  }

  @ApiOperation(value = "elasticsearch cluster start using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/start-cluster-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> startClusterByAnsible(
    @RequestBody InstanceRequestModel m
  ) {
    return elasticsearchService.startClusterByAnsible(m);
  }

  @ApiOperation(value = "elasticsearch cluster stop using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/stop-cluster-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> stopClusterByAnsible(
    @RequestBody InstanceRequestModel m
  ) {
    return elasticsearchService.stopClusterByAnsible(m);
  }

  @ApiOperation(value = "elasticsearch node start/stop temporary ansible ",
    httpMethod = "POST")
  @PostMapping(value = "/create-temporary-ansible-for-node", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> createTemporaryAnsibleForNodeRestart(@RequestBody ManagementRequestModel m
  ) {
    return elasticsearchService.createTemporaryAnsibleForNodeRestart(m);
  }

  @ApiOperation(value = "elasticsearch node start using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/start-node-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> startNodeByAnsible(
    @RequestBody ManagementRequestModel m
  ) {
    return elasticsearchService.startNodeByAnsible(m);
  }

  @ApiOperation(value = "elasticsearch node stop using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/stop-node-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> stopNodeByAnsible(
    @RequestBody ManagementRequestModel m
  ) {
    return elasticsearchService.stopNodeByAnsible(m);
  }

}

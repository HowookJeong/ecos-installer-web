package com.mzc.ecos.installer.api.aws.controller;

import com.mzc.ecos.installer.api.aws.model.ManagementRequestModel;
import com.mzc.ecos.installer.api.aws.model.InstanceRequestModel;
import com.mzc.ecos.installer.api.aws.service.KibanaService;
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
@RequestMapping("/api/aws/kibana")
@Log4j2
@RequiredArgsConstructor
public class KibanaRestController {

  private final KibanaService kibanaService;

  @ApiOperation(value = "create kibana using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/create-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> createByAnsible(
    @RequestBody InstanceRequestModel m
  ) {
    return kibanaService.createByAnsible(m);
  }

  @ApiOperation(value = "kibana start using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/start-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> startKibanaByAnsible(
    @RequestBody InstanceRequestModel m
  ) {
    return kibanaService.startKibanaByAnsible(m);
  }

  @ApiOperation(value = "kibana stop using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/stop-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> stopKibanaByAnsible(
    @RequestBody InstanceRequestModel m
  ) {
    return kibanaService.stopKibanaByAnsible(m);
  }

  @ApiOperation(value = "kibana node start/stop temporary ansible ",
    httpMethod = "POST")
  @PostMapping(value = "/create-temporary-ansible-for-node", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> createTemporaryAnsibleForNodeRestart(@RequestBody ManagementRequestModel m
  ) {
    return kibanaService.createTemporaryAnsibleForNodeRestart(m);
  }

  @ApiOperation(value = "kibana node start using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/start-node-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> startNodeByAnsible(
    @RequestBody ManagementRequestModel m
  ) {
    return kibanaService.startNodeByAnsible(m);
  }

  @ApiOperation(value = "elasticsearch node stop using ansible.",
    httpMethod = "POST")
  @PostMapping(value = "/stop-node-by-ansible", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> stopNodeByAnsible(
    @RequestBody ManagementRequestModel m
  ) {
    return kibanaService.stopNodeByAnsible(m);
  }
}

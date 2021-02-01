package com.mzc.ecos.installer.api.aws.controller;

import com.mzc.ecos.installer.api.aws.model.InstanceRequestModel;
import com.mzc.ecos.installer.api.aws.model.TerraformRequestModel;
import com.mzc.ecos.installer.api.aws.service.TerraformService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aws/terraform")
@Log4j2
@RequiredArgsConstructor
public class TerraformRestController {

  private final TerraformService terraformService;

  @ApiOperation(value = "create instances using terraform.",
    httpMethod = "POST")
  @PostMapping(value = "/create-instances-for-elasticsearch", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> createInstancesForElasticsearch(
    @RequestBody InstanceRequestModel m
    ) {
    return terraformService.createInstancesForElasticsearch(m);
  }

  @ApiOperation(value = "destroy instances using terraform.",
          httpMethod = "POST")
  @PostMapping(value = "/destroy-instances-for-elasticsearch", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> destroyInstancesForElasticsearch(
          @RequestBody InstanceRequestModel m
  ) {
    return terraformService.destroyInstancesForElasticsearch(m);
  }

  @ApiOperation(value = "get terraform project list.",
          httpMethod = "GET")
  @GetMapping(value = "/list-terraform-project", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<Object[]> listTerraformProject() {
    return terraformService.listTerraformProject();
  }

  @ApiOperation(value = "pull terraform state.",
    httpMethod = "POST")
  @PostMapping(value = "/pull-terraform-state", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> pullTerraformState(
    @RequestBody InstanceRequestModel m
  ) {
    return terraformService.pullTerraformState(m);
  }

  @ApiOperation(value = "fetch terraform state.",
    httpMethod = "POST")
  @PostMapping(value = "/fetch-terraform-state", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> fetchTerraformState(
    @RequestBody TerraformRequestModel m
  ) {
    return terraformService.fetchTerraformState(m);
  }

  @ApiOperation(value = "list terraform state files from s3 backend bucket.",
    httpMethod = "POST")
  @PostMapping(value = "/list-terraform-state-file", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> listTerraformStateFile(
    @RequestBody TerraformRequestModel m
  ) {
    return terraformService.listTerraformStateFile(m);
  }

  @ApiOperation(value = "manual destroy instance by terraform.",
    httpMethod = "POST")
  @PostMapping(value = "/destroy-resource-by-terraform", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> destroyResourceByTerraform(
    @RequestBody TerraformRequestModel m
  ) {
    return terraformService.destroyResourceByTerraform(m);
  }

  @ApiOperation(value = "manual tfstate file deletion.",
    httpMethod = "POST")
  @PostMapping(value = "/delete-tfstate", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> deleteTfState(
    @RequestBody TerraformRequestModel m
  ) {
    return terraformService.deleteTfState(m);
  }

  @ApiOperation(value = "create instance using terraform for kibana.",
    httpMethod = "POST")
  @PostMapping(value = "/create-instances-for-kibana", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> createInstancesForKibana(
    @RequestBody InstanceRequestModel m
  ) {
    return terraformService.createInstancesForKibana(m);
  }

  @ApiOperation(value = "destroy instances using terraform for kibana.",
    httpMethod = "POST")
  @PostMapping(value = "/destroy-instances-for-kibana", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> destroyInstancesForKibana(
    @RequestBody InstanceRequestModel m
  ) {
    return terraformService.destroyInstancesForKibana(m);
  }

}

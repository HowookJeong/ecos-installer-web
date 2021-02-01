package com.mzc.ecos.installer.api.aws.controller;

import com.mzc.ecos.installer.api.aws.service.Ec2Service;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aws/ec2")
@Log4j2
@RequiredArgsConstructor
public class Ec2RestController {

  private final Ec2Service ec2Service;

  @ApiOperation(value = "Returns  a  list  of  all  instance  types  offered in your current AWS Region.",
    httpMethod = "POST")
  @PostMapping(value = "/describe-instance-types", produces = { MediaType.APPLICATION_JSON_VALUE })
  public ResponseEntity<String> describeInstanceTypes() {
    return ec2Service.describeInstanceTypes();
  }
}
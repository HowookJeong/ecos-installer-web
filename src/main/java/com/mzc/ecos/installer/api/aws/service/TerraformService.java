package com.mzc.ecos.installer.api.aws.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mzc.ecos.core.util.FileUtil;
import com.mzc.ecos.installer.api.aws.model.InstanceRequestModel;
import com.mzc.ecos.installer.api.aws.model.NodeRequestModel;
import com.mzc.ecos.installer.api.aws.model.TerraformRequestModel;
import com.mzc.ecos.installer.api.constant.InMemory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@Log4j2
@RequiredArgsConstructor
public class TerraformService {
  private StringBuffer responseBuffer;

  @Value("${process.terraform.path.temporary.base}")
  private String terraformPathTemporary;

  @Value("${process.terraform.path.temporary.backup}")
  private String terraformPathTemporaryBackup;

  @Value("${process.terraform.state.s3.copy}")
  private String terraformStateS3Copy;

  @Value("${process.template.terraform.provider}")
  private String templateTerraformProvider;

  @Value("${process.template.terraform.elasticsearch.setup}")
  private String templateTerraformElasticsearchSetup;

  @Value("${process.template.terraform.kibana.setup}")
  private String templateTerraformKibanaSetup;

  public ResponseEntity<String> createInstancesForKibana(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String terraformTemplate = "";
    boolean isCreateTerraformConfig = false;

    responseBuffer = new StringBuffer();

    // step 0) create terraform backend
    if ( m.getTerraformRequestModel().isBackend() ) {
      createTerraformS3Backend(m);
    }

    // step 1) read terraform template
    terraformTemplate = readTerraformTemplateForKibana(m);

    // step 2) write terraform file
    isCreateTerraformConfig = writeTerraformTemplateForKibana(m, terraformTemplate);

    log.debug("{}", isCreateTerraformConfig);

    if ( isCreateTerraformConfig ) {
      // step 3) run terraform
      runTerraformTemplateForElasticsearchKibana(m);

      // step 4) terraform state store
      if ( !m.getTerraformRequestModel().isBackend() ) {
        backupTerraformTemplateStateForElasticsearchKibana(m);
      }
    }

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(responseBuffer.toString()));
  }

  public ResponseEntity<String> createInstancesForElasticsearch(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String terraformTemplate = "";
    boolean isCreateTerraformConfig = false;

    responseBuffer = new StringBuffer();

    // step 0) create terraform backend
    if ( m.getTerraformRequestModel().isBackend() ) {
      createTerraformS3Backend(m);
    }

    // step 1) read terraform template
    terraformTemplate = readTerraformTemplateForElasticsearch(m);

    // step 2) write terraform file
    isCreateTerraformConfig = writeTerraformTemplateForElasticsearch(m, terraformTemplate);

    log.debug("{}", isCreateTerraformConfig);

    if ( isCreateTerraformConfig ) {
      // step 3) run terraform
      runTerraformTemplateForElasticsearchKibana(m);

      // step 4) terraform state store
      if ( !m.getTerraformRequestModel().isBackend() ) {
        backupTerraformTemplateStateForElasticsearchKibana(m);
      }
    }

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(responseBuffer.toString()));
  }

  public ResponseEntity<String> destroyInstancesForKibana(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    PrintWriter input;
    String output = "";

    try {
      builder.command("sh", "-c", "cd "+m.getTerraformRequestModel().getTfPath()+"; terraform destroy");
      process = builder.redirectErrorStream(true).start();

      input = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);
      input.println("yes");

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();

      InMemory.TERRAFORM_PROJECTS.remove(m.getTerraformRequestModel().getTfPath());

      log.debug("{}", output);
    } catch(Exception e) {
      log.debug("{}", e.getMessage());
    }
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(output));
  }

  public ResponseEntity<String> destroyInstancesForElasticsearch(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    PrintWriter input;
    String output = "";

    try {
      builder.command("sh", "-c", "cd "+m.getTerraformRequestModel().getTfPath()+"; terraform destroy");
      process = builder.redirectErrorStream(true).start();

      input = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);
      input.println("yes");

      output = new BufferedReader(
              new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
              .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();

      InMemory.TERRAFORM_PROJECTS.remove(m.getTerraformRequestModel().getTfPath());

      log.debug("{}", output);
    } catch(Exception e) {
      log.debug("{}", e.getMessage());
    }
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(output));
  }

  public ResponseEntity<Object[]> listTerraformProject() {
    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(InMemory.TERRAFORM_PROJECTS.toArray());
  }

  public ResponseEntity<String> pullTerraformState(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output = "";

    try {
      builder.command("sh", "-c", "cd " + m.getTerraformRequestModel().getTfPath() + "; terraform state pull");
      process = builder.redirectErrorStream(true).start();

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();

      log.debug("{}", output);
    } catch(Exception e) {
      log.debug("{}", e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(output));
  }

  public ResponseEntity<String> fetchTerraformState(TerraformRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    PrintWriter input;
    String command = "";
    String output = "";
    String terraformConfig = "";
    String temporaryTerraformPath = terraformPathTemporary + "/" + String.valueOf(System.currentTimeMillis());

    responseBuffer = new StringBuffer();

    // make temporary terraform working directory
    File dir = new File(temporaryTerraformPath);

    if ( dir.isDirectory() ) {
      dir.delete();
    }

    dir.mkdirs();

    // cp provider.tf temporary
    try {
      terraformConfig = FileUtil.readResourceFile(templateTerraformProvider);
    } catch (Exception e) {
      log.error("{}", e.getMessage());
    }

    FileUtil.writeTemplateFile(
      temporaryTerraformPath+"/provider.tf",
      terraformConfig
    );

    // terraform init
    // aws s3 cp s3://megatoi-terraform-state/elasticsearch/terraform.tfstate terraform.tfstate
    // terraform state pull
    try {
      command = "cd " + temporaryTerraformPath + "; terraform init";
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

      log.debug("{}", command);

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();
      log.debug("terraform init : {}", output);
      responseBuffer.append(output);

      log.debug("{}", m.toString());

      command = terraformStateS3Copy;
      command = command.replace("_TEMPTERRAFORMPATH_", temporaryTerraformPath);
      command = command.replace("_BACKENDBUCKET_", m.getBackendBucket());
      command = command.replace("_BACKENDKEY", m.getBackendKey());
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

      log.debug("{}", command);

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();
      log.debug("{}", output);
      responseBuffer.append(output);

      command = "cd " + temporaryTerraformPath + "; terraform state pull";
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

      log.debug("{}", command);

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();

      log.debug("{}", output);
      responseBuffer.append(output);
    } catch(Exception e) {
      log.debug("{}", e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(output));
  }

  public ResponseEntity<String> listTerraformStateFile(TerraformRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    S3Client s3 = S3Client.builder().build();

    ListObjectsRequest request = ListObjectsRequest
      .builder()
      .bucket(m.getBackendBucket())
      .build();

    ListObjectsResponse response = s3.listObjects(request);
    List<S3Object> objects = response.contents();

    log.debug("{}", objects.toString());

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(objects));
  }

  public ResponseEntity<String> destroyResourceByTerraform(TerraformRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    PrintWriter input;
    String command = "";
    String output = "";
    String terraformConfig = "";
    String temporaryTerraformPath = terraformPathTemporary + "/" + String.valueOf(System.currentTimeMillis());

    log.debug("{}", temporaryTerraformPath);

    responseBuffer = new StringBuffer();

    // make temporary terraform working directory
    File dir = new File(temporaryTerraformPath);

    if ( dir.isDirectory() ) {
      dir.delete();
    }

    dir.mkdirs();

    // cp provider.tf temporary
    try {
      terraformConfig = FileUtil.readResourceFile(templateTerraformProvider);
    } catch (Exception e) {
      log.error("{}", e.getMessage());
    }

    FileUtil.writeTemplateFile(
      temporaryTerraformPath+"/provider.tf",
      terraformConfig
    );

    // terraform init
    // aws s3 cp s3://megatoi-terraform-state/elasticsearch/terraform.tfstate terraform.tfstate
    // terraform destroy
    try {
      command = "cd " + temporaryTerraformPath + "; terraform init";
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

      log.debug("{}", command);

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();
      log.debug("terraform init : {}", output);
      responseBuffer.append(output);

      command = terraformStateS3Copy;
      command = command.replace("_TEMPTERRAFORMPATH_", temporaryTerraformPath);
      command = command.replace("_BACKENDBUCKET_", m.getBackendBucket());
      command = command.replace("_BACKENDKEY", m.getBackendKey());
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

      log.debug("{}", command);

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();
      log.debug("{}", output);
      responseBuffer.append(output);

      command = "cd " + temporaryTerraformPath + "; terraform destroy";
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

      log.debug("{}", command);

      input = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);
      input.println("yes");

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();

      log.debug("{}", output);
      responseBuffer.append(output);
    } catch(Exception e) {
      log.debug("{}", e.getMessage());
    }

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(responseBuffer.toString()));
  }

  public ResponseEntity<String> deleteTfState(TerraformRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    S3Client s3 = S3Client.builder().build();

    DeleteObjectRequest request = DeleteObjectRequest.builder()
      .bucket(m.getBackendBucket())
      .key(m.getBackendKey())
      .build();

    DeleteObjectResponse response = s3.deleteObject(request);

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(response.toString()));
  }

  private boolean createTerraformS3Backend(InstanceRequestModel m) {
    boolean isBucket = false;
    S3Client s3 = S3Client.builder().build();

    CreateBucketRequest request = CreateBucketRequest
      .builder()
      .bucket(m.getTerraformRequestModel().getBackendBucket())
      .build();
    try {
      CreateBucketResponse response = s3.createBucket(request);
      isBucket = true;
    } catch (BucketAlreadyOwnedByYouException baobye) {
      isBucket = true;
    } catch (Exception e) {
      isBucket = false;
    }

    log.debug("S3 Bucket is : {}", isBucket);

    return isBucket;
  }

  private String readTerraformTemplateForElasticsearch(InstanceRequestModel m) {
    String terraformConfig = "";

    try {
      terraformConfig = FileUtil.readResourceFile(templateTerraformElasticsearchSetup);
    } catch (Exception e) {
      log.error("{}", e.getMessage());
    }

    return terraformConfig;
  }

  private boolean writeTerraformTemplateForElasticsearch(InstanceRequestModel m, String template) {
    try {
      List<String> vpcSecurityGroupIds = Arrays.asList(m.getTerraformRequestModel().getVpcSecurityGroupIds().split(","));
      StringBuilder builder = new StringBuilder();

      if ( InMemory.TERRAFORM_PROJECTS.contains(m.getTerraformRequestModel().getTfPath()) ) {
        return false;
      }

      for ( String vpcSecurityGroupId : vpcSecurityGroupIds ) {
        builder.append("\"");
        builder.append(vpcSecurityGroupId);
        builder.append("\",");
      }

      template = template.replace("_VPCSECURITYGROUPIDS_", builder.toString().substring(0, builder.toString().length()-1));
      template = template.replace("_SUBNETID_", m.getTerraformRequestModel().getSubnetId());
      template = template.replace("_ZONEID_", m.getTerraformRequestModel().getZoneId());
      template = template.replace("_KEYNAME_", m.getTerraformRequestModel().getKeyName());
      template = template.replace("_KEYPEM_", m.getTerraformRequestModel().getKeyPem());
      template = template.replace("_AMIID_", m.getTerraformRequestModel().getAmiId());
      template = template.replace("_ENV_", m.getTerraformRequestModel().getEnv());
      template = template.replace("_BUCKET_", m.getTerraformRequestModel().getBackendBucket());
      template = template.replace("_KEY_", m.getTerraformRequestModel().getBackendKey());

      String topology = "";

      for ( NodeRequestModel node : m.getInstances() ) {
        topology = node.getTopology().toUpperCase();
        template = template.replace("_"+topology+"INSTANCETYPE_", node.getInstanceType());
        template = template.replace("_"+topology+"INSTANCESIZE_", String.valueOf(node.getInstanceSize()));
        template = template.replace("_"+topology+"VOLUMESIZE_", String.valueOf(node.getVolumeSize()));
      }

      File dir = new File(m.getTerraformRequestModel().getTfPath());

      if ( dir.isDirectory() ) {
        dir.delete();
      }

      dir.mkdirs();

      FileUtil.writeTemplateFile(
        m.getTerraformRequestModel().getTfPath()+"/setup.tf",
        template
      );

      log.debug("{}", template);

      return true;
    } catch (Exception e) {
      log.error("{}", e.getMessage());
      return false;
    }
  }

  private void runTerraformTemplateForElasticsearchKibana(InstanceRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    PrintWriter input;
    String output = "";
    String terraformInit = "";

    try {
      builder.command("sh", "-c", "cd "+m.getTerraformRequestModel().getTfPath()+"; rm -rf .terraform");
      process = builder.redirectErrorStream(true).start();

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      int exitCode = process.waitFor();
      log.debug("rm -rf .terraform : {}", output);

      builder.command("sh", "-c", "cd "+m.getTerraformRequestModel().getTfPath()+"; terraform init");
      process = builder.redirectErrorStream(true).start();

      output = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
        .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();
      log.debug("terraform init : {}", output);

      builder.command("sh", "-c", "cd "+m.getTerraformRequestModel().getTfPath()+"; terraform apply");
      process = builder.redirectErrorStream(true).start();

      input = new PrintWriter(new OutputStreamWriter(process.getOutputStream()), true);
      input.println("yes");

      output = new BufferedReader(
              new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
              .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();
      log.debug("{}", output);
      responseBuffer.append(output);
    } catch(Exception e) {
      log.debug("{}", e.getMessage());
    }
  }

  private void backupTerraformTemplateStateForElasticsearchKibana(InstanceRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output = "";

    try {
      builder.command("sh", "-c", "mkdir -p " + terraformPathTemporaryBackup);
      process = builder.redirectErrorStream(true).start();

      int exitCode = process.waitFor();

      builder.command("sh", "-c", "cp -rf "+m.getTerraformRequestModel().getTfPath()+" "+terraformPathTemporaryBackup);
      process = builder.redirectErrorStream(true).start();

      output = new BufferedReader(
              new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)).lines()
              .collect(Collectors.joining("\n"));

      exitCode = process.waitFor();

      InMemory.TERRAFORM_PROJECTS.add(m.getTerraformRequestModel().getTfPath());

      log.debug("{}", output);
    } catch(Exception e) {
      log.debug("{}", e.getMessage());
    }
  }

  private String readTerraformTemplateForKibana(InstanceRequestModel m) {
    String terraformConfig = "";

    try {
      terraformConfig = FileUtil.readResourceFile(templateTerraformKibanaSetup);
    } catch (Exception e) {
      log.error("{}", e.getMessage());
    }

    return terraformConfig;
  }

  private boolean writeTerraformTemplateForKibana(InstanceRequestModel m, String template) {
    try {
      List<String> vpcSecurityGroupIds = Arrays.asList(m.getTerraformRequestModel().getVpcSecurityGroupIds().split(","));
      StringBuilder builder = new StringBuilder();

      if ( InMemory.TERRAFORM_PROJECTS.contains(m.getTerraformRequestModel().getTfPath()) ) {
        return false;
      }

      for ( String vpcSecurityGroupId : vpcSecurityGroupIds ) {
        builder.append("\"");
        builder.append(vpcSecurityGroupId);
        builder.append("\",");
      }

      template = template.replace("_VPCSECURITYGROUPIDS_", builder.toString().substring(0, builder.toString().length()-1));
      template = template.replace("_SUBNETID_", m.getTerraformRequestModel().getSubnetId());
      template = template.replace("_ZONEID_", m.getTerraformRequestModel().getZoneId());
      template = template.replace("_KEYNAME_", m.getTerraformRequestModel().getKeyName());
      template = template.replace("_KEYPEM_", m.getTerraformRequestModel().getKeyPem());
      template = template.replace("_AMIID_", m.getTerraformRequestModel().getAmiId());
      template = template.replace("_ENV_", m.getTerraformRequestModel().getEnv());
      template = template.replace("_BUCKET_", m.getTerraformRequestModel().getBackendBucket());
      template = template.replace("_KEY_", m.getTerraformRequestModel().getBackendKey());

      template = template.replace("_KIBANAINSTANCETYPE_", m.getKibanaRequestModel().getInstanceType());
      template = template.replace("_KIBANAINSTANCESIZE_", String.valueOf(m.getKibanaRequestModel().getInstanceSize()));
      template = template.replace("_KIBANAVOLUMESIZE_", String.valueOf(m.getKibanaRequestModel().getVolumeSize()));

      File dir = new File(m.getTerraformRequestModel().getTfPath());

      if ( dir.isDirectory() ) {
        dir.delete();
      }

      dir.mkdirs();

      FileUtil.writeTemplateFile(
        m.getTerraformRequestModel().getTfPath()+"/setup.tf",
        template
      );

      log.debug("{}", template);

      return true;
    } catch (Exception e) {
      log.error("{}", e.getMessage());
      return false;
    }
  }
}

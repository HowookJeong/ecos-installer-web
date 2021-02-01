package com.mzc.ecos.installer.api.aws.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mzc.ecos.core.util.FileUtil;
import com.mzc.ecos.installer.api.aws.model.InstanceRequestModel;
import com.mzc.ecos.installer.api.aws.model.ManagementRequestModel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class KibanaService {
  @Value("${process.template.kibana.docker-compose}")
  private String templateKibanaDockerCompose;

  @Value("${process.template.ansible.inventories.kibana.base}")
  private String templateAnsibleInventoriesKibana;

  @Value("${process.template.ansible.inventories.kibana.nodes}")
  private String templateAnsibleInventoriesKibanaNodes;

  @Value("${process.template.ansible.roles.kibana.base}")
  private String templateAnsibleRolesKibana;

  @Value("${process.template.ansible.roles.kibana.docker-start}")
  private String templateAnsibleRolesKibanaDockerStart;

  @Value("${process.template.ansible.roles.kibana.docker-stop}")
  private String templateAnsibleRolesKibanaDockerStop;

  @Value("${process.template.ansible.playbook.kibana.base}")
  private String templateAnsiblePlaybookKibana;

  @Value("${process.template.ansible.playbook.kibana.docker-start}")
  private String templateAnsiblePlaybookKibanaDockerStart;

  @Value("${process.template.ansible.playbook.kibana.docker-stop}")
  private String templateAnsiblePlaybookKibanaDockerStop;

  @Value("${process.template.terraform.kibana.init}")
  private String templateTerraformInit;

  @Value("${process.template.docker.kibana.docker-compose}")
  private String templateDockerKibanaDockerCompose;

  @Value("${process.docker.path.kibana.base}")
  private String dockerPathKibana;

  @Value("${process.docker.path.kibana.docker-compose}")
  private String dockerPathKibanaDockerCompose;

  @Value("${process.ansible.kbnode.docker-start}")
  private String kbNodeDockerStart;

  @Value("${process.ansible.kbnode.docker-stop}")
  private String kbNodeDockerStop;

  @Value("${process.ansible.path.temporary}")
  private String ansiblePathTemporary;

  @Value("${process.ansible.kibana.docker-start}")
  private String kibanaDockerStart;

  @Value("${process.ansible.kibana.docker-stop}")
  private String kibanaDockerStop;

  @Value("${process.ansible.path.kibana.base}")
  private String ansiblePathKibana;

  @Value("${process.ansible.path.kibana.inventories.base}")
  private String ansiblePathKibanaInventories;

  @Value("${process.ansible.path.kibana.inventories.nodes}")
  private String ansiblePathKibanaInventoriesNodes;

  @Value("${process.ansible.path.kibana.roles.base}")
  private String ansiblePathKibanaRoles;

  @Value("${process.ansible.path.kibana.roles.docker-start-dir}")
  private String ansiblePathKibanaRolesDockerStartDir;

  @Value("${process.ansible.path.kibana.roles.docker-stop-dir}")
  private String ansiblePathKibanaRolesDockerStopDir;

  @Value("${process.ansible.path.kibana.roles.docker-start-file}")
  private String ansiblePathKibanaRolesDockerStartFile;

  @Value("${process.ansible.path.kibana.roles.docker-stop-file}")
  private String ansiblePathKibanaRolesDockerStopFile;

  @Value("${process.ansible.path.kibana.playbook.base}")
  private String ansiblePathKibanaPlaybook;

  @Value("${process.ansible.path.kibana.playbook.docker-start}")
  private String ansiblePathKibanaPlaybookDockerStart;

  @Value("${process.ansible.path.kibana.playbook.docker-stop}")
  private String ansiblePathKibanaPlaybookDockerStop;

  @Value("${process.ansible.path.kibana.playbook.docker-start-nodes}")
  private String ansiblePathKibanaPlaybookDockerStartNodes;

  @Value("${process.ansible.path.kibana.playbook.docker-stop-nodes}")
  private String ansiblePathKibanaPlaybookDockerStopNodes;

  @Value("${process.ansible.path.kibana.roles.docker-start-nodes-dir}")
  private String ansiblePathKibanaRolesDockerStartNodesDir;

  @Value("${process.ansible.path.kibana.roles.docker-stop-nodes-dir}")
  private String ansiblePathKibanaRolesDockerStopNodesDir;

  @Value("${process.ansible.path.kibana.roles.docker-start-nodes-file}")
  private String ansiblePathKibanaRolesDockerStartNodesFile;

  @Value("${process.ansible.path.kibana.roles.docker-stop-nodes-file}")
  private String ansiblePathKibanaRolesDockerStopNodesFile;

  @Value("${process.ansible.path.kibana.install}")
  private String ansiblePathKibanaInstall;

  private StringBuffer responseBuffer;

  public ResponseEntity<String> createByAnsible(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    log.debug("{}", m.toString());

    // Step 1) Create Docker Compose configuration for kibana
    createDockerComposeConfiguration(m);

    // Step 2) Create Ansible configuration
    // Step 3) Create ansible inventories/elasticsearch/hosts
    createAnsibleInventories(m);

    // Step 4) Create ansible roles/elasticsearch/tasks/main.yml per node
    createAnsibleRoles(m);

    // Step 5) Run ansible
    runAnsiblePlaybook(m);

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(responseBuffer.toString()));
  }

  public ResponseEntity<String> startKibanaByAnsible(InstanceRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    command = kibanaDockerStart;
    command = command.replace(
      "_ANSIBLE_",
      m.getWorkingPath() + ansiblePathKibana
    );
    command = command.replace("_PRIVATEIP_", m.getKibanaRequestModel().getIp());

    log.debug(command);

    try {
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

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

  public ResponseEntity<String> stopKibanaByAnsible(InstanceRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    command = kibanaDockerStop;
    command = command.replace(
      "_ANSIBLE_",
      m.getWorkingPath() + ansiblePathKibana
    );
    command = command.replace("_PRIVATEIP_", m.getKibanaRequestModel().getIp());

    log.debug(command);

    try {
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

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

  public ResponseEntity<String> createTemporaryAnsibleForNodeRestart(ManagementRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    createAnsibleInventoriesTemp(m);

    createAnsibleRolesTemp(m);

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(m));
  }

  private void createAnsibleInventoriesTemp(ManagementRequestModel m) {
    String temporaryAnsiblePath = ansiblePathTemporary + "/" + String.valueOf(System.currentTimeMillis());
    String template = "";
    String ips = "";

    m.setTemporaryAnsiblePath(temporaryAnsiblePath);

    FileUtil.makeDirectory(
      temporaryAnsiblePath + ansiblePathKibanaInventoriesNodes
    );

    ips = m.getNodeIps();

    log.debug("{}", ips);

    // read ansible inventories template file
    template = FileUtil.readResourceFile(templateAnsibleInventoriesKibana);

    // replace variables
    template = template.replace("_PRIVATEIP_", ips);
    template = template.replace("_KEYPEM_", m.getKeyPem());
    template = template.replace("_BASTIONIP_", m.getBastionIp());

    log.debug("{}", template);

    // write ansible inventories hosts
    FileUtil.writeTemplateFile(
      temporaryAnsiblePath +
        templateAnsibleInventoriesKibanaNodes,
      template
    );
  }

  private void createAnsibleRolesTemp(ManagementRequestModel m) {
    String template = "";

    // make roles
    FileUtil.makeDirectory(
      m.getTemporaryAnsiblePath() + ansiblePathKibanaRolesDockerStartNodesDir
    );
    FileUtil.makeDirectory(
      m.getTemporaryAnsiblePath() + ansiblePathKibanaRolesDockerStopNodesDir
    );

    // write docker-compose up/down roles
    template = FileUtil.readResourceFile(templateAnsibleRolesKibanaDockerStart);
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathKibanaRolesDockerStartNodesFile,
      template
    );

    log.debug("{}", template);

    template = FileUtil.readResourceFile(templateAnsibleRolesKibanaDockerStop);
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathKibanaRolesDockerStopNodesFile,
      template
    );

    log.debug("{}", template);

    // write playbook
    template = FileUtil.readResourceFile(templateAnsiblePlaybookKibanaDockerStart);
    template = template.replace("_PRIVATEIP_", "nodes");
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathKibanaPlaybookDockerStartNodes,
      template
    );

    log.debug("{}", template);

    template = FileUtil.readResourceFile(templateAnsiblePlaybookKibanaDockerStop);
    template = template.replace("_PRIVATEIP_", "nodes");
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathKibanaPlaybookDockerStopNodes,
      template
    );

    log.debug("{}", template);
  }

  private void createDockerComposeConfiguration(InstanceRequestModel m) {
    String template = "";

    template = FileUtil.readResourceFile(templateKibanaDockerCompose);

    // make dir per node
    FileUtil.makeDirectory(
      m.getWorkingPath() +
        dockerPathKibana + "/" +
        m.getKibanaRequestModel().getIp()
    );

    // replace variables
    // _VERSION_ _ELASTICSEARCHHOSTS_ _KIBANAPORT_
    template = template.replace("_PRIVATEIP_", m.getKibanaRequestModel().getIp());
    template = template.replace("_VERSION_", m.getKibanaRequestModel().getVersion());
    template = template.replace("_KIBANAPORT_", String.valueOf(m.getKibanaRequestModel().getPort()));
    template = template.replace("_ELASTICSEARCHHOSTS_", m.getKibanaRequestModel().getElasticsearchHosts());

    log.debug("{}", template);

    FileUtil.writeTemplateFile(
      m.getWorkingPath() +
        dockerPathKibana + "/" +
        m.getKibanaRequestModel().getIp()+"/docker-compose.yml",
      template
    );
  }

  private void createAnsibleInventories(InstanceRequestModel m) {
    String template = "";

    FileUtil.makeDirectory(
      m.getWorkingPath() +
        ansiblePathKibanaInventories +
        "/" +
        m.getKibanaRequestModel().getIp()
    );

    // read ansible inventories template file
    template = FileUtil.readResourceFile(templateAnsibleInventoriesKibana);

    // replace variables
    template = template.replace("_PRIVATEIP_", m.getKibanaRequestModel().getIp());
    template = template.replace("_KEYPEM_", m.getTerraformRequestModel().getKeyPem());
    template = template.replace("_BASTIONIP_", m.getBastionIp());

    log.debug("{}", template);

    // write ansible inventories hosts
    FileUtil.writeTemplateFile(
      m.getWorkingPath() +
        ansiblePathKibanaInventories +
        "/" +
        m.getKibanaRequestModel().getIp() +
        "/hosts",
      template
    );
  }

  private void createAnsibleRoles(InstanceRequestModel m) {
    String template = "";
    String replaceSource = "";
    String privateIp = m.getKibanaRequestModel().getIp();

    replaceSource = ansiblePathKibanaRoles;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.makeDirectory(
      m.getWorkingPath() + replaceSource
    );

    replaceSource = ansiblePathKibanaRolesDockerStartDir;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.makeDirectory(
      m.getWorkingPath() + replaceSource
    );

    replaceSource = ansiblePathKibanaRolesDockerStopDir;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.makeDirectory(
      m.getWorkingPath() + replaceSource
    );

    // read ansible script template
    template = FileUtil.readResourceFile(templateTerraformInit);
    // write ansible script template
    FileUtil.writeTemplateFile(
      m.getTerraformRequestModel().getTfPath() + "/init.sh",
      template
    );

    // read ansible roles template file
    template = FileUtil.readResourceFile(templateAnsibleRolesKibana);

    // replace variables
    template = template.replace(
      "_INITSCRIPT_",
      m.getTerraformRequestModel().getTfPath()+"/init.sh"
    );

    replaceSource = dockerPathKibanaDockerCompose;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    template = template.replace(
      "_DOCKERCOMPOSESRC_",
      m.getWorkingPath() + replaceSource
    );

    template = template.replace(
      "_DOCKERCOMPOSEDEST_",
      templateDockerKibanaDockerCompose
    );

    // write ansible roles tasks
    replaceSource = ansiblePathKibanaRoles;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.writeTemplateFile(
      m.getWorkingPath() +
        replaceSource +
        "/main.yml",
      template
    );

    // write ansible docker-compose roles tasks
    template = FileUtil.readResourceFile(templateAnsibleRolesKibanaDockerStart);
    replaceSource = ansiblePathKibanaRolesDockerStartFile;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.writeTemplateFile(
      m.getWorkingPath() + replaceSource,
      template
    );

    template = FileUtil.readResourceFile(templateAnsibleRolesKibanaDockerStop);
    replaceSource = ansiblePathKibanaRolesDockerStopFile;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.writeTemplateFile(
      m.getWorkingPath() + replaceSource,
      template
    );

    log.debug("{}", template);

    // write playbook
    template = FileUtil.readResourceFile(templateAnsiblePlaybookKibana);
    template = template.replace("_PRIVATEIP_", privateIp);
    replaceSource = ansiblePathKibanaPlaybook;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.writeTemplateFile(
      m.getWorkingPath() + replaceSource,
      template
    );

    log.debug("+++++++++++++++++++++++++++++++");
    log.debug("{}", template);
    log.debug("{}", replaceSource);
    log.debug("+++++++++++++++++++++++++++++++");

    template = FileUtil.readResourceFile(templateAnsiblePlaybookKibanaDockerStart);
    template = template.replace("_PRIVATEIP_", privateIp);
    replaceSource = ansiblePathKibanaPlaybookDockerStart;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.writeTemplateFile(
      m.getWorkingPath() + replaceSource,
      template
    );

    template = FileUtil.readResourceFile(templateAnsiblePlaybookKibanaDockerStop);
    template = template.replace("_PRIVATEIP_", privateIp);
    replaceSource = ansiblePathKibanaPlaybookDockerStop;
    replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
    FileUtil.writeTemplateFile(
      m.getWorkingPath() + replaceSource,
      template
    );

    log.debug("{}", template);
  }

  private void runAnsiblePlaybook(InstanceRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;

    command = ansiblePathKibanaInstall;
    command = command.replace("_ANSIBLE_", m.getWorkingPath() + ansiblePathKibana);
    command = command.replace("_PRIVATEIP_", m.getKibanaRequestModel().getIp());

    log.debug(command);

    try {
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

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

  public ResponseEntity<String> startNodeByAnsible(ManagementRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    log.debug("====> {}", kbNodeDockerStart);

    command = kbNodeDockerStart;
    command = command.replace(
      "_ANSIBLE_",
      m.getTemporaryAnsiblePath() + ansiblePathKibana
    );

    log.debug("====> {}", command);

    try {
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

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

  public ResponseEntity<String> stopNodeByAnsible(ManagementRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    command = kbNodeDockerStop;
    command = command.replace(
      "_ANSIBLE_",
      m.getTemporaryAnsiblePath() + ansiblePathKibana
    );

    log.debug(command);

    try {
      builder.command("sh", "-c", command);
      process = builder.redirectErrorStream(true).start();

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
}

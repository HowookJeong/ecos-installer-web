package com.mzc.ecos.installer.api.aws.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mzc.ecos.core.util.FileUtil;
import com.mzc.ecos.installer.api.aws.model.ManagementRequestModel;
import com.mzc.ecos.installer.api.aws.model.InstanceRequestModel;
import com.mzc.ecos.installer.api.aws.model.NodeRequestModel;
import com.mzc.ecos.installer.api.constant.InMemory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
public class ElasticsearchService {
  @Value("${process.ansible.esnode.docker-start}")
  private String esNodeDockerStart;

  @Value("${process.ansible.esnode.docker-stop}")
  private String esNodeDockerStop;

  @Value("${process.ansible.escluster.docker-start}")
  private String esClusterDockerStart;

  @Value("${process.ansible.escluster.docker-stop}")
  private String esClusterDockerStop;

  @Value("${process.ansible.path.elasticsearch.base}")
  private String ansiblePathElasticsearch;

  @Value("${process.docker.path.elasticsearch.base}")
  private String dockerPathElasticsearch;

  @Value("${process.docker.path.elasticsearch.docker-compose}")
  private String dockerPathElasticsearchDockerCompose;

  @Value("${process.ansible.path.temporary}")
  private String ansiblePathTemporary;

  @Value("${process.ansible.path.elasticsearch.inventories.base}")
  private String ansiblePathElasticsearchInventories;

  @Value("${process.ansible.path.elasticsearch.inventories.nodes}")
  private String ansiblePathElasticsearchInventoriesNodes;

  @Value("${process.ansible.path.elasticsearch.roles.base}")
  private String ansiblePathElasticsearchRoles;

  @Value("${process.ansible.path.elasticsearch.roles.docker-start-dir}")
  private String ansiblePathElasticsearchRolesDockerStartDir;

  @Value("${process.ansible.path.elasticsearch.roles.docker-stop-dir}")
  private String ansiblePathElasticsearchRolesDockerStopDir;

  @Value("${process.ansible.path.elasticsearch.roles.docker-start-file}")
  private String ansiblePathElasticsearchRolesDockerStartFile;

  @Value("${process.ansible.path.elasticsearch.roles.docker-stop-file}")
  private String ansiblePathElasticsearchRolesDockerStopFile;

  @Value("${process.ansible.path.elasticsearch.playbook.base}")
  private String ansiblePathElasticsearchPlaybook;

  @Value("${process.ansible.path.elasticsearch.playbook.docker-start}")
  private String ansiblePathElasticsearchPlaybookDockerStart;

  @Value("${process.ansible.path.elasticsearch.playbook.docker-stop}")
  private String ansiblePathElasticsearchPlaybookDockerStop;

  @Value("${process.ansible.path.elasticsearch.playbook.docker-start-nodes}")
  private String ansiblePathElasticsearchPlaybookDockerStartNodes;

  @Value("${process.ansible.path.elasticsearch.playbook.docker-stop-nodes}")
  private String ansiblePathElasticsearchPlaybookDockerStopNodes;

  @Value("${process.ansible.path.elasticsearch.roles.docker-start-nodes-dir}")
  private String ansiblePathElasticsearchRolesDockerStartNodesDir;

  @Value("${process.ansible.path.elasticsearch.roles.docker-stop-nodes-dir}")
  private String ansiblePathElasticsearchRolesDockerStopNodesDir;

  @Value("${process.ansible.path.elasticsearch.roles.docker-start-nodes-file}")
  private String ansiblePathElasticsearchRolesDockerStartNodesFile;

  @Value("${process.ansible.path.elasticsearch.roles.docker-stop-nodes-file}")
  private String ansiblePathElasticsearchRolesDockerStopNodesFile;

  @Value("${process.ansible.path.elasticsearch.install}")
  private String ansiblePathElasticsearchInstall;

  @Value("${process.template.elasticsearch.docker-compose}")
  private String templateElasticsearchDockerCompose;

  @Value("${process.template.elasticsearch.java-opts}")
  private String templateElasticsearchJavaOpts;

  @Value("${process.template.ansible.inventories.elasticsearch.base}")
  private String templateAnsibleInventoriesElasticsearch;

  @Value("${process.template.ansible.inventories.elasticsearch.nodes}")
  private String templateAnsibleInventoriesElasticsearchNodes;

  @Value("${process.template.ansible.roles.elasticsearch.base}")
  private String templateAnsibleRolesElasticsearch;

  @Value("${process.template.ansible.roles.elasticsearch.docker-start}")
  private String templateAnsibleRolesElasticsearchDockerStart;

  @Value("${process.template.ansible.roles.elasticsearch.docker-stop}")
  private String templateAnsibleRolesElasticsearchDockerStop;

  @Value("${process.template.ansible.playbook.elasticsearch.base}")
  private String templateAnsiblePlaybookElasticsearch;

  @Value("${process.template.ansible.playbook.elasticsearch.docker-start}")
  private String templateAnsiblePlaybookElasticsearchDockerStart;

  @Value("${process.template.ansible.playbook.elasticsearch.docker-stop}")
  private String templateAnsiblePlaybookElasticsearchDockerStop;

  @Value("${process.terraform.path.temporary.base}")
  private String terraformPathTemporary;

  @Value("${process.template.terraform.elasticsearch.init}")
  private String templateTerraformInit;

  @Value("${process.template.terraform.provider}")
  private String templateTerraformProvider;

  @Value("${process.template.docker.elasticsearch.docker-compose}")
  private String templateDockerElasticsearchDockerCompose;

  private StringBuffer responseBuffer;

  public ResponseEntity<String> createClusterByAnsible(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    log.debug("{}", m.toString());

    // Step 1) Create Docker Compose configuration per node
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

  public ResponseEntity<String> appendClusterByAnsible(InstanceRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    // Step 1) Create Docker Compose configuration per node
    appendDockerComposeConfiguration(m);

    // Step 2) Create Ansible configuration
    // Step 3) Create ansible inventories/elasticsearch/hosts
    createAnsibleInventories(m);

    // Step 4) Create ansible roles/elasticsearch/tasks/main.yml per node
    createAnsibleRoles(m);

    // Step 5) Run ansible
    runAnsiblePlaybook(m);

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(responseBuffer.toString()));
  }

  public ResponseEntity<String> createTemporaryAnsibleForNodeRestart(ManagementRequestModel m) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    createAnsibleInventoriesTemp(m);

    createAnsibleRolesTemp(m);

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(m));
  }

  public ResponseEntity<String> startNodeByAnsible(ManagementRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    command = esNodeDockerStart;
    command = command.replace(
      "_ANSIBLE_",
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearch
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

  public ResponseEntity<String> stopNodeByAnsible(ManagementRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    command = esNodeDockerStop;
    command = command.replace(
      "_ANSIBLE_",
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearch
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

  public ResponseEntity<String> startClusterByAnsible(InstanceRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if (nrm.getInstanceSize() > 0) {
        for (String privateIp : nrm.getPrivateIps()) {
          command = esClusterDockerStart;
          command = command.replace(
            "_ANSIBLE_",
            m.getWorkingPath() + ansiblePathElasticsearch
          );
          command = command.replace("_PRIVATEIP_", privateIp);

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
      }
    }

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(responseBuffer.toString()));
  }

  public ResponseEntity<String> stopClusterByAnsible(InstanceRequestModel m) {
    ProcessBuilder builder = new ProcessBuilder();
    Process process;
    String output;
    String command;
    int exitCode = 0;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    responseBuffer = new StringBuffer();

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if (nrm.getInstanceSize() > 0) {
        for (String privateIp : nrm.getPrivateIps()) {
          command = esClusterDockerStop;
          command = command.replace(
            "_ANSIBLE_",
            m.getWorkingPath() + ansiblePathElasticsearch
          );
          command = command.replace("_PRIVATEIP_", privateIp);

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
      }
    }

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(gson.toJson(responseBuffer.toString()));
  }

  private void createDockerComposeConfiguration(InstanceRequestModel m) {
    String template = "";
    String discoverySeedHosts = "";
    String clusterInitialMasterNodes = "";
    String nodeMaster = "";
    String nodeData = "";

    discoverySeedHosts = getDiscoverySeedHosts(m);
    clusterInitialMasterNodes = getClusterInitialMasterNodes(m);

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if ( nrm.getInstanceSize() > 0 ) {
        for ( String privateIp : nrm.getPrivateIps() ) {
          // read docker compose template file
          template = FileUtil.readResourceFile(templateElasticsearchDockerCompose);

          // make dir per node
          FileUtil.makeDirectory(
            m.getWorkingPath() +
            dockerPathElasticsearch + "/" +
            privateIp
          );

          // replace variables
          template = template.replace("_NODETOPOLOGY_", nrm.getTopology());
          template = template.replace(
            "_VERSION_",
            m.getElasticsearchClusterRequestModel().getEsVersion()
          );
          template = template.replace(
            "_CLUSTERNAME_",
            m.getElasticsearchClusterRequestModel().getClusterName()
          );

          template = template.replace(
            "_NODENAME_",
            nrm.getTopology() + "-" + privateIp
          );

          nodeMaster = InMemory.getElasticsearchNodeType().get(nrm.getTopology()+".master");
          nodeData = InMemory.getElasticsearchNodeType().get(nrm.getTopology()+".data");

          template = template.replace("_NODEMASTER_", nodeMaster);
          template = template.replace("_NODEDATA_", nodeData);

          template = template.replace("_NETWORKPUBLISHHOST_", privateIp);
          template = template.replace("_DISCOVERYSEEDHOSTS_", discoverySeedHosts);

          if ( isCluster(m)  ) {
            template = template.replace("_DISCOVERYANDCLUSTERMASTER_", "cluster.initial_master_nodes="+clusterInitialMasterNodes);
          } else {
            template = template.replace("_DISCOVERYANDCLUSTERMASTER_", "discovery.type=single-node");
          }

          template = template.replace("_ESJAVAOPTS_", getEsJavaOpts(nrm));
          template = template.replace("_VOLUMESELASTICSEARCHDATA_", nrm.getPathData());
          template = template.replace("_VOLUMESELASTICSEARCHLOGS_", nrm.getPathLogs());
          template = template.replace("_PORTSELASTICSEARCHTTPPORT_", String.valueOf(nrm.getHttpPort()));
          template = template.replace("_PORTSELASTICSEARCHTCPPORT_", String.valueOf(nrm.getTcpPort()));

          log.debug("{}", template);
          // write docker compose file per node
          FileUtil.writeTemplateFile(
            m.getWorkingPath() +
            dockerPathElasticsearch + "/" +
            privateIp+"/docker-compose.yml",
            template
          );
        }
      }
    }
  }

  private void appendDockerComposeConfiguration(InstanceRequestModel m) {
    String template = "";
    String discoverySeedHosts = "";
    String nodeMaster = "";
    String nodeData = "";

    discoverySeedHosts = getDiscoverySeedHosts(m) + "," + m.getElasticsearchClusterRequestModel().getSeedHosts();

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if ( nrm.getInstanceSize() > 0 ) {
        for ( String privateIp : nrm.getPrivateIps() ) {
          // read docker compose template file
          template = FileUtil.readResourceFile(templateElasticsearchDockerCompose);

          // make dir per node
          FileUtil.makeDirectory(
            m.getWorkingPath() + dockerPathElasticsearch + "/" + privateIp
          );

          // replace variables
          template = template.replace("_NODETOPOLOGY_", nrm.getTopology());
          template = template.replace(
            "_VERSION_",
            m.getElasticsearchClusterRequestModel().getEsVersion()
          );
          template = template.replace(
            "_CLUSTERNAME_",
            m.getElasticsearchClusterRequestModel().getClusterName()
          );

          template = template.replace(
            "_NODENAME_",
            nrm.getTopology() + "-" + privateIp
          );

          nodeMaster = InMemory.getElasticsearchNodeType().get(nrm.getTopology()+".master");
          nodeData = InMemory.getElasticsearchNodeType().get(nrm.getTopology()+".data");

          template = template.replace("_NODEMASTER_", nodeMaster);
          template = template.replace("_NODEDATA_", nodeData);

          template = template.replace("_NETWORKPUBLISHHOST_", privateIp);
          template = template.replace("_DISCOVERYSEEDHOSTS_", discoverySeedHosts);

          template = template.replace("_DISCOVERYANDCLUSTERMASTER_", "dummy");

          template = template.replace("_ESJAVAOPTS_", getEsJavaOpts(nrm));
          template = template.replace("_VOLUMESELASTICSEARCHDATA_", nrm.getPathData());
          template = template.replace("_VOLUMESELASTICSEARCHLOGS_", nrm.getPathLogs());
          template = template.replace("_PORTSELASTICSEARCHTTPPORT_", String.valueOf(nrm.getHttpPort()));
          template = template.replace("_PORTSELASTICSEARCHTCPPORT_", String.valueOf(nrm.getTcpPort()));

          log.debug("{}", template);
          // write docker compose file per node
          FileUtil.writeTemplateFile(
            m.getWorkingPath() +
            dockerPathElasticsearch +
            "/" +
            privateIp+
            "/docker-compose.yml",
            template
          );
        }
      }
    }
  }

  private boolean isCluster(InstanceRequestModel m) {
    int size = 0;

    for ( NodeRequestModel nrm : m.getInstances() ) {
      size += nrm.getInstanceSize();
    }

    if ( size > 1 ) {
      return true;
    } else {
      return false;
    }
  }

  private String getDiscoverySeedHosts(InstanceRequestModel m) {
    List<String> ips = new ArrayList<>();

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if (nrm.getInstanceSize() > 0) {
        ips.addAll(nrm.getPrivateIps());
      }
    }

    return String.join(",", ips);
  }

  private String getClusterInitialMasterNodes(InstanceRequestModel m) {
    List<String> ips = new ArrayList<>();

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if (nrm.getInstanceSize() > 0 &&
        ( nrm.getTopology().equalsIgnoreCase("master") ||
        nrm.getTopology().equalsIgnoreCase("allinone") )
      ) {
        ips.addAll(nrm.getPrivateIps());
        break;
      }
    }

    return String.join(",", ips);
  }

  private String getEsJavaOpts(NodeRequestModel m) {
    String opts = templateElasticsearchJavaOpts;
    int heapSize = (m.getMemSize() * 1024) / 2;

    if ( heapSize > 32768 ) {
      heapSize = 31744;
    }

    opts = opts.replace("_HEAPSIZE_", String.valueOf(heapSize));

    return opts;
  }

  private void createAnsibleInventories(InstanceRequestModel m) {
    String template = "";

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if (nrm.getInstanceSize() > 0) {
        for ( String privateIp : nrm.getPrivateIps() ) {
          // make dir for elasticsearch cluster
          FileUtil.makeDirectory(
            m.getWorkingPath() +
            ansiblePathElasticsearchInventories +
            "/" +
            privateIp
          );

          // read ansible inventories template file
          template = FileUtil.readResourceFile(templateAnsibleInventoriesElasticsearch);

          // replace variables
          template = template.replace("_NODETOPOLOGY_", nrm.getTopology());
          template = template.replace("_PRIVATEIP_", privateIp);
          template = template.replace("_KEYPEM_", m.getTerraformRequestModel().getKeyPem());
          template = template.replace("_BASTIONIP_", m.getBastionIp());

          log.debug("{}", template);

          // write ansible inventories hosts
          FileUtil.writeTemplateFile(
          m.getWorkingPath() +
            ansiblePathElasticsearchInventories +
            "/" +
            privateIp +
            "/hosts",
            template
          );
        }
      }
    }
  }

  private void createAnsibleInventoriesTemp(ManagementRequestModel m) {
    String temporaryAnsiblePath = ansiblePathTemporary + "/" + String.valueOf(System.currentTimeMillis());
    String template = "";
    String ips = "";

    m.setTemporaryAnsiblePath(temporaryAnsiblePath);

    FileUtil.makeDirectory(
      temporaryAnsiblePath + ansiblePathElasticsearchInventoriesNodes
    );

    List<String> nodeIps = Stream.of(
      m.getNodeIps()
        .split(",", -1)
    ).collect(Collectors.toList());

    ips = String.join(System.getProperty("line.separator"), nodeIps);

    log.debug("{}", ips);

    // read ansible inventories template file
    template = FileUtil.readResourceFile(templateAnsibleInventoriesElasticsearch);

    // replace variables
    template = template.replace("_PRIVATEIP_", ips);
    template = template.replace("_KEYPEM_", m.getKeyPem());
    template = template.replace("_BASTIONIP_", m.getBastionIp());

    log.debug("{}", template);

    // write ansible inventories hosts
    FileUtil.writeTemplateFile(
    temporaryAnsiblePath +
      templateAnsibleInventoriesElasticsearchNodes,
      template
    );
  }

  private void createAnsibleRoles(InstanceRequestModel m) {
    String template = "";
    String replaceSource = "";

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if (nrm.getInstanceSize() > 0) {
        for ( String privateIp : nrm.getPrivateIps() ) {
          // make dir per node
          replaceSource = ansiblePathElasticsearchRoles;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.makeDirectory(
            m.getWorkingPath() + replaceSource
          );

          replaceSource = ansiblePathElasticsearchRolesDockerStartDir;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.makeDirectory(
            m.getWorkingPath() + replaceSource
          );

          replaceSource = ansiblePathElasticsearchRolesDockerStopDir;
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
          template = FileUtil.readResourceFile(templateAnsibleRolesElasticsearch);

          // replace variables
          template = template.replace(
            "_INITSCRIPT_",
            m.getTerraformRequestModel().getTfPath()+"/init.sh"
          );
          template = template.replace("_VOLUMESELASTICSEARCHDATA_", nrm.getPathData());
          template = template.replace("_VOLUMESELASTICSEARCHLOGS_", nrm.getPathLogs());

          replaceSource = dockerPathElasticsearchDockerCompose;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          template = template.replace(
            "_DOCKERCOMPOSESRC_",
            m.getWorkingPath() + replaceSource
          );

          template = template.replace(
            "_DOCKERCOMPOSEDEST_",
            templateDockerElasticsearchDockerCompose
          );

          // write ansible roles tasks
          replaceSource = ansiblePathElasticsearchRoles;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.writeTemplateFile(
          m.getWorkingPath() +
            replaceSource +
            "/main.yml",
            template
          );

          // write ansible docker-compose roles tasks
          template = FileUtil.readResourceFile(templateAnsibleRolesElasticsearchDockerStart);
          replaceSource = ansiblePathElasticsearchRolesDockerStartFile;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.writeTemplateFile(
            m.getWorkingPath() + replaceSource,
            template
          );

          template = FileUtil.readResourceFile(templateAnsibleRolesElasticsearchDockerStop);
          replaceSource = ansiblePathElasticsearchRolesDockerStopFile;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.writeTemplateFile(
            m.getWorkingPath() + replaceSource,
            template
          );

          log.debug("{}", template);

          // write playbook
          template = FileUtil.readResourceFile(templateAnsiblePlaybookElasticsearch);
          template = template.replace("_PRIVATEIP_", privateIp);
          replaceSource = ansiblePathElasticsearchPlaybook;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.writeTemplateFile(
            m.getWorkingPath() + replaceSource,
            template
          );

          log.debug("+++++++++++++++++++++++++++++++");
          log.debug("{}", template);
          log.debug("{}", replaceSource);
          log.debug("+++++++++++++++++++++++++++++++");

          template = FileUtil.readResourceFile(templateAnsiblePlaybookElasticsearchDockerStart);
          template = template.replace("_PRIVATEIP_", privateIp);
          replaceSource = ansiblePathElasticsearchPlaybookDockerStart;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.writeTemplateFile(
            m.getWorkingPath() + replaceSource,
            template
          );

          template = FileUtil.readResourceFile(templateAnsiblePlaybookElasticsearchDockerStop);
          template = template.replace("_PRIVATEIP_", privateIp);
          replaceSource = ansiblePathElasticsearchPlaybookDockerStop;
          replaceSource = replaceSource.replace("_PRIVATEIP_", privateIp);
          FileUtil.writeTemplateFile(
            m.getWorkingPath() + replaceSource,
            template
          );

          log.debug("{}", template);
        }
      }
    }
  }

  private void createAnsibleRolesTemp(ManagementRequestModel m) {
    String template = "";

    // make roles
    FileUtil.makeDirectory(
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearchRolesDockerStartNodesDir
    );
    FileUtil.makeDirectory(
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearchRolesDockerStopNodesDir
    );

    // write docker-compose up/down roles
    template = FileUtil.readResourceFile(templateAnsibleRolesElasticsearchDockerStart);
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearchRolesDockerStartNodesFile,
      template
    );

    log.debug("{}", template);

    template = FileUtil.readResourceFile(templateAnsibleRolesElasticsearchDockerStop);
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearchRolesDockerStopNodesFile,
      template
    );

    log.debug("{}", template);

    // write playbook
    template = FileUtil.readResourceFile(templateAnsiblePlaybookElasticsearchDockerStart);
    template = template.replace("_PRIVATEIP_", "nodes");
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearchPlaybookDockerStartNodes,
      template
    );

    log.debug("{}", template);

    template = FileUtil.readResourceFile(templateAnsiblePlaybookElasticsearchDockerStop);
    template = template.replace("_PRIVATEIP_", "nodes");
    FileUtil.writeTemplateFile(
      m.getTemporaryAnsiblePath() + ansiblePathElasticsearchPlaybookDockerStopNodes,
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

    for ( NodeRequestModel nrm : m.getInstances() ) {
      if (nrm.getInstanceSize() > 0) {
        for (String privateIp : nrm.getPrivateIps()) {
          command = ansiblePathElasticsearchInstall;
          command = command.replace("_ANSIBLE_", m.getWorkingPath() + ansiblePathElasticsearch);
          command = command.replace("_PRIVATEIP_", privateIp);

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
      }
    }
  }
}

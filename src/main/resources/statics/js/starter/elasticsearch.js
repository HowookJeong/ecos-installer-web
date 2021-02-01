$(document).ready(function () {
  describeInstanceTypes();
  hardResetNode();
  drawConsoleLog("ecosConsoleLog");
});

function drawConsoleLog(viewer){
  var beautify = ace.require("ace/ext/beautify");
  var editor = ace.edit(viewer);
//  editor.setOptions({ maxLines: Infinity });
  editor.setOptions({ newLineMode: 'auto' });
  editor.setTheme("ace/theme/nord_dark");
  editor.session.setMode("ace/mode/text");
  editor.setShowInvisibles(false);
  editor.setReadOnly(true);
  editor.setShowPrintMargin(false);
  editor.session.setTabSize(2);
  editor.session.setUseSoftTabs(true);
  editor.session.setUseWrapMode(true);

  try {
    beautify.beautify(editor.session);
  } catch(e) {
  }

  editor.resize();
}

function setConsoleLog(viewer, log) {
  let editor = ace.edit(viewer);

  log = log.replace(/\u001b\[.*?m/ig, '');
  editor.setValue(
    editor.getValue() +
    "\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" +
    "\n+++++++++++++++++++++++++++++++ End of Logs +++++++++++++++++++++++++++++++\n" +
    "\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n" +
    log
  );

  drawConsoleLog(viewer);
}

function describeInstanceTypes() {
  $.ajax({
    url: "/api/aws/ec2/describe-instance-types",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
    }),
    success: function (res) {
      describeInstanceTypesShow(res);
    },
    error: function (res) {
      console.log(res);
    }
  });
}

function describeInstanceTypesShow(res) {
  const fm = document.infrastructureForm;
  let instances = res.InstanceTypes;
  let template = "<option value='_INSTANCETYPE_/_VCPU_/_MEM_'>_INSTANCETYPE_ (_VCPU_ / _MEM_ GiB)</option>";
  let item;

  instances.forEach(function (instance) {
    item = "";
    item = template.replace(/_INSTANCETYPE_/gi, instance.InstanceType);
    item = item.replace(/_VCPU_/gi, instance.VCpuInfo.DefaultVCpus);
    item = item.replace(/_MEM_/gi, instance.MemoryInfo.SizeInMiB/1024);

    if ( instance.MemoryInfo.SizeInMiB/1024 >= 1 ) {
      $('#instanceType').append(item);
    }
  });
}

let instanceRequestModel = {
  terraformRequestModel: {
    vpcSecurityGroupIds:"",
    subnetId:"",
    zoneId:"",
    keyPem:"",
    amiId:"ami-0d0bbf63",
    tfPath:"",
    env:"dev",
    backendBucket:"",
    backendKey:"",
    isBackend:false
  },
  elasticsearchClusterRequestModel: {
    clusterName:"",
    esVersion:"",
    backendBucket:"",
    backendKey:"",
    seedHosts:"",
  },
  nodeSize:0,
  instances:[],
  workingPath:"",
  bastionIp:"",
}

let nodeRequestModel = function () {
  this.vendor = "";
  this.stack = "";
  this.topology = "";
  this.instanceType = "";
  this.coreSize = 0;
  this.memSize = 0;
  this.instanceSize = 0;
  this.volumeSize = 10;
  this.privateIps = [];
  this.httpPort = 9200;
  this.tcpPort = 9300;
  this.pathData = "";
  this.pathLogs = "";
}

function hardResetNode() {
  let template = "<td>_INSTANCETYPE_</td><td>_INSTANCESIZE_</td><td>_VOLUMESIZE_ GiB</td>";

  template = template.replace(/_INSTANCETYPE_/gi, "t2.micro");
  template = template.replace(/_INSTANCESIZE_/gi, "0");
  template = template.replace(/_VOLUMESIZE_/gi, "10");

  instanceRequestModel.nodeSize = 4;
  instanceRequestModel.instances[0] = new nodeRequestModel();
  instanceRequestModel.instances[0].topology = "allinone";
  instanceRequestModel.instances[0].vendor = "aws";
  instanceRequestModel.instances[0].stack = "elasticsearch";
  instanceRequestModel.instances[0].instanceType = "t2.micro";
  instanceRequestModel.instances[0].coreSize = 1;
  instanceRequestModel.instances[0].memSize = 1;
  instanceRequestModel.instances[0].instanceSize = 0;
  instanceRequestModel.instances[0].volumeSize = 10;
  instanceRequestModel.instances[0].pathData = "/usr/share/elasticsearch/data";
  instanceRequestModel.instances[0].pathLogs = "/usr/share/elasticsearch/logs";
  $('#allinoneNode').html("<td>Master and Data</td>"+template);

  instanceRequestModel.instances[1] = new nodeRequestModel();
  instanceRequestModel.instances[1].topology = "master";
  instanceRequestModel.instances[1].vendor = "aws";
  instanceRequestModel.instances[1].stack = "elasticsearch";
  instanceRequestModel.instances[1].instanceType = "t2.micro";
  instanceRequestModel.instances[1].coreSize = 1;
  instanceRequestModel.instances[1].memSize = 1;
  instanceRequestModel.instances[1].instanceSize = 0;
  instanceRequestModel.instances[1].volumeSize = 10;
  instanceRequestModel.instances[1].pathData = "/usr/share/elasticsearch/data";
  instanceRequestModel.instances[1].pathLogs = "/usr/share/elasticsearch/logs";
  $('#masterNode').html("<td>Master</td>"+template);

  instanceRequestModel.instances[2] = new nodeRequestModel();
  instanceRequestModel.instances[2].topology = "data";
  instanceRequestModel.instances[2].vendor = "aws";
  instanceRequestModel.instances[2].stack = "elasticsearch";
  instanceRequestModel.instances[2].instanceType = "t2.micro";
  instanceRequestModel.instances[2].coreSize = 1;
  instanceRequestModel.instances[2].memSize = 1;
  instanceRequestModel.instances[2].instanceSize = 0;
  instanceRequestModel.instances[2].volumeSize = 10;
  instanceRequestModel.instances[2].pathData = "/usr/share/elasticsearch/data";
  instanceRequestModel.instances[2].pathLogs = "/usr/share/elasticsearch/logs";
  $('#dataNode').html("<td>Data</td>"+template);

  instanceRequestModel.instances[3] = new nodeRequestModel();
  instanceRequestModel.instances[3].topology = "coordinator";
  instanceRequestModel.instances[3].vendor = "aws";
  instanceRequestModel.instances[3].stack = "elasticsearch";
  instanceRequestModel.instances[3].instanceType = "t2.micro";
  instanceRequestModel.instances[3].coreSize = 1;
  instanceRequestModel.instances[3].memSize = 1;
  instanceRequestModel.instances[3].instanceSize = 0;
  instanceRequestModel.instances[3].volumeSize = 10;
  instanceRequestModel.instances[3].pathData = "/usr/share/elasticsearch/data";
  instanceRequestModel.instances[3].pathLogs = "/usr/share/elasticsearch/logs";
  $('#coordinatorNode').html("<td>Coordinator</td>"+template);

}

function resetAppendMode() {
  const tfFm = document.terraformForm;

  tfFm.tfPath.value = "/tmp/home/mzc/app/terraform/_CLUSTERNAME_";
  tfFm.terraformBackendKey.value = "_CLUSTERNAME_/terraform.tfstate";
}

function registerNode() {
  const fm = document.infrastructureForm;
  const tfFm = document.terraformForm;
  const esFm = document.elasticsearchForm;
  let nrm = new nodeRequestModel();
  let instanceSpecInfo = fm.instanceType[fm.instanceType.selectedIndex].value.split("/");
  let appendClusterName = "";

  instanceRequestModel.elasticsearchClusterRequestModel.clusterName = fm.clusterName.value;
  esFm.backendKey.value = esFm.backendKey.value.replace("_CLUSTERNAME_", fm.clusterName.value);

  if ( fm.isAppend.checked ) {
    appendClusterName = fm.clusterName.value + "/" + Date.now();
  } else {
    appendClusterName = fm.clusterName.value;
  }

  tfFm.tfPath.value = tfFm.tfPath.value.replace("_CLUSTERNAME_", appendClusterName);
  tfFm.terraformBackendKey.value = tfFm.terraformBackendKey.value.replace("_CLUSTERNAME_", appendClusterName);

  nrm.vendor = fm.vendor[fm.vendor.selectedIndex].value;
  nrm.stack = fm.stack[fm.stack.selectedIndex].value;
  nrm.topology = fm.topology[fm.topology.selectedIndex].value;
  nrm.instanceType = instanceSpecInfo[0];
  nrm.coreSize = instanceSpecInfo[1];
  nrm.memSize = instanceSpecInfo[2];
  nrm.instanceSize = fm.instanceSize.value;
  nrm.volumeSize = fm.volumeSize.value;

  if ( instanceRequestModel.nodeSize > 0 ) {
    let isExist = false;

    for (let i=0; i<instanceRequestModel.nodeSize; i++ ) {
      if ( instanceRequestModel.instances[i].topology == nrm.topology ) {
        isExist = true;
        instanceRequestModel.instances[i] = nrm;
        break;
      }
    }

    if ( !isExist ) {
      instanceRequestModel.instances[instanceRequestModel.nodeSize] = nrm;
      instanceRequestModel.nodeSize++;
    }
  } else {
    instanceRequestModel.instances[instanceRequestModel.nodeSize] = nrm;
    instanceRequestModel.nodeSize++;
  }

  let template = "<td>_NODETYPE_</td><td>_INSTANCETYPE_</td><td>_INSTANCESIZE_</td><td>_VOLUMESIZE_ GiB</td>";

  template = template.replace(/_INSTANCETYPE_/gi, nrm.instanceType);
  template = template.replace(/_INSTANCESIZE_/gi, nrm.instanceSize);
  template = template.replace(/_VOLUMESIZE_/gi, nrm.volumeSize);

  switch ( nrm.topology ) {
    case "allinone" :
      template = template.replace(/_NODETYPE_/gi, "Master and Data");
      $('#allinoneNode').html(template);
      break;
    case "master" :
      template = template.replace(/_NODETYPE_/gi, "Master");
      $('#masterNode').html(template);
      break;
    case "data" :
      template = template.replace(/_NODETYPE_/gi, "Data");
      $('#dataNode').html(template);
      break;
    case "coordinator" :
      template = template.replace(/_NODETYPE_/gi, "Coordinator");
      $('#coordinatorNode').html(template);
      break;
  }
}

function registerTerraform() {
  const fm = document.terraformForm;
  let template = "<table class='table table-sm table-striped table-dark'><tr><td>_VPCSECURITYGROUPS_</td></tr><tr><td>_SUBNET_</td></tr>"+
   "<tr><td>_ZONE_</td></tr><tr><td>_KEYNAME_</td></tr>"+
   "<tr><td>_AMI_</td></tr><tr><td>_KEYPEM_</td></tr>"+
   "<tr><td>_ENV_</td></tr><tr><td>_TFPATH_</td></tr>"+
   "<tr><td>_BACKENDBUCKET_</td></tr><tr><td>_BACKENDKEY_</td></tr></table>";

  instanceRequestModel.terraformRequestModel.vpcSecurityGroupIds = fm.vpcSecurityGroupIds.value;
  instanceRequestModel.terraformRequestModel.subnetId = fm.subnetId.value;
  instanceRequestModel.terraformRequestModel.zoneId = fm.zoneId.value;
  instanceRequestModel.terraformRequestModel.keyName = fm.keyName.value;
  instanceRequestModel.terraformRequestModel.keyPem = fm.keyPem.value;
  instanceRequestModel.terraformRequestModel.amiId = fm.amiId.value;
  instanceRequestModel.terraformRequestModel.tfPath = fm.tfPath.value;
  instanceRequestModel.terraformRequestModel.env = fm.env.value;
  instanceRequestModel.terraformRequestModel.backendBucket = fm.terraformBackendBucket.value;
  instanceRequestModel.terraformRequestModel.backendKey = fm.terraformBackendKey.value;
  instanceRequestModel.terraformRequestModel.backend = fm.isTerraformBackend.checked;

  template = template.replace(/_VPCSECURITYGROUPS_/gi, instanceRequestModel.terraformRequestModel.vpcSecurityGroupIds);
  template = template.replace(/_SUBNET_/gi, instanceRequestModel.terraformRequestModel.subnetId);
  template = template.replace(/_ZONE_/gi, instanceRequestModel.terraformRequestModel.zoneId);
  template = template.replace(/_KEYNAME_/gi, instanceRequestModel.terraformRequestModel.keyName);
  template = template.replace(/_AMI_/gi, instanceRequestModel.terraformRequestModel.amiId);
  template = template.replace(/_KEYPEM_/gi, instanceRequestModel.terraformRequestModel.keyPem);
  template = template.replace(/_ENV_/gi, instanceRequestModel.terraformRequestModel.env);
  template = template.replace(/_TFPATH_/gi, instanceRequestModel.terraformRequestModel.tfPath);
  template = template.replace(/_BACKENDBUCKET_/gi, instanceRequestModel.terraformRequestModel.backendBucket);
  template = template.replace(/_BACKENDKEY_/gi, instanceRequestModel.terraformRequestModel.backendKey);

  $('#terraformConfiguration').html(template);
}

function createInstances() {
  spinner_show();

  $.ajax({
    url: "/api/aws/terraform/create-instances-for-elasticsearch",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
      "terraformRequestModel": instanceRequestModel.terraformRequestModel,
      "nodeSize": instanceRequestModel.nodeSize,
      "instances": instanceRequestModel.instances
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);

      spinner_hide();
      alert("생성 되었습니다.\nIP 정보를 추출 합니다.");
      pullTerraformState();
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);

      spinner_hide();
      alert("오류가 발생했습니다.");
    }
  });
}

function destroyInstances() {
  spinner_show();

  $.ajax({
    url: "/api/aws/terraform/destroy-instances-for-elasticsearch",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
      "terraformRequestModel": instanceRequestModel.terraformRequestModel
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      spinner_hide();
      alert("삭제 되었습니다.");
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      spinner_hide();
      alert("오류가 발생했습니다.");
    }
  });
}

function pullTerraformState() {
  spinner_show();

  $.ajax({
    url: "/api/aws/terraform/pull-terraform-state",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
      "terraformRequestModel": instanceRequestModel.terraformRequestModel,
      "nodeSize": instanceRequestModel.nodeSize,
      "instances": instanceRequestModel.instances
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      spinner_hide();
      console.log(res);
      extractInstancesPrivateIp(res);
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      spinner_hide();
      console.log(res);
      alert("오류가 발생했습니다.");
    }
  });
}

function extractInstancesPrivateIp(res) {
  const fm = document.elasticsearchForm;

  try {
    res = JSON.parse(res);

    if ( res.outputs.allinone_private_ip === undefined ) {
      throw new Error("Node Topology Undefined!!");
    }

    fm.allinoneIps.value = extractInstancePrivateIp(res.outputs.allinone_private_ip.value);
    setupInstancePrivateIp("allinone", fm.allinoneIps.value);

    fm.masterIps.value = extractInstancePrivateIp(res.outputs.master_private_ip.value);
    setupInstancePrivateIp("master", fm.masterIps.value);

    fm.dataIps.value = extractInstancePrivateIp(res.outputs.data_private_ip.value);
    setupInstancePrivateIp("data", fm.dataIps.value);

    fm.coordinatorIps.value = extractInstancePrivateIp(res.outputs.coordinator_private_ip.value);
    setupInstancePrivateIp("coordinator", fm.coordinatorIps.value);

  } catch (e) {
    setConsoleLog("ecosConsoleLog", e);
    console.log(e);
  }
}

function extractClusterMasterIp(res) {
  const fm = document.elasticsearchForm;
  let masterIp = "";

  try {
    res = JSON.parse(res);

    if ( res.outputs.allinone_private_ip === undefined ) {
      throw new Error("Node Topology Undefined!!");
    }

    masterIp = extractInstancePrivateIp(res.outputs.master_private_ip.value);
  } catch (e) {
    setConsoleLog("ecosConsoleLog", e);
    console.log(e);
  }

  return masterIp;
}

function extractInstancePrivateIp(ips) {
  let tempIps = "";

  for (let i=0; i<ips.length; i++ ) {
    tempIps += ips[i] + ",";
  }

  console.log(tempIps);
  console.log(tempIps.substring(0, tempIps.length - 1));

  return tempIps.substring(0, tempIps.length - 1);
}

function setupInstancePrivateIp(topology, ips) {
  if ( $.trim(ips) != "" ) {
    for ( i=0; i<instanceRequestModel.nodeSize; i++ ) {
      if ( instanceRequestModel.instances[i].topology == topology ) {
        instanceRequestModel.instances[i].privateIps = ips.split(",");
        console.log(instanceRequestModel.instances[i].privateIps);
        break;
      }
    }
  }
}

function setupNodeConfiguration() {
  const fm = document.elasticsearchForm;
  let pathData = "PathData";
  let pathLogs = "PathLogs";
  let httpPort = "HttpPort";
  let tcpPort = "TcpPort";
  let temp = "";

  for ( let i=0; i<instanceRequestModel.nodeSize; i++ ) {
    temp = instanceRequestModel.instances[i].topology + pathData
    console.log(fm[temp].value);
    instanceRequestModel.instances[i].pathData = fm[temp].value;

    temp = instanceRequestModel.instances[i].topology + pathLogs
    console.log(fm[temp].value);
    instanceRequestModel.instances[i].pathLogs = fm[temp].value;

    temp = instanceRequestModel.instances[i].topology + httpPort
    console.log(fm[temp].value);
    instanceRequestModel.instances[i].httpPort = fm[temp].value;

    temp = instanceRequestModel.instances[i].topology + tcpPort
    console.log(fm[temp].value);
    instanceRequestModel.instances[i].tcpPort = fm[temp].value;
  }
}

function createElasticsearchCluster() {
  const fm = document.elasticsearchForm;
  const infFm = document.infrastructureForm;

  instanceRequestModel.elasticsearchClusterRequestModel.clusterName = infFm.clusterName.value;
  instanceRequestModel.elasticsearchClusterRequestModel.esVersion = fm.esVersion[fm.esVersion.selectedIndex].value;
  instanceRequestModel.workingPath = fm.workingPath.value;
  instanceRequestModel.bastionIp = fm.bastionIp.value;
  setupNodeConfiguration();

  spinner_show();

  $.ajax({
    url: "/api/aws/elasticsearch/create-cluster-by-ansible",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
      "terraformRequestModel": instanceRequestModel.terraformRequestModel,
      "elasticsearchClusterRequestModel": instanceRequestModel.elasticsearchClusterRequestModel,
      "nodeSize": instanceRequestModel.nodeSize,
      "instances": instanceRequestModel.instances,
      "workingPath": instanceRequestModel.workingPath,
      "bastionIp": instanceRequestModel.bastionIp,
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("생성 되었습니다.");
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("오류가 발생했습니다.");
    }
  });
}

function appendElasticsearchCluster() {
  fetchTerraformState();

  const fm = document.elasticsearchForm
  const infFm = document.infrastructureForm;

  instanceRequestModel.elasticsearchClusterRequestModel.clusterName = infFm.clusterName.value;
  instanceRequestModel.elasticsearchClusterRequestModel.esVersion = fm.esVersion[fm.esVersion.selectedIndex].value;
  instanceRequestModel.elasticsearchClusterRequestModel.backendBucket = fm.backendBucket.value;
  instanceRequestModel.elasticsearchClusterRequestModel.backendKey = fm.backendKey.value;
  instanceRequestModel.elasticsearchClusterRequestModel.seedHosts = fm.seedHosts.value;
  instanceRequestModel.workingPath = fm.workingPath.value;
  instanceRequestModel.bastionIp = fm.bastionIp.value;

  setupNodeConfiguration();

  spinner_show();

  $.ajax({
    url: "/api/aws/elasticsearch/append-cluster-by-ansible",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
      "terraformRequestModel": instanceRequestModel.terraformRequestModel,
      "elasticsearchClusterRequestModel": instanceRequestModel.elasticsearchClusterRequestModel,
      "nodeSize": instanceRequestModel.nodeSize,
      "instances": instanceRequestModel.instances,
      "workingPath": instanceRequestModel.workingPath,
      "bastionIp": instanceRequestModel.bastionIp,
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("생성 되었습니다.");
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("오류가 발생했습니다.");
    }
  });
}

function fetchTerraformState() {
  const fm = document.elasticsearchForm;
  let backendBucket = fm.backendBucket.value;
  let backendKey = fm.backendKey.value;

  spinner_show();

  $.ajax({
    url: "/api/aws/terraform/fetch-terraform-state",
    type: "POST",
    dataType: "json",
    async: false,
    contentType: 'application/json',
    data: JSON.stringify({
      "backendBucket": backendBucket,
      "backendKey": backendKey,
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      spinner_hide();
      console.log(res);
      fm.seedHosts.value = extractClusterMasterIp(res);
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      spinner_hide();
      console.log(res);
      alert("오류가 발생했습니다.");
    }
  });
}

function startClusterByAnsible() {
  spinner_show();

  $.ajax({
    url: "/api/aws/elasticsearch/start-cluster-by-ansible",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
      "terraformRequestModel": instanceRequestModel.terraformRequestModel,
      "elasticsearchClusterRequestModel": instanceRequestModel.elasticsearchClusterRequestModel,
      "nodeSize": instanceRequestModel.nodeSize,
      "instances": instanceRequestModel.instances,
      "workingPath": instanceRequestModel.workingPath,
      "bastionIp": instanceRequestModel.bastionIp,
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("시작 되었습니다.");
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("오류가 발생했습니다.");
    }
  });
}

function stopClusterByAnsible() {
  spinner_show();

  $.ajax({
    url: "/api/aws/elasticsearch/stop-cluster-by-ansible",
    type: "POST",
    dataType: "json",
    contentType: 'application/json',
    data: JSON.stringify({
      "terraformRequestModel": instanceRequestModel.terraformRequestModel,
      "elasticsearchClusterRequestModel": instanceRequestModel.elasticsearchClusterRequestModel,
      "nodeSize": instanceRequestModel.nodeSize,
      "instances": instanceRequestModel.instances,
      "workingPath": instanceRequestModel.workingPath,
      "bastionIp": instanceRequestModel.bastionIp,
    }),
    success: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("중지 되었습니다.");
    },
    error: function (res) {
      setConsoleLog("ecosConsoleLog", res);
      console.log(res);
      spinner_hide();
      alert("오류가 발생했습니다.");
    }
  });
}
provider "aws" {
  region = "ap-northeast-2"
  profile = "ecos"
}

variable "vpc" {
  default = [_VPCSECURITYGROUPIDS_]
  description = ""
}

variable "subnet" {
  default = "_SUBNETID_"
  description = ""
}

variable "zone" {
  default = "_ZONEID_"
  description = ""
}

variable "key" {
  default = "_KEYNAME_"
  description = ""
}

resource "aws_instance" "allinone" {
  ami           			= "_AMIID_"
  availability_zone 		= var.zone
  subnet_id 				= var.subnet
  key_name 				= var.key
  vpc_security_group_ids 	= var.vpc

  associate_public_ip_address = false
  instance_type = "_ALLINONEINSTANCETYPE_"
  count = _ALLINONEINSTANCESIZE_

  tags = {
    Name = "elasticsearch_allinone"
    Environment = "_ENV_"
  }

  root_block_device {
    delete_on_termination = true
    volume_size = _ALLINONEVOLUMESIZE_
    volume_type = "gp2"
  }

  connection {
    host		= self.private_ip
    user        = "ubuntu"
    type        = "ssh"
    private_key = file("~/.ssh/_KEYPEM_")
    timeout     = "10m"
  }
}

resource "aws_instance" "master" {
  ami           			= "_AMIID_"
  availability_zone 		= var.zone
  subnet_id 				= var.subnet
  key_name 				= var.key
  vpc_security_group_ids 	= var.vpc

  associate_public_ip_address = false
  instance_type = "_MASTERINSTANCETYPE_"
  count = _MASTERINSTANCESIZE_

  tags = {
    Name = "elasticsearch_master"
    Environment = "_ENV_"
  }

  root_block_device {
    delete_on_termination = true
    volume_size = _MASTERVOLUMESIZE_
    volume_type = "gp2"
  }

  connection {
    host		= self.private_ip
    user        = "ubuntu"
    type        = "ssh"
    private_key = file("~/.ssh/_KEYPEM_")
    timeout     = "10m"
  }
}

resource "aws_instance" "data" {
  ami           			= "_AMIID_"
  availability_zone 		= var.zone
  subnet_id 				= var.subnet
  key_name 				= var.key
  vpc_security_group_ids 	= var.vpc

  associate_public_ip_address = false
  instance_type = "_DATAINSTANCETYPE_"
  count = _DATAINSTANCESIZE_

  tags = {
    Name = "elasticsearch_data"
    Environment = "_ENV_"
  }

  root_block_device {
    delete_on_termination = true
    volume_size = _DATAVOLUMESIZE_
    volume_type = "gp2"
  }

  connection {
    host		= self.private_ip
    user        = "ubuntu"
    type        = "ssh"
    private_key = file("~/.ssh/_KEYPEM_")
    timeout     = "10m"
  }
}

resource "aws_instance" "coordinator" {
  ami           			= "_AMIID_"
  availability_zone 		= var.zone
  subnet_id 				= var.subnet
  key_name 				= var.key
  vpc_security_group_ids 	= var.vpc

  associate_public_ip_address = false
  instance_type = "_COORDINATORINSTANCETYPE_"
  count = _COORDINATORINSTANCESIZE_

  tags = {
    Name = "elasticsearch_coordinator"
    Environment = "_ENV_"
  }

  root_block_device {
    delete_on_termination = true
    volume_size = _COORDINATORVOLUMESIZE_
    volume_type = "gp2"
  }

  connection {
    host		= self.private_ip
    user        = "ubuntu"
    type        = "ssh"
    private_key = file("~/.ssh/_KEYPEM_")
    timeout     = "10m"
  }
}

output "allinone_private_ip" {
  value = "${aws_instance.allinone.*.private_ip}"
}

output "master_private_ip" {
  value = "${aws_instance.master.*.private_ip}"
}

output "data_private_ip" {
  value = "${aws_instance.data.*.private_ip}"
}

output "coordinator_private_ip" {
  value = "${aws_instance.coordinator.*.private_ip}"
}

terraform {
  backend "s3" {
    bucket = "_BUCKET_"
    key    = "_KEY_"
    region = "ap-northeast-2"
  }
}
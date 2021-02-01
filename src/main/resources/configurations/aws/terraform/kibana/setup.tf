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

resource "aws_instance" "kibana" {
  ami           			= "_AMIID_"
  availability_zone 		= var.zone
  subnet_id 				= var.subnet
  key_name 				= var.key
  vpc_security_group_ids 	= var.vpc

  associate_public_ip_address = false
  instance_type = "_KIBANAINSTANCETYPE_"
  count = _KIBANAINSTANCESIZE_

  tags = {
    Name = "kibana"
    Environment = "_ENV_"
  }

  root_block_device {
    delete_on_termination = true
    volume_size = _KIBANAVOLUMESIZE_
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

output "kibana_private_ip" {
  value = "${aws_instance.kibana.*.private_ip}"
}

terraform {
  backend "s3" {
    bucket = "_BUCKET_"
    key    = "_KEY_"
    region = "ap-northeast-2"
  }
}
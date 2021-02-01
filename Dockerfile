FROM openjdk:8-jdk-alpine
LABEL maintainer="MegazoneCloud"

ARG BASTION_IP

ENV serverPort 8081
ENV env dev
ENV redirecthttps true
ENV configWorkingPath /tmp/home/mzc/app
ENV configTerraformAwsBastionIp $BASTION_IP
ENV configTerraformAwsSecurityGroup sg-xxxxxxxxxxxxxxxx
ENV configTerraformAwsAz ap-northeast-2a
ENV configTerraformAwsAmi ami-061b0ee20654981ab
ENV configTerraformAwsSubnet subnet-xxxxxxxxxxxxxxxx
ENV configTerraformAwsKeyName ec2key-gw
ENV configTerraformAwsPemFile ec2key-gw.pem
ENV configTerraformAwsPathElasticsearch /tmp/home/mzc/app/terraform/_CLUSTERNAME_
ENV configTerraformAwsPathKibana /tmp/home/mzc/app/terraform/_CLUSTERNAME_/kibana
ENV configTerraformAwsBackendBucket megatoi-terraform-state
ENV configTerraformAwsBackendKeyElasticsearch _CLUSTERNAME_/terraform.tfstate
ENV configTerraformAwsBackendKeyKibana _CLUSTERNAME_/kibana/terraform.tfstate

# Timezone 보정
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime
RUN echo "Asia/Seoul" > /etc/timezone
RUN apk add curl

RUN apk --no-cache update
RUN apk --no-cache add python3
RUN pip3 --no-cache-dir install awscli

RUN wget https://releases.hashicorp.com/terraform/0.14.5/terraform_0.14.5_linux_amd64.zip
RUN unzip terraform_0.14.5_linux_amd64.zip && rm terraform_0.14.5_linux_amd64.zip
RUN mv terraform /usr/bin/terraform

RUN apk add ansible

#
ARG JAR_FILE=./build/libs/ecos-installer-web-0.0.1.jar
ADD ${JAR_FILE} /app.jar

ENTRYPOINT java \
#-javaagent:/elastic-apm-agent-1.17.0.jar \
#-Delastic.apm.recording="${apmuse}" \
#-Delastic.apm.service_name=MegaToI-BackOffice-Web \
#-Delastic.apm.server_urls="${apmurls}" \
#-Delastic.apm.application_packages=com.mzc.megatoi.backoffice.web \
-Djava.security.egd=file:/dev/./urandom \
-Dserver.port="${serverPort}" \
-Dspring.profiles.active="${env}" \
-Dserver.redirect-https="${redirecthttps}" \
-Dconfig.working-path="${configWorkingPath}" \
-Dconfig.terraform.aws.bastion-ip="${configTerraformAwsBastionIp}" \
-Dconfig.terraform.aws.security-group="${configTerraformAwsSecurityGroup}" \
-Dconfig.terraform.aws.az="${configTerraformAwsAz}" \
-Dconfig.terraform.aws.ami="${configTerraformAwsAmi}" \
-Dconfig.terraform.aws.subnet="${configTerraformAwsSubnet}" \
-Dconfig.terraform.aws.KeyName="${configTerraformAwsKeyName}" \
-Dconfig.terraform.aws.PemFile="${configTerraformAwsPemFile}" \
-Dconfig.terraform.aws.path.elasticsearch="${configTerraformAwsPathElasticsearch}" \
-Dconfig.terraform.aws.path.kibana="${configTerraformAwsPathKibana}" \
-Dconfig.terraform.aws.backend-bucket="${configTerraformAwsBackendBucket}" \
-Dconfig.terraform.aws.backend-key.elasticsearch="${configTerraformAwsBackendKeyElasticsearch}" \
-Dconfig.terraform.aws.backend-key.kibana="${configTerraformAwsBackendKeyKibana}" \
-jar /app.jar

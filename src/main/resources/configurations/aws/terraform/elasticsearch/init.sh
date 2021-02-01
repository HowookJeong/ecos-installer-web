#!/bin/bash

echo "en_US.UTF-8 UTF-8" | sudo tee -a /etc/locale.gen > /dev/null
sudo locale-gen
sudo update-locale LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8

export LANG=en_US.UTF-8
export LANGUAGE=en_US:en
export LC_ALL=en_US.UTF-8

export TZ=Asia/Seoul

echo $TZ | sudo tee -a /etc/timezone > /dev/null
sudo rm /etc/localtime
sudo ln -snf /usr/share/zoneinfo/$TZ /etc/localtime
sudo dpkg-reconfigure -f noninteractive tzdata

sudo useradd -ms /bin/bash -u 1002 mzc
sudo mkdir /home/mzc/.ssh
sudo chmod 700 /home/mzc/.ssh
sudo cp ~/.ssh/authorized_keys /home/mzc/.ssh/authorized_keys
sudo chown mzc.mzc /home/mzc -Ry

echo 'mzc ALL=(ALL) NOPASSWD:ALL' | sudo tee -a /etc/sudoers > /dev/null

sudo mkdir -p /home/mzc/apps/elastic/elasticsearch/data
sudo mkdir -p /home/mzc/apps/elastic/elasticsearch/logs
sudo mkdir -p /home/mzc/logs
sudo mkdir -p /home/mzc/config
sudo mkdir -p /home/mzc/bin
sudo mkdir -p /home/mzc/build
sudo mkdir -p /home/mzc/backup

sudo chown mzc.mzc /home/mzc -R

sudo usermod -aG docker mzc
sudo usermod -aG docker ubuntu

sudo systemctl enable docker

sudo sysctl -w vm.max_map_count=262144
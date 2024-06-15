#!/bin/bash

set -x #echo on

cd ~/projects/rabbitmq/tanzu-rabbitmq-event-streaming-showcase/


# Set GemFire Pre-Requisite

kubectl create namespace cert-manager

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.7.1/cert-manager.yaml

helm repo add jetstack https://charts.jetstack.io

helm repo update

helm install cert-manager jetstack/cert-manager --namespace cert-manager  --version v1.0.2 --set installCRDs=true

kubectl create secret docker-registry image-pull-secret --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD



## RabbitMQ  Operator

kubectl apply -f "https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"



## RabbitMQ Install 1 Node cluster

#kubectl apply -f cloud/k8/data-services/rabbitmq/rabbitmq-1-node.yml

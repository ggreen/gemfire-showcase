#!/bin/bash

set -x #echo on

kind delete cluster

cd ~/projects/rabbitmq/tanzu-rabbitmq-event-streaming-showcase/
git pull

cd ~
kind create cluster  --config k8-1wnode.yaml

# Set GemFire Pre-Requisite

kubectl create namespace cert-manager

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.7.1/cert-manager.yaml

helm repo add jetstack https://charts.jetstack.io

helm repo update

helm install cert-manager jetstack/cert-manager --namespace cert-manager  --version v1.0.2 --set installCRDs=true

kubectl create namespace gemfire-system


kubectl create secret docker-registry image-pull-secret --namespace=gemfire-system --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD

kubectl create secret docker-registry image-pull-secret --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD

kubectl create rolebinding psp-gemfire --clusterrole=psp:vmware-system-privileged --serviceaccount=gemfire-system:default


# Install the GemFire Operator
sleep 40
helm install gemfire-operator ~/gemfire-operator-1.0.3.tgz --namespace gemfire-system

sleep 30s
cd ~/projects/rabbitmq/tanzu-rabbitmq-event-streaming-showcase/
kubectl apply -f cloud/k8/data-services/geode/gemfire.yml


# Install Postgres
cd ~/dataServices/postgres/postgres-for-kubernetes-v1.2.0
docker load -i ./images/postgres-instance
docker load -i ./images/postgres-operator
docker images "postgres-*"
export HELM_EXPERIMENTAL_OCI=1
helm registry login registry.pivotal.io \
--username=$HARBOR_USER --password=$HARBOR_PASSWORD

helm chart pull registry.pivotal.io/tanzu-sql-postgres/postgres-operator-chart:v1.2.0
helm chart export registry.pivotal.io/tanzu-sql-postgres/postgres-operator-chart:v1.2.0  --destination=/tmp/

kubectl create secret docker-registry regsecret \
--docker-server=https://registry.pivotal.io --docker-username=$HARBOR_USER \
--docker-password=$HARBOR_PASSWORD

helm install --wait postgres-operator /tmp/postgres-operator/
sleep 30s

cd ~/projects/rabbitmq/tanzu-rabbitmq-event-streaming-showcase/
kubectl apply -f cloud/k8/data-services/postgres
sleep 2m
git

## SCDF
cd ~/dataServices/scdf
wget https://github.com/mikefarah/yq/releases/download/v4.12.1/yq_linux_386.tar.gz -O - |\
tar xz && sudo mv yq_linux_386 /usr/bin/yq

curl -OL https://github.com/vmware-tanzu/carvel-kbld/releases/download/v0.30.0/kbld-linux-amd64
sudo mv kbld-linux-amd64 /usr/bin/kbld
sudo chmod +x /usr/bin/kbld

curl -OL https://github.com/vmware-tanzu/carvel-kapp/releases/download/v0.39.0/kapp-linux-amd64
sudo mv kapp-linux-amd64 /usr/bin/kapp


kubectl create secret docker-registry scdf-image-regcred --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.3/install.sh | bash -s v0.18.3
kubectl create -f https://operatorhub.io/install/prometheus.yaml
kubectl get csv -n operators
cd ~/dataServices/scdf
kubectl apply -f ./services/dev/monitoring


kubectl apply -f ./services/dev/postgresql/secret.yaml
kubectl apply -f ./services/dev/rabbitmq/config.yaml
kubectl apply -f ./services/dev/rabbitmq/secret.yaml

kubectl apply -f ./services/dev/skipper.yaml
kubectl wait pod -l=app=skipper --for=condition=Ready

sleep 1m

kubectl apply -f ./services/dev/data-flow.yaml
sleep 2m
kubectl apply -f ./services/dev/monitoring-proxy

# Create GemFire Cluster
cd ~/projects/rabbitmq/tanzu-rabbitmq-event-streaming-showcase

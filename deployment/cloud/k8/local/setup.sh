#!/bin/bash


STR=`pwd`
if [[ "$STR" == *"cloud"* ]]; then
  echo "You must execute this script from the root project directory"
  exit 0
fi




#PRE_REQUISUITE
if ! command -v yq &> /dev/null
then
    echo "Please install yq See https://github.com/mikefarah/yq/releases/tag/3.3.0"
    exit
fi


if ! command -v kbld &> /dev/null
then
    echo "Please install kbld See https://github.com/vmware-tanzu/carvel-kbld/releases/download/v0.30.0"
    exit
fi

if ! command -v kapp &> /dev/null
then
    echo "Please install kapp See https://github.com/vmware-tanzu/carvel-kapp/releases/download/v0.39.0"
    exit
fi


if [ -z $POSTGRES_OPERATOR_DOWNLOAD_DIR ]
then
  echo "Please set \POSTGRES_OPERATOR_DOWNLOAD_DIR to the location where your download postgres operator See http://network.pivotal.io"
  exit
fi

if [ -z $GEMFIRE_OPERATOR_DOWNLOAD_DIR ]
then
  echo "Please set \$GEMFIRE_OPERATOR_DOWNLOAD_DIR to the location where your download gemfire-operator-1.0.1.tgz. See http://network.pivotal.io"
  exit
fi


if [ -z $HARBOR_USER ]
then
  echo "Please set the your username and password used to login into registry.pivotal.io .profile on on the shell. Example: export \$HARBOR_USER=MYUSER; export=\$HARBOR_PASSWORD==MYPASSWORD "
  exit
fi

if [ -z $HARBOR_PASSWORD ]
then
  echo "Please set the your username and password used to login into registry.pivotal.io in .profile on on the shell. Example: export \$HARBOR_USER=MYUSER; export=\$HARBOR_PASSWORD==MYPASSWORD "
  k get p
fi

set -x #echo on


kind delete cluster
kind create cluster  --config cloud/k8/local/k8-1wnode.yaml

# Set GemFire Pre-Requisite

kubectl create namespace cert-manager

helm repo add jetstack https://charts.jetstack.io

helm repo update

helm install cert-manager jetstack/cert-manager --namespace cert-manager  --version v1.0.2 --set installCRDs=true

kubectl create namespace gemfire-system


kubectl create secret docker-registry image-pull-secret --namespace=gemfire-system --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD

kubectl create secret docker-registry image-pull-secret --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD

kubectl create rolebinding psp-gemfire --clusterrole=psp:vmware-system-privileged --serviceaccount=gemfire-system:default


# Install the GemFire Operator
sleep 40
helm install gemfire-operator $GEMFIRE_OPERATOR_DOWNLOAD_DIR/gemfire-operator-1.0.1.tgz --namespace gemfire-system

sleep 30s
kubectl apply -f cloud/k8/data-services/geode/gemfire.yml


# Install Postgres
docker load -i $POSTGRES_OPERATOR_DOWNLOAD_DIR/images/postgres-instance
docker load -i $POSTGRES_OPERATOR_DOWNLOAD_DIR/images/postgres-operator
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

kubectl apply -f cloud/k8/data-services/postgres
sleep 2m
git

## SCDF



kubectl create secret docker-registry scdf-image-regcred --docker-server=registry.pivotal.io --docker-username=$HARBOR_USER --docker-password=$HARBOR_PASSWORD
curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.18.3/install.sh | bash -s v0.18.3
kubectl create -f https://operatorhub.io/install/prometheus.yaml
kubectl get csv -n operators

kubectl apply -f cloud/k8/data-services/scdf/services/dev/monitoring


kubectl apply -f cloud/k8/data-services/scdf/services/dev/postgresql/secret.yaml
kubectl apply -f cloud/k8/data-services/scdf/services/dev/rabbitmq/config.yaml
kubectl apply -f cloud/k8/data-services/scdf/services/dev/rabbitmq/secret.yaml

kubectl apply -f cloud/k8/data-services/scdf/services/dev/skipper.yaml
kubectl wait pod -l=app=skipper --for=condition=Ready

sleep 1m

kubectl apply -f cloud/k8/data-services/scdf/services/dev/data-flow.yaml
sleep 2m
kubectl apply -f cloud/k8/data-services/scdf/services/dev/monitoring-proxy

# Create GemFire Cluster

# Postgres

kubectl exec -it postgres-0 -- psql -c "ALTER USER postgres PASSWORD 'CHANGEME'"

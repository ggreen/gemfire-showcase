#!/bin/bash

export GEMFIRE_K8_VERSION=2.6.4

set echo off
#set -x #echo on


kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.14.4/cert-manager.yaml
kubectl get pods --namespace cert-manager

# wait for CRD manager
sleep 5
kubectl wait pod -l=app.kubernetes.io/component=webhook --for=condition=Ready --timeout=360s --namespace=cert-manager


# Set GemFire Pre-Requisite

kubectl create namespace gemfire-system
kubectl create namespace tanzu-data

kubectl create secret docker-registry image-pull-secret --namespace=gemfire-system --docker-server=gemfire-registry.packages.broadcom.com --docker-username=$BROADCOM_USERNAME --docker-password=$BROADCOM_GEMFIRE_PASSWORD

kubectl create secret docker-registry image-pull-secret --namespace=tanzu-data --docker-server=gemfire-registry.packages.broadcom.com --docker-username=$BROADCOM_USERNAME --docker-password=$BROADCOM_GEMFIRE_PASSWORD


kubectl create rolebinding psp-gemfire --namespace=gemfire-system --clusterrole=psp:vmware-system-privileged --serviceaccount=gemfire-system:default


echo  Login
helm registry login -u  $BROADCOM_USERNAME -p $BROADCOM_GEMFIRE_PASSWORD  gemfire-registry.packages.broadcom.com

#kubectl create secret docker-registry image-pull-secret  --docker-server=registry.packages.broadcom.com --docker-username=$BROADCOM_USERNAME --docker-password=$BROADCOM_GEMFIRE_PASSWORD --namespace=tanzu-data

#kubectl create secret docker-registry image-pull-secret  --docker-server=registry.packages.broadcom.com --docker-username=$BROADCOM_USERNAME --docker-password=$BROADCOM_GEMFIRE_PASSWORD --namespace=gemfire-system

# Install the GemFire Operator
helm install gemfire-crd oci://gemfire-registry.packages.broadcom.com/tanzu-gemfire-for-kubernetes/gemfire-crd --version $GEMFIRE_K8_VERSION --namespace gemfire-system --set operatorReleaseName=gemfire-operator
helm install gemfire-operator oci://gemfire-registry.packages.broadcom.com/tanzu-gemfire-for-kubernetes/gemfire-operator --version $GEMFIRE_K8_VERSION --namespace gemfire-system


sleep 5
kubectl wait pod -l=app.kubernetes.io/component=gemfire-controller-manager --for=condition=Ready --timeout=360s --namespace=gemfire-system



kubectl get pods --namespace gemfire-system

kubectl apply -f https://projectcontour.io/quickstart/contour-gateway-provisioner.yaml

kubectl --namespace projectcontour get deployments

kubectl config set-context --current --namespace=tanzu-data
kubectl create namespace gateway-system

sleep 5
kubectl apply -f deployment/cloud/k8/data-services/gemfire/gf-gateway.yml --namespace=gateway-system
kubectl get pods -n gateway-system

kubectl apply -f deployment/cloud/k8/data-services/gemfire/gf-load-balancer.yml

kubectl get services -n kube-system

#kubectl apply -f deployment/cloud/k8/data-services/gemfire/gf-load-balance-config-map.yml
#kubectl delete pods -l k8s-app=kube-dns --namespace kube-system
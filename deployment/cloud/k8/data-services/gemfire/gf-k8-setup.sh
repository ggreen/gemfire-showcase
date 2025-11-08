#!/bin/bash

set echo off
#set -x #echo on

# Set GemFire Pre-Requisite

kubectl create namespace cert-manager

helm repo add jetstack https://charts.jetstack.io

helm repo update


#helm install cert-manager oci://registry-1.docker.io/bitnamicharts/cert-manager --namespace cert-manager  --create-namespace

kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.15.0/cert-manager.yaml

kubectl get pods --namespace cert-manager
#kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.10.0/cert-manager.yaml

# wait for CRD manager
sleep 5
kubectl wait pod -l=app.kubernetes.io/component=webhook --for=condition=Ready --timeout=160s --namespace=cert-manager


kubectl create namespace gemfire-system
kubectl create namespace tanzu-data

helm registry login -u  $BROADCOM_USERNAME -p $BROADCOM_GEMFIRE_PASSWORD  registry.packages.broadcom.com

kubectl create rolebinding psp-gemfire --namespace=gemfire-system --clusterrole=psp:vmware-system-privileged --serviceaccount=gemfire-system:default

# Install the GemFire Operator

helm install gemfire-crd oci://registry.packages.broadcom.com/tanzu-gemfire-for-kubernetes/gemfire-crd --version 2.5.0 --namespace gemfire-system --set operatorReleaseName=gemfire-operator
helm install gemfire-operator oci://registry.packages.broadcom.com/tanzu-gemfire-for-kubernetes/gemfire-operator --version 2.5.0 --namespace gemfire-system

sleep 5
kubectl wait pod -l=app.kubernetes.io/component=gemfire-controller-manager --for=condition=Ready --timeout=160s --namespace=gemfire-system



kubectl get pods --namespace gemfire-system

kubectl apply -f https://projectcontour.io/quickstart/contour-gateway-provisioner.yaml

kubectl --namespace projectcontour get deployments

kubectl config set-context --current --namespace=tanzu-data
kubectl create namespace gateway-system
kubectl apply -f deployment/cloud/k8/data-services/gemfire/gf-gateway.yml --namespace=gateway-system
kubectl get pods -n gateway-system

kubectl apply -f deployment/cloud/k8/data-services/gemfire/gf-load-balancer.yml

kubectl get services -n kube-system

#kubectl apply -f deployment/cloud/k8/data-services/gemfire/gf-load-balance-config-map.yml
#kubectl delete pods -l k8s-app=kube-dns --namespace kube-system
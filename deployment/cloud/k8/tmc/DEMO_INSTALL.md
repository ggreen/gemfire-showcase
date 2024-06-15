# Access

## TMC

```text
https://sgpoc.tmc.cloud.vmware.com/clusters
```

Click Actions -> access this cluster


## Harbor

```text
https://harbor.shared-services.sg.kirkware.com/
```

admin/VMware1! if you want to log in
gregoryg/VMware1!

VMware1!


export PRIVATE_CONTAINER_REPO=harbor.shared-services.sg.kirkware.com
docker login $PRIVATE_CONTAINER_REPO -u admin

# Carvel Install


```shell
curl -L https://carvel.dev/install.sh | bash
```



# Cert manager


```shell

k get pod -A | grep secret
k get pod -A | grep kapp
k get pods -A | grep cert-manager

```


```shell
tanzu package install cert-manager --package-name cert-manager.tanzu.vmware.com --namespace cert-manager  --version 1.5.3+vmware.2-tkg.1  --create-namespace
```

Check


Check for these already installed kapp-controller v0.28.0+  secretgen-controller v0.5.0+


# Setup Namespace


```shell
kubectl create -f cloud/k8/tmc/data-services/rabbitmq/rabbitmq-poc-namespace.yml
```



```shell
kubectl config set-context rabbitmq-poc --namespace=rabbitmq-poc \
  --cluster=lob-1-1 \
  --user=lob-1-1
```


```shell
kubectl create secret docker-registry reg-creds -n rabbitmq-poc --docker-server "registry.tanzu.vmware.com" --docker-username $HARBOR_USER --docker-password $HARBOR_PASSWORD
```

```shell
kubectl create secret docker-registry tanzu-rabbitmq-registry-creds -n rabbitmq-system --docker-server "registry.tanzu.vmware.com" --docker-username $HARBOR_USER --docker-password $HARBOR_PASSWORD
```

```shell
kubectl create secret docker-registry tanzu-rabbitmq-registry-creds -n rabbitmq-poc  --docker-server "registry.tanzu.vmware.com" --docker-username $HARBOR_USER --docker-password $HARBOR_PASSWORD
```

```shell
kubectl apply -f cloud/k8/tmc/data-services/rabbitmq/secretExport.yml
```


```shell
k apply -f cloud/k8/tmc/data-services/rabbitmq/roles/serviceAccount.yml
```

```shell
kubectl create clusterrolebinding tanzu-rabbitmq-crd-install \
--clusterrole=tanzu-rabbitmq-crd-install \
--serviceaccount=rabbitmq-poc:rabbitmq-lob-1-1-server \
--namespace=rabbitmq-poc
```


```shell
kapp deploy -a tanzu-rabbitmq-repo -f cloud/k8/tmc/data-services/rabbitmq/repo.yml -y
```


Verify

```shell
kubectl get packages | grep rabbit
```



```shell
kubectl create -f cloud/k8/tmc/data-services/rabbitmq/rabbitmq-poc-namespace.yml
```
```shell
kubectl config set-context rabbitmq-poc --namespace=rabbitmq-poc \
  --cluster=lob-1-1 \
  --user=lob-1-1
```

To view the new contexts:
```shell
kubectl config view
```
Switch Context

```shell
kubectl config use-context rabbitmq-poc
```



```shell
kubectl -n rabbitmq-poc create secret \
docker-registry rabbitmq \
--docker-server=registry.pivotal.io \
--docker-username=$HARBOR_USER \
--docker-password=$HARBOR_PASSWORD
```


-------------------

```shell
kubectl get customresourcedefinitions.apiextensions.k8s.io | grep  rabbitmq
```

Assuming RabbitmqCluster name is 'mycluster'


```shell
k apply -f cloud/k8/tmc/data-services/rabbitmq/roles/role.yml
```


```shell  REMOVE
kubectl create role rabbitmq:psp:unprivileged \
    --verb=use \
    --resource=podsecuritypolicy \
    --resource-name=rabbitmq-poc-pod-security-policy
```



```shell REMOVE
kubectl create rolebinding rabbitmq-mycluster:psp:unprivileged \
    --role=rabbitmq:psp:unprivileged \
    --serviceaccount=rabbitmq-poc:rabbitmq-lob-1-1-server
```
--------------




## Install the Package
### 

```shell
kapp deploy -a tanzu-rabbitmq -f cloud/k8/tmc/data-services/rabbitmq/package/rabbit-package-install.yml -y
```


# RabbitMQ Operator Install

```shell
kubectl apply -f "https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"
```


--------------

# Create RabbitMQ cluster

verify that rabbitmqclusters.rabbitmq.com is the list

```shell
kubectl apply -f cloud/k8/tmc/data-services/rabbitmq/full-yml/tanzu-rabbitmq-replication.yaml
```


# Install RabbitMQ

## Provide imagePullSecrets

kapp deploy -a tanzu-rabbitmq-repo -f cloud/k8/tmc/data-services/rabbitmq/repo.yml -y


kubectl get packages
## Install Downstream

kubeclt apply -f cloud/k8/tmc/data-services/rabbitmq/dowstream/rabbitmq-downstream.yml


kubectl apply -f "https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"



## ERROR Debugging

kubectl get packagerepository tanzu-rabbitmq-repo -o yaml

kubectl get packageinstall tanzu-rabbitmq -o yaml


I am using my same credential I used for register.pivotal.io.
I verified I can login with those credentials to registry.tanzu.vmware.com.

Do you know if there is something special I need to do to get the proper permissions?



rabbitmqctl add_vhost rabbitmq_schema_definition_sync



-------------------------

# Setup

## Add new users

```shell
# <user>: bugs
# <password>: bunny
kubectl -n rabbitmq-poc exec rabbitmq-lob-1-1-server-0 -- rabbitmqctl add_user bugs bunny

kubectl -n rabbitmq-poc exec rabbitmq-lob-1-1-server-0 -- rabbitmqctl set_permissions  -p / bugs ".*" ".*" ".*"

kubectl -n rabbitmq-poc exec rabbitmq-lob-1-1-server-0 -- rabbitmqctl set_user_tags bugs administrator

```

```shell
kubectl port-forward rabbitmq-lob-1-1-server-0  15672:25672
```

Change replicas to 3

```shell
kubectl edit rmq rabbitmq-lob-1-1
```
---------------

k apply -f cloud/k8/tmc/data-services/rabbitmq/full-yml/tanzu-rabbitmq-replication.yaml

kubectl get packageinstall tanzu-rabbitmq -o yaml

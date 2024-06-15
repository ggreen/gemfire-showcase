kubectl create namespace rabbitmq-system
kubectl create secret docker-registry reg-creds --docker-server "registry.tanzu.vmware.com" --docker-username $HARBOR_USER --docker-password $HARBOR_PASSWORD

kubectl create secret docker-registry tanzu-rabbitmq-registry-creds -n rabbitmq-system --docker-server "registry.tanzu.vmware.com" --docker-username $HARBOR_USER --docker-password $HARBOR_PASSWORD

kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v1.3.1/cert-manager.yaml

sleep 5

#Wait for cert manager
kubectl wait pod -l=app.kubernetes.io/instance=cert-manager --for=condition=Ready --timeout=160s -n cert-manager

#Install cluster operator
kubectl apply -f "https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"

# Wait for operator
kubectl wait pod -l=app.kubernetes.io/name=rabbitmq-cluster-operator --for=condition=Ready --timeout=160s --namespace=rabbitmq-system

#Then, to install the Message Topology Operator, run the following command:
kubectl apply -f https://github.com/rabbitmq/messaging-topology-operator/releases/latest/download/messaging-topology-operator-with-certmanager.yaml

#Wait for operator to run
kubectl wait pod -l=app.kubernetes.io/name=messaging-topology-operator --for=condition=Ready --timeout=160s -n rabbitmq-system


#----------------
kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-hub.yml
kubectl wait pod -l=app.kubernetes.io/name=rabbitmq-hub --for=condition=Ready --timeout=160s

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-hub-user.yml

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/hub-topology.yaml

#------------
# Site 1

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-site-1.yml

kubectl wait pod -l=app.kubernetes.io/name=rabbitmq-site1 --for=condition=Ready --timeout=160s


kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-site-1-user.yml

kubectl apply  -f deployment/cloud/k8/data-services/rabbitmq/multi-site/site1-topology.yaml

#------------
# Site 2

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-site-2.yml

kubectl wait pod -l=app.kubernetes.io/name=rabbitmq-site2 --for=condition=Ready --timeout=160s


kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-site-2-user.yml

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/verticals/transportation_logistics/messsage-topology/site2-topology.yaml

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/site2-topology.yaml

#------------
# Site 3

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-site-3.yml

kubectl wait pod -l=app.kubernetes.io/name=rabbitmq-site3 --for=condition=Ready --timeout=160s

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/rabbitmq-site-3-user.yml

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/site3-topology.yaml

# ------------------
# Shovels

kubectl apply -f deployment/cloud/k8/data-services/rabbitmq/multi-site/site1-hub-replication.yml
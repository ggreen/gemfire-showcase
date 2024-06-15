export ACCT_USER_NM=`kubectl get secrets/rabbitmq-default-user --template={{.data.username}} | base64 -d`
export ACCT_USER_PWD=`kubectl get secrets/rabbitmq-default-user --template={{.data.password}} | base64 -d`
helm repo add bitnami https://charts.bitnami.com/bitnami

helm install scdf oci://registry-1.docker.io/bitnamicharts/spring-cloud-dataflow --set externalRabbitmq.enabled=true --set rabbitmq.enabled=false --set externalRabbitmq.host=rabbitmq --set externalRabbitmq.username=$ACCT_USER_NM --set externalRabbitmq.password=$ACCT_USER_PWD --set server.service.type=LoadBalancer --set server.service.ports.http=9393

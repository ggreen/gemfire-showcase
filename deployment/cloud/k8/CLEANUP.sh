cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuRabbitMQ/dev/tanzu-rabbitmq-event-streaming-showcase
kubectl delete -f cloud/k8/apps/account-jdbc-amqp-sink


cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuRabbitMQ/dev/tanzu-rabbitmq-event-streaming-showcase
kubectl delete -f cloud/k8/apps/account-gemfire-amqp-sink/account-gemfire-amqp-sink.yml

cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuRabbitMQ/dev/tanzu-rabbitmq-event-streaming-showcase
kubectl delete -f cloud/k8/apps/account-http-ampq-source/account-http-ampq-source.yml


cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuRabbitMQ/dev/tanzu-rabbitmq-event-streaming-showcase
kubectl delete -f cloud/k8/data-services/gemfire/gemfire.yml


kubectl delete pvc data-gemfire1-locator-0 data-gemfire1-locator-0 data-gemfire1-server-0 data-gemfire1-server-1 data-gemfire1-server-2

cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuRabbitMQ/dev/tanzu-rabbitmq-event-streaming-showcase
k delete -f cloud/k8/data-services/rabbitmq/vmware-rabbitmq-1-node.yml


cd /Users/Projects/VMware/Tanzu/TanzuData/TanzuRabbitMQ/dev/tanzu-rabbitmq-event-streaming-showcase
kubectl delete -f cloud/k8/data-services/postgres/postgres.yml

echo Login to registry.tanzu.vmware.com

docker login registry.tanzu.vmware.com
docker pull registry.tanzu.vmware.com/gemfire-management-console/gemfire-management-console:1.2.0

docker run -d -p 8080:8080 --rm --name gideon-console   --network=gemfire-cache registry.tanzu.vmware.com/gemfire-management-console/gemfire-management-console:1.2.0

docker pull pivotalrabbitmq/perf-test:2.16.0

export PRIVATE_CONTAINER_REPO=harbor.shared-services.sg.kirkware.com

docker tag pivotalrabbitmq/perf-test:2.16.0 $PRIVATE_CONTAINER_REPO/lob-1/pivotalrabbitmq/perf-test:2.16.0
docker push $PRIVATE_CONTAINER_REPO/lob-1/pivotalrabbitmq/perf-test:2.16.0


k apply -f cloud/k8/tmc/perf-test/perf-test.yml

k delete -f cloud/k8/tmc/perf-test/perf-test.yml
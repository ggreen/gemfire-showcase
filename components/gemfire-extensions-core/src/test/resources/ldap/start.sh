#docker rmi -f h3nrik/apacheds
docker run --rm --name ldap -d -p 389:10389 h3nrik/apacheds

ldapadd -h localhost -p 10389 -x -W -D "uid=admin,ou=system" -f nyla.ldif
ldapadd -h localhost -p 10389 -x -W -D "uid=admin,ou=system" -f cluster.ldif
ldapadd -h localhost -p 10389 -x -W -D "uid=admin,ou=system" -f datawrite.ldif

source ./setenv.sh

mkdir -p $ROOT_DIR
chown -R $GEM_USER $ROOT_DIR

echo Made $ROOT_DIR owned by $GEM_USER

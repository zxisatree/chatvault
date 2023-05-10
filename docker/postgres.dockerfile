FROM postgres:13.1-alpine
COPY ./docker/docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
FROM debian:stable-slim

LABEL maintainer="Joewalker"

RUN apt-get update; \
    apt-get install -y curl unzip zip socat;
WORKDIR /root
COPY entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh && \
    ./entrypoint.sh
EXPOSE 2375

CMD [ "socat", "tcp-listen:2375,fork,reuseaddr", "unix-connect:/var/run/docker.sock" ]
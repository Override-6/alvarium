FROM alpine:latest

RUN apk add mosquitto-clients

ENTRYPOINT mosquitto_sub -v -t alvarium-topic -h mosquitto-server

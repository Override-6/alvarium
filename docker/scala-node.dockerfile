FROM openjdk:22 as builder
WORKDIR /alvarium

ARG name

ADD . .
RUN curl -L https://raw.githubusercontent.com/lefou/millw/0.4.11/millw > mill && chmod +x mill
RUN ./mill $name.assembly

FROM eclipse-temurin:22-alpine as runtime
WORKDIR /alvarium
ARG name

RUN apk update && apk add curl
COPY --from=builder /alvarium/out/$name/assembly.dest/out.jar .
COPY --from=builder /alvarium/config.json .
COPY --from=builder /alvarium/res/ res
ENTRYPOINT java -jar out.jar
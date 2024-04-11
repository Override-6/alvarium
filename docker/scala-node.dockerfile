FROM openjdk:22 as builder
WORKDIR /alvarium

ARG name

VOLUME ~/.cache
VOLUME ~/.mill

ADD mill build.sc ./
RUN ./mill resolve _
ADD lib lib
ADD $name $name
ADD res res
ADD config.json .

RUN ls
RUN ./mill $name.assembly



FROM eclipse-temurin:22-alpine as runtime
WORKDIR /alvarium
ARG name

COPY --from=builder /alvarium/out/$name/assembly.dest/out.jar .
COPY --from=builder /alvarium/config.json .
COPY --from=builder /alvarium/res/ res
ENTRYPOINT java -jar out.jar
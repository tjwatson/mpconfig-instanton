FROM icr.io/appcafe/open-liberty:beta

COPY --chown=1001:0 /src/main/liberty/config /config
COPY --chown=1001:0 target/*.war /config/apps

RUN configure.sh
RUN checkpoint.sh afterAppStart


#!/bin/sh

./mvnw package

sudo podman build -t mpconfig-instanton --cap-add CHECKPOINT_RESTORE --cap-add SYS_PTRACE --cap-add SETPCAP --security-opt seccomp=unconfined .

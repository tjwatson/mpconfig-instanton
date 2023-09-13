#!/bin/sh

sudo podman run \
	--rm \
	-p 9080:9080 \
	--cap-add CHECKPOINT_RESTORE \
	--cap-add SETPCAP \
       	--security-opt seccomp=unconfined \
	--security-opt systempaths=unconfined \
	--env IO_OPENLIBERTY_EXAMPLE_INSTANTON_CONFIG1=envValue1 \
	--env IO_OPENLIBERTY_EXAMPLE_INSTANTON_CONFIG2=envValue2 \
	mpconfig-instanton

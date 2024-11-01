#!/bin/bash

set -euo pipefail

set -a; source .env; set +a

upload() {
    curl --request POST \
        --verbose \
        --header "Authorization: Bearer $MAVEN_CENTRAL_TOKEN" \
        --form bundle=lens.zip \
        https://central.sonatype.com/api/v1/publisher/upload
}

cmdCheck() {
    DEPLOYMENT=$1
    curl --request POST \
        --verbose \
        --header "Authorization: Bearer $MAVEN_CENTRAL_TOKEN" \
        "https://central.sonatype.com/api/v1/publisher/deployment/$DEPLOYMENT" \
        | jq
}

cmdPublish() {
    rm -f ./lens.zip
    ./gradlew clean publish
    pushd ./lens/build/staging-deploy
        zip -r ../../../lens.zip ./*
    popd
    upload
}

CMD=$1
case "$CMD" in
	"publish") cmdPublish ;;
    "check") cmdCheck $2 ;;
	*) echo "Unknown command $CMD"; exit 1 ;;
esac
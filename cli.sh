#!/bin/bash

set -euo pipefail

set -a; source .env; set +a

MAVEN_CENTRAL_TOKEN=$(printf "$MAVEN_CENTRAL_USERNAME:$MAVEN_CENTRAL_PASSWORD" | base64 )

upload() {
    curl --request POST \
        --verbose \
        --header "Authorization: Bearer $MAVEN_CENTRAL_TOKEN" \
        --form bundle=@lens.zip \
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
        pushd ./nl/bvkatwijk/java-lens
            VERSION=$(ls -lA | awk '/^d/ {print $9}')
            pushd ./$VERSION
                gpg --verify java-lens-$VERSION.jar.asc java-lens-$VERSION.jar
            popd
        popd
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
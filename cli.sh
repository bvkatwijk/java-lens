#!/bin/bash

set -euo pipefail

set -a; source .env; set +a

TOKEN=$(printf "$OSSRH_USERNAME:$OSSRH_PASSWORD" | base64 )

echo $TOKEN

# POST /api/v1/publisher/deployment/{deploymentId}
# POST /api/v1/publisher/upload

upload() {
    curl --request POST \
        --verbose \
        --header "Authorization: Bearer $TOKEN" \
        --form bundle=@central-bundle.zip \
        https://central.sonatype.com/api/v1/publisher/upload
}


# curl --request POST \
#   --verbose \
#   --header "Authorization: Bearer $TOKEN" \
#   'https://central.sonatype.com/api/v1/publisher/deployment/28570f16-da32-4c14-bd2e-c1acc0782365

cmdPublish() {
    ./gradlew clean publishMavenJavaPublicationToMavenLocal
}

CMD=$1
case "$CMD" in
	"publish") echo "cmdPublish" ;;
	*) echo "Unknown command $CMD"; exit 1 ;;
esac
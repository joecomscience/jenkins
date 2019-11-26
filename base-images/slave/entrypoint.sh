#!/bin/bash

set -e

echo '--Install Package Manager--'
curl -s "https://get.sdkman.io" | bash > /dev/null
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk version

echo '--Install Dependency--'
java_version="8.0.232.hs-adpt"

sdk install java $java_version
sdk install maven

#!/bin/bash

helpFunction() {
  echo ""
  echo "Script used to create new core module in project"
  echo "Usage: $0 -n <module_name>"
  echo -e "\t-n Core module to create name"
  exit 1
}

while getopts "n:" opt
do
  case "$opt" in
    n ) moduleName="$OPTARG" ;;
    ? ) helpFunction ;;
  esac
done

if [ "$moduleName" = "" ]; then
  helpFunction
fi

mkdir ../core/$moduleName
touch ../core/$moduleName/.gitignore
echo "/build" > ../core/$moduleName/.gitignore
touch ../core/$moduleName/build.gradle.kts
moduleNameLowercase=$(echo "$moduleName" | tr '[:upper:]' '[:lower:]')
cat << EOF > ../core/$moduleName/build.gradle.kts
plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library)
}

android {
    namespace = "by.bashlikovvv.$moduleNameLowercase"
}

dependencies {
    //TODO add needed dependencies
}
EOF
mkdir -p ../core/$moduleName/src/main/kotlin/by/bashlikovvv/$moduleNameLowercase
echo "include(\":core:$moduleName\")" >> ../settings.gradle.kts

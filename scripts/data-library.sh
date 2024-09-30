#!/bin/bash

helpFunction() {
  echo ""
  echo "Script used to create new data module in project"
  echo "Usage: $0 -n <module_name>"
  echo -e "\t-n Data module to create name"
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

mkdir ../data/$moduleName
touch ../data/$moduleName/.gitignore
echo "/build" > ../data/$moduleName/.gitignore
touch ../data/$moduleName/build.gradle.kts
moduleNameLowercase=$(echo "$moduleName" | tr '[:upper:]' '[:lower:]')
cat << EOF > ../data/$moduleName/build.gradle.kts
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
mkdir -p ../data/$moduleName/src/main/kotlin/by/bashlikovvv/$moduleNameLowercase
echo "include(\":data:$moduleName\")" >> ../settings.gradle.kts

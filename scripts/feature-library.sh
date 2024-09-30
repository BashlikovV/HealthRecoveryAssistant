#!/bin/bash

helpFunction() {
  echo ""
  echo "Script used to create new feature in project"
  echo "Usage: $0 -n <module_name>"
  echo -e "\t-n Feature module to create name"
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

mkdir ../features/$moduleName
touch ../features/$moduleName/.gitignore
echo "/build" > ../features/$moduleName/.gitignore
touch ../features/$moduleName/build.gradle.kts
moduleNameLowercase=$(echo "$moduleName" | tr '[:upper:]' '[:lower:]') 
cat << EOF > ../features/$moduleName/build.gradle.kts
plugins {
    alias(libs.plugins.healthrecoveryassistant.android.library.compose)
}

android {
    namespace = "by.bashlikovvv.$moduleNameLowercase"
}

dependencies {
    //TODO add needed dependencies
}
EOF
mkdir -p ../features/$moduleName/src/main/kotlin/by/bashlikovvv/$moduleNameLowercase
echo "include(\":features:$moduleName\")" >> ../settings.gradle.kts

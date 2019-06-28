#!/usr/bin/env bash
path=$(cd `dirname $0`; pwd)

lib="/Users/Neo/Documents/git/EASYLIB"
rlib="$lib/repository"

echo "更新 $lib"
cd "$lib"
git pull

echo "打包 $rlib"
cd "$path"
mvn deploy -DaltDeploymentRepository=hengyunabc-mvn-repo::default::file:$rlib

cd $lib
git add .
git commit -m "$1"
git push

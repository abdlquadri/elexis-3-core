#!/usr/bin/env bash

WEBSPACE=/var/www/vhosts/elexis.ch/httpdocs/ungrad

PRODUCTS=${WEBSPACE}/products/elexis-core
P2=${WEBSPACE}/p2/elexis-core

mkdir -p ${PRODUCTS}/${BUILD_NUMBER}
mkdir -p ${P2}/${BUILD_NUMBER}

cp ch.elexis.core.p2site/target/products/*.zip ${PRODUCTS}/${BUILD_NUMBER}
cp -R ch.elexis.core.p2site/target/repository/* ${P2}/${BUILD_NUMBER}

rm ${PRODUCTS}/latest
ln -s ${BUILD_NUMBER} ${WEBSPACE}/products/elexis-core/latest
rm ${P2}/latest
ln -s ${BUILD_NUMBER} ${WEBSPACE}/p2/elexis-core/latest


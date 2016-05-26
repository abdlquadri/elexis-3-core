#!/usr/bin/env bash

$PRODUCTS=/var/www/vhosts/elexis.ch/httpdocs/ungrad/products/${BUILD_NUMBER}
$P2=/var/www/vhosts/elexis.ch/httpdocs/ungrad/p2/elexis-core/${BUILD_NUMBER}

mkdir $PRODUCTS
mkdir $P2

cp ch.elexis.core.p2site/target/products/*.zip $PRODUCTS
cp -R ch.elexis.core.p2site/target/repository/* $P2

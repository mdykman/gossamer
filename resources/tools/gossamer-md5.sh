#!/bin/sh

BASE=`dirname $0`

groovy -cp $BASE/../webapp/ROOT/WEB-INF/lib/gossamer.jar  $BASE/md5.groovy "$@"

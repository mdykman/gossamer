#!/bin/sh

THIS=`readlink -f $0`
JETTY_BIN=`dirname $THIS`
JETTY_HOME=`dirname $JETTY_BIN`
GOSSAMER_HOME="${JETTY_HOME}/gossamer"

. ${GOSSAMER_HOME}/etc/extraclasspath.sh

# echo "I${GOSSAMER_HOME}I"
# echo "I${EXTRACLASSPATH}I"
JAVA_OPTIONS="${JAVA_OPTIONS} -Dgossamer.path.base=${GOSSAMER_HOME}"
JAVA_OPTIONS="${JAVA_OPTIONS} -Dgossamer.classpath.extra=${EXTRACLASSPATH}"

export JETTY_HOME JAVA_OPTIONS GOSSAMER_HOME

cd "${JETTY_HOME}"

./bin/jetty.sh "$@"


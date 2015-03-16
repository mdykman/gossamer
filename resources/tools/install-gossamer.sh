#!/bin/sh

shopt -s extglob

#constants
DEFTARGET=/opt/gossamer

DEFSOURCE=http://localhost/svn/gossamer/trunk

CHOWN=/bin/chown
CHMOD=/bin/chmod
TAR=/bin/tar
SVN=/usr/bin/svn

SCRIPTBASE=`dirname $0`

. ${SCRIPTBASE}/functions.sh

MERGE=${SCRIPTBASE}/merge.sh
CONFIGFILE=${SCRIPTBASE}/config.sh

if [[ -z "$GOSSAMER_HOME" ]]; then 
	read -p "install target [${DEFTARGET}]: " INPUT
	if [[ -n "$INPUT" ]]; then 
		GOSSAMER_HOME=$INPUT;  
	else
		GOSSAMER_HOME=$DEFTARGET;  
	fi;
fi;

# run config 

# get source from  command line (if present)
if [[ -n "$1" ]]; then
	SOURCE="$1"
	shift;
fi;

# config

# use default source if none specified at the command line
if [[ -z "$SOURCE" ]]; then
	SOURCE="$DEFSOURCE"
fi;


WORKDIR=`dirname $GOSSAMER_HOME`;
# test is a url was specified
case $SOURCE in 
	http://*)	URL="$SOURCE" ;;
	*)		URL="" ;;
esac


def_jettyhome
def_jetty

# if it's a directory
if [[ -d "$SOURCE" ]]; then
	CORE=`cd $SOURCE; pwd`
#if URL
elif [[ -n "$URL" ]]; then
	case $SOURCE in 
	# remote tarball
		*.tar.gz) 
			TARBALL=`echo "$SOURCE" | awk ' { if(0 < match($0,/[^/]+[.]tar[.]gz$/,m)) {print m[0]} }'`
			curl $SOURCE 
			if [[ "$?" != "0" ]]; then echo "failed to download $SOURCE"; fi;
			;;
		*) 
	# SVN repo
			SVNSOURCE="$SOURCE"
			echo "installing from svn $SVNSOURCE"
			TARBALL="" 
			;;
	esac
else
# local tarball
	case $SOURCE in 
		*.tar.gz) 
		TARBALL="$SOURCE"
	esac
fi;


cd $WORKDIR

#checkout
if [[ -n "$SVNSOURCE" ]]; then
	CHECKOUT=/opt/gossamer-checkout
	read -p "checkout $SVNSOURCE to directory [$CHECKOUT]: " TMP
	read -p "svn username for $SVNSOURCE: " SVNUSER
	if [[ -n "$TMP" ]]; then CHECKOUT="$TMP"; fi;

	if [[ -d "$CHECKOUT" ]]; then
		read -p "previous version of $CHECKOUT.. remove it? [Y/n]: " YN
		if [[ -z "$YN" || "y" == "$YN" || "Y" == "$YN" ]]; then
			rm -Rf $CHECKOUT
		else
			echo "updating from SVN"
			UPDATESVN=true
		fi;
	fi;

	if [[ -n "$SVNUPDATE" ]]; then
		$SVN --username="$SVNUSER" update $CHECKOUT
	else
		$SVN --username="$SVNUSER" checkout $SVN $CHECKOUT
	fi;
	CORE=$CHECKOUT
elif [[ -z "$CORE" ]]; then
# tarball install
	CORE=`echo "$TARBALL" | awk '{ if(0 < match($0,/^(.+\/)?([^\/]+)(-[0-9]+-[0-9]+)[.]tar[.]gz$/,m)) {print m[2]} }'`
	echo "CORE=$CORE"
	if [[ -d "$CORE" ]]; then
		backup_file $CORE
	fi;
	$TAR xfz $TARBALL
	if [[ "$?" != "0" ]]; then
		echo "failed to untar $TARBALL"
		exit 1
	fi;
fi;

backup_file $GOSSAMER_HOME ".backup"
ln -s $CORE $GOSSAMER_HOME


EXTRACLASSPATH="${GOSSAMER_HOME}/scripts/library"

read -p "install application now? [y/N]: " YN
if [[ "Y" == "$YN" || "y" == "$YN" ]]; then 
	. $MERGE
fi;

# we know where jetty is 
# remove existing webapps (if any)
TMP=`ls $JETTY_HOME/contexts/*.xml 2> /dev/null`
if [[ -n "$TMP" ]]; then 
	for f in "$TMP"; do 
		backup_file "$f" ".ignore"
	done;
fi;

TMP=`ls $JETTY_HOME/webapps/*.war 2> /dev/null`
if [[ -n "$TMP" ]]; then 
	for f in "$TMP"; do 
		backup_file "$f" ".ignore"
	done;
fi;

# install gossamer context file
conf_file "$GOSSAMER_HOME/config-sample/jetty7/context.xml" \
	"/tmp/gossamer.xml" \
	'[$][{]GOSSAMER_HOME[}]' "$GOSSAMER_HOME"

conf_file "/tmp/gossamer.xml" \
	"$JETTY_HOME/contexts/gossamer.xml" \
	'[$][{]EXTRACLASSPATH[}]' "$EXTRACLASSPATH"



# install gossamer defaults-context file

conf_file "$GOSSAMER_HOME/config-sample/jetty7/jetty7-gossamer-default.xml" \
	"$JETTY_HOME/etc/gossamer-default.xml" \
	'[$][{]GOSSAMER_HOME[}]' $GOSSAMER_HOME
# gossamer properties
conf_file "$GOSSAMER_HOME/webapp/WEB-INF/gossamer.properties.template"   \
	"$GOSSAMER_HOME/webapp/WEB-INF/gossamer.properties"  \
	'[$][{]GOSSAMER_HOME[}]' $GOSSAMER_HOME

# set ownership and permissions
set_perms "$GOSSAMER_HOME" "$JETTY_USER" "$JETTY_GROUP"

cd $GOSSAMER_HOME/scripts
if [[ "$?" == "0" ]]; then
	ln -s ../www
	ln -s ../xsl
fi;

echo "done."

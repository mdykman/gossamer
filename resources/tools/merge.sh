#!/bin/bash


conf_home
def_jetty

echo "installing to ${GOSSAMER_HOME}"

if [[ -z "$APP_HOME" ]]; then
	read -e -p "application directory: " APP_HOME

fi;
if [[ ! -d "$APP_HOME" ]]; then
	echo "$APP_HOME is not a directory. aborting ..."
	exit 1
fi;


if [[ ! -d "$APP_HOME/scripts" ]]; then
		echo "no directory $APP_HOME/scripts found. aborting.."
		exit 10
fi;

if [[ ! -d "$APP_HOME/www" ]]; then
		echo "no directory $APP_HOME/www found. aborting.."
		exit 11
fi;

if [[ ! -d "$APP_HOME/xsl" ]]; then
		echo "no directory $APP_HOME/xsl found. aborting.."
		exit 11
fi;

echo "copying resources from $APP_HOME to $GOSSAMER_HOME"

WEBINF="webapp/WEB-INF"
WIR="applicationContext-user.xml site.properties jdbc.properties"


TT="$GOSSAMER_HOME/$WEBINF/"  
for f in $WIR; do 
	SS="$APP_HOME/$WEBINF/$f" 
	if [[ -f "$SS" ]]; then
#		echo "  copying $SS to $TT"  
		cp "$SS" "$TT"  
		set_perms "$TT"
	else
		echo "$SS not found.  using builtin"
	fi;
done;

TEST=`ls $APP_HOME/etc/*.db > /dev/null | wc -l`

if [[ "$TEST" != "0" ]]; then
echo "  ** installing sqlite dbs"
	for f in `ls $APP_HOME/etc/*.db`; do
		cp $APP_HOME/etc/${f} $GOSSAMER_HOME/etc
		set_perms $APP_HOME/etc/${f}
	done;
fi;

if [[ -f "$APP_HOME/etc/rewrite.rules" ]]; then
#	echo "  copying $APP_HOME/etc/rewrite.rules to $GOSSAMER_HOME/etc"
	cp "$APP_HOME/etc/rewrite.rules" "$GOSSAMER_HOME/etc"
	set_perms $GOSSAMER_HOME/etc/rewrite.rules
fi;

if [[ -f "$APP_HOME/etc/rewrite.test" ]]; then
#	echo "  copying $APP_HOME/etc/rewrite.test to $GOSSAMER_HOME/etc"
	cp "$APP_HOME/etc/rewrite.test" "$GOSSAMER_HOME/etc"
	set_perms $GOSSAMER_HOME/etc/rewrite.test
fi;

echo "  ** installing application libraries"
if [[ -d "$APP_HOME/$WEBINF/lib" ]]; then
	cp $APP_HOME/$WEBINF/lib/* $GOSSAMER_HOME/$WEBINF/lib
	set_perms $GOSSAMER_HOME/$WEBINF/lib
fi;

if [[ -d "$APP_HOME/$WEBINF/classes" ]]; then
	mkdir -p $GOSSAMER_HOME/$WEBINF/classes
	cp -R $APP_HOME/$WEBINF/classes/* $GOSSAMER_HOME/$WEBINF/classes
	set_perms $GOSSAMER_HOME/$WEBINF/classes
fi;


cd $GOSSAMER_HOME

echo "  ** installing application links"

rm_link www
rm_link xsl
rm_link scripts
rm_link res


ln -s $APP_HOME/xsl
if [[ "$?" != "0" ]]; then
	echo "failed to create link 'www'.  aborting..."
	exit 1
fi;
set_perms "$APP_HOME/xsl"

ln -s $APP_HOME/www
if [[ "$?" != "0" ]]; then
	echo "failed to create link 'www'.  aborting..."
	exit 1
fi;
set_perms "$APP_HOME/www"

ln -s $APP_HOME/scripts
if [[ "$?" != "0" ]]; then
	echo "failed to create link 'scripts'.  aborting..."
	exit 1
fi;
set_perms "$APP_HOME/scripts"

ln -s $APP_HOME/res
if [[ "$?" != "0" ]]; then
	echo "failed to create link 'res'.  aborting..."
	exit 1
fi;
set_perms "$APP_HOME/res"


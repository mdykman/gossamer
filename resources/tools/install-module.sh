#!/bin/sh

MODULEPATH=$1;
shift;
TARGET=$1;
shift;



MKDIR="mkdir -p"
LN="ln -s"

SCRIPTBASE=`dirname $0`
. $SCRIPTBASE/functions.sh

if [[ ! -d "$MODULEPATH" ]]; then
	echo "no module found at $MODULEPATH" > /dev/stderr
	exit 1;
fi;

if [[ -z "$TARGET" ]]; then
	conf_home	
	TARGET="${GOSSAMER_HOME}"
fi;

MODULE=`basename $MODULEPATH`

# establish jetty user/group
def_jetty

if [[ -d "$MODULEPATH/www" ]]; then
	backup_file "$TARGET/www/$MODULE"
	$LN "$MODULEPATH/www" "$TARGET/www/$MODULE"
	for f in `ls -d $TARGET/www/$MODULE/*`; do
		set_perms $f $JETTY_USER $JETTY_GROUP;
		echo "   WWW - $f"
#		set_perms $TARGET/www/$MODULE/$f;
	done;
fi;

if [[ -d "$MODULEPATH/xsl" ]]; then
	XSLDIRS=`ls -d $MODULEPATH/xsl/*/*`
	for dd in "$XSLDIRS"; do
		pd=`echo $dd | awk '{ if(0 < match($0,/[/]([^/]+[/][^/]+)$/,m)) {print m[1]} }'`
		if [[ -d "$MODULEPATH/xsl/$pd" ]]; then
			backup_file "$TARGET/xsl/$pd/$MODULE"
			$LN "$MODULEPATH/xsl/$pd" "$TARGET/xsl/$pd/$MODULE"
			for f in `ls $TARGET/xsl/$pd/$MODULE/*`; do
				echo "   XSL - $f"
				set_perms $TARGET/xsl/$pd/$MODULE/$f $JETTY_USER $JETTY_GROUP
			done;
		fi;
	done;
fi;


if [[ -d "$MODULEPATH/scripts/controllers" ]]; then
	backup_file "$TARGET/scripts/controllers/$MODULE"
	$LN $MODULEPATH/scripts/controllers  \
		"$TARGET/scripts/controllers/$MODULE"
	for f in `ls -d $TARGET/scripts/controllers/$MODULE/*`; do
		echo "   CONTROLLERS - $f"
		set_perms "$f" $JETTY_USER $JETTY_GROUP
	done;
fi;


if [[ -d "$MODULEPATH/scripts/library" ]]; then
	backup_file "$TARGET/scripts/library/$MODULE"
	$LN $MODULEPATH/scripts/library  \
		"$TARGET/scripts/library/$MODULE"
	for f in `ls -d $TARGET/scripts/library/$MODULE/*`; do
		echo "   LIBRARY - $f"
		set_perms "$f" $JETTY_USER $JETTY_GROUP;
	done;
fi;


TEST=`ls $MODULEPATH/etc/*.db 2> /dev/null | wc -l`

if [[  "$TEST" != "0" ]]; then
	echo "  ** installing sqlite dbs"
	echo "MANUAL datasource config?"
	for f in `ls $MODULEPATH/etc/*.db`; do
		echo "   SQLITE - $f"
		cp $MODULEPATH/etc/${f} $TARGET/etc
		set_perms $TARGET/etc/${f} $JETTY_USER $JETTY_GROUP
	done;
fi;

## merge the rewrite rules
if [[ -f "$MODULEPATH/etc/rewrite.rules" ]]; then
	echo "  ** merging rewrite rules"
	ever=
	if [[ -f "$TARGET/etc/rewrite.rules" ]]; then
		ever=`grep Module.$MODULE $TARGET/etc/rewrite.rules`
	fi;
	TMP="/tmp/rewrite.rules";
	if [[ -n "$ever" ]]; then
		awk "BEGIN { printme=1;  cnf=\"$MODULEPATH/etc/rewrite.rules\"; } \
				printme && !/End Module.$MODULE/ { print } \
				/Start Module.$MODULE/ { printme = 0; while ((getline line < cnf) > 0) print line;  } \
				/End Module.$MODULE/ { printme = 1; } " \
			"$TARGET/etc/rewrite.rules" >> $TMP
		echo "# End Module $MODULE" >> $TMP
	else
		echo "# Start Module $MODULE" > $TMP
		cat "$MODULEPATH/etc/rewrite.rules" >> $TMP
		echo "# End Module $MODULE" >> $TMP
		if [[ -f "$TARGET/etc/rewrite.rules" ]]; then
			cat "$TARGET/etc/rewrite.rules" >> $TMP
		fi;
	fi;

	if [[ -f "$TARGET/etc/rewrite.rules" ]]; then
		backup_file "$TARGET/etc/rewrite.rules"
	fi;
	mkdir -p "$TARGET/etc"
	mv $TMP "$TARGET/etc/rewrite.rules"
	set_perms "$TARGET/etc/rewrite.rules" $JETTY_USER $JETTY_GROUP
fi;

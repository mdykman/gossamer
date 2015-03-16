#!/bin/sh

shopt -s extglob

rm_link() {
	LINK=$1;
	shift;
if [[ -h "$LINK" ]]; then
	rm $LINK
	if [[ "$?" != "0" ]]; then
		echo "failed to remove link '$LINK'." > /dev/stderr
	fi;
fi;


}
conf_home() {

if [[ -z "$GOSSAMER_HOME" ]]; then
	GOSSAMER_HOME=/opt/gossamer
	
	read -e -p "gossamer base directory [${GOSSAMER_HOME}]: " INPUT
	if [[ -n "$INPUT" ]]; then GOSSAMER_HOME="$INPUT"; fi;
	
fi;

if [[ ! -d "$GOSSAMER_HOME" ]]; then
	echo "$GOSSAMER_HOME is not a directory. aborting..."
	exit 1
fi;

}

get_perms() {
	if [[ -z "$JETTY_USER" ]]; then
		JETTY_USER=jetty
		read -p "jetty user [${JETTY_USER}]: " INPUT
		if [[ -n "$INPUT" ]]; then JETTY_USER="$INPUT"; fi;
	fi;

	EX=`grep "^${JETTY_USER}:" /etc/passwd`
	if [[  -z "$EX" ]]; then
		echo "user $JETTY_USER does not exist. aborting..."
		exit 1
	fi;

	if [[ -z "$JETTY_GROUP" ]]; then
		JETTY_GROUP=jetty
		read -p "jetty group [${JETTY_GROUP}]: " INPUT
		if [[ -n "$INPUT" ]]; then JETTY_GROUP="$INPUT"; fi;
	fi;
	EX=`grep "^${JETTY_GROUP}:" /etc/group`
	if [[ -z "$EX" ]]; then
		echo "group $JETTY_GROUP does not exist. aborting..."
		exit 1
	fi;

};


promptextraclasspath () {
	CLASSDIR=
	CLASSES=

	read -e -p "additional jar files: " CLASSDIR
	if [[ -f "$CLASSDIR" ]]; then CLASSES="$CLASSDIR"
	elif [[ -n "$CLASSDIR" ]]; then
		for f in `find ${CLASSDIR} -name "*.jar"`; do
			if [[ -z "${CLASSES}" ]]; then 
			echo branch 1
				CLASSES="${f}"
			else 
			echo branch 2
				CLASSES="${CLASSES}:${f}"
			fi;
		done;
	fi;
	echo "end of func:  ${CLASSES}"
}

def_jettyhome() {

	if [[ -z "$JETTY_HOME" ]]; then
		if [[ -d "/usr/local/jetty" ]]; then
			JETTY_HOME="/usr/local/jetty"
		fi;
		read -e -p "jetty home [${JETTY_HOME}]: " INPUT
		if [[ -n "$INPUT" ]]; then JETTY_HOME="$INPUT"; fi;
	fi;

	if [[ ! -d "$JETTY_HOME/contexts" ]]; then
		echo "could not find $JETTY_HOME/contexts. aborting jetty install."
		exit 1
	fi;

}
def_jetty() {

	if [[ -z "$JETTY_USER" ]]; then
		JETTY_USER=jetty
		read -p "jetty user [${JETTY_USER}]: " INPUT
		if [[ -n "$INPUT" ]]; then JETTY_USER="$INPUT"; fi;
	fi;
	EX=`grep "^${JETTY_USER}:" /etc/passwd`
	if [[  -z "$EX" ]]; then
		echo "user $JETTY_USER does not exist. aborting..."
		exit 1
	fi;

	if [[ -z "$JETTY_GROUP" ]]; then
		JETTY_GROUP=jetty
		read -p "jetty group [${JETTY_GROUP}]: " INPUT
		if [[ -n "$INPUT" ]]; then JETTY_GROUP="$INPUT"; fi;
	fi;
	EX=`grep "^${JETTY_GROUP}:" /etc/group`
	if [[ -z "$EX" ]]; then
		echo "group $JETTY_GROUP does not exist. aborting..."
		exit 1
	fi;

}

set_perms() {
	local PDIR="$1";  shift;
	local PUSER="$1";  shift;
	local PGROUP="$1";  shift;

echo "perm, dir=$PDIR"
	if [[ -h "$PDIR" ]]; then
		PDIR=`readlink -f -n $PDIR`
	fi;
	if [[ -d "$PDIR" ]]; then
# owners
		find $PDIR/ -exec chown $PUSER:$PGROUP {} \;

# permission
		find $PDIR/ -type d -exec chmod 2775 {} \;
		find $PDIR/ -type f -exec chmod 0664 {} \;
	
		local TST=`find $PDIR -name '*.sh' -type f 2> /dev/null | wc -l`
		if [[ "$TST" != "0" ]]; then
			find $PDIR -name '*.sh' -type f -exec chmod 0775 {} \;
		fi;
	elif [[ -f "$PDIR" ]]; then
# owners
		chown $PUSER:$PGROUP $PDIR
# permission
		echo "$PDIR" | grep -qE ".sh$"
		if [[ "$?" == "0" ]]; then
			chmod 0664 $PDIR
		else
			chmod 0775 $PDIR 
		fi;
		chown $PUSER:$PGROUP $PDIR
	
	else 
		echo "do not know how to set permissions on $PDIR" > /dev/stderr
	fi;
}

backup_file() {
	local FILE=$1;
	shift;
	local EXT="-back"

	if [[ -n "$1" ]]; then 
		local EXT="$1"; 
		shift;
	fi;

	
	if [[ -e "$FILE" ]]; then
		if [[ -h "$FILE" ]]; then
#if it's a softlink, remove it
			rm $FILE
		elif [[ -f "${FILE}" || -d "${FILE}" ]]; then
			local BACK="${FILE}${EXT}";
# if it's a file or directory, back it up
			local cnt=1
			local BF=${BACK}
# find a unique name
			while [[ -d "$BF" ]]; do
				cnt=$(( $cnt + 1 ));
				BF="${BACK}.${cnt}"
			done;
			mv $FILE ${BF}
		else
			echo "don't know how to backup $FILE" 
		fi;
	fi;

}

config() {
	if [[ -f "$CONFIGFILE" ]]; then
		. "$CONFIGFILE"
	fi;
}


conf_file() {
	SRC=$1;
	shift;
	DST=$1;
	shift;
	PTRN=$1;
	shift;
	RPLC=$1;
	shift;

#	echo "sed  -e \"s#${PTRN}#${RPLC}#\" $SRC > $DST"
	sed  -e "s#${PTRN}#${RPLC}#" $SRC > $DST
}


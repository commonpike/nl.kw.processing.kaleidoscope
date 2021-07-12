#!/bin/sh

cd `dirname $0`/..

if [ "$COREJAR" = "" ]; then
	COREJAR=/Applications/3rdparty/Processing.app/Contents/Java/core.jar
	read -e -p "Where is Processings core.jar [$COREJAR]? " corejar
	if [ "$corejar" = "" ]; then
		corejar=$COREJAR
	fi
else
	corejar=$COREJAR
fi

if [ "$CLASSPATH" = "" ]; then
	CLASSPATH=.
	# note: only alphanumeric
	read -p "Additional classpath to use [$CLASSPATH]? " classpath
	if [ "$classpath" = "" ]; then
		classpath=$CLASSPATH
	fi
else
	classpath=$CLASSPATH
fi

echo
echo 'Document src/*.java to ./reference/*html ..'

find src -name "*.java" -print0 | xargs -0 \
 javadoc -d reference -classpath "$corejar:$classpath"

  
echo All done.


cd - &>/dev/null
 
 
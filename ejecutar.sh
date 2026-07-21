#!/bin/bash
# Script para compilar y ejecutar el Help Desk con Maven
if [ -x "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java" ]; then
  export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
else
  export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home)
fi
export PATH="$JAVA_HOME/bin:$PATH"
cd "$(dirname "$0")"

if command -v mvn >/dev/null 2>&1; then
  MVN=mvn
else
  MVN="/Applications/Apache NetBeans.app/Contents/Resources/netbeans/java/maven/bin/mvn"
fi

if [ ! -x "$MVN" ]; then
  echo "No se encontró Maven. Ejecute el proyecto desde NetBeans."
  exit 1
fi

if [ "$#" -gt 0 ]; then
  exec "$MVN" -q compile exec:java -Dexec.args="$*"
else
  exec "$MVN" -q compile exec:java
fi

#!/bin/bash
# Script para compilar y ejecutar el Help Desk
export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home)
export PATH="$JAVA_HOME/bin:$PATH"
cd "$(dirname "$0")"
mkdir -p out
echo "Compilando..."
javac -d out $(find src -name "*.java")
if [ $? -ne 0 ]; then
  echo "Error de compilación"
  exit 1
fi
echo "Ejecutando..."
java -cp out main.Main

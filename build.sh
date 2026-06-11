#!/bin/bash

# Définition des variables
APP_NAME="tkspring"
SRC_DIR="src/main/java"
BUILD_DIR="build"
LIB_DIR="lib"

# Initialisation
rm -rf $BUILD_DIR

# Compilation des fichiers Java
find $SRC_DIR -name "*.java" > sources.txt
javac -cp "$LIB_DIR/*" -d $BUILD_DIR @sources.txt
rm sources.txt

# Génération du fichier .jar
cd $BUILD_DIR || exit
jar -cvf $APP_NAME.jar *
cd ..

# Finalisation
mv $BUILD_DIR/$APP_NAME.jar $APP_NAME.jar


echo ""
echo "Build terminé."
echo ""

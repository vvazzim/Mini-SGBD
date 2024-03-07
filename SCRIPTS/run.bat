#!/bin/bash

SOURCE_DIR="F:\France\paris_cite\S1\BDDA\projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah\CODE\src"

OUTPUT_DIR="F:\France\paris_cite\S1\BDDA\projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah\CODE\bin"

MAIN_CLASS="eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah.Main"

javac -d "$OUTPUT_DIR" "$SOURCE_DIR"/*.java

java -cp "$OUTPUT_DIR" "$MAIN_CLASS"
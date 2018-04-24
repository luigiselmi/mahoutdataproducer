#!/bin/sh
java -jar target/mahoutdataproducer-0.1.0-SNAPSHOT-jar-with-dependencies.jar -output signals.csv -comparisons ../user-navigation/cortex01/pre-lf01/comparisons -views ../user-navigation/cortex01/pre-lf01/views -downloads ../user-navigation/cortex01/pre-lf01/downloads 

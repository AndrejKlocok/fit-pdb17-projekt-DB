#!/bin/sh

mvn compile $@
mvn exec:java -Dexec.mainClass="cz.vutbr.fit.pdb.project01.App" -Dexec.cleanupDaemonThreads=false -Dexec.args="$*"

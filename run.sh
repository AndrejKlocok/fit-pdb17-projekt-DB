#!/bin/sh

mvn compile $@
mvn exec:java -Dexec.mainClass="cz.vutbr.fit.pdb.core.App" -Dexec.cleanupDaemonThreads=false -Dexec.args="$*"

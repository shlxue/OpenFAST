@echo off
svn propset major.version %1 .
svn propset minor.version %2 .
echo "Set version to %1.%2"
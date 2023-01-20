# common loader
common.loader="${catalina.base}/lib","${catalina.base}/lib/*.jar","${catalina.home}/lib","${catalina.home}/lib/*.jar","${catalina.home}/lib/ext/*.jar","${catalina.home}/lib/oracle/*.jar"



# scan types by configuring a JarScanner with a nested JarScanFilter.
tomcat.util.scan.StandardJarScanFilter.jarsToScan=\
log4j-web*.jar,log4j-taglib*.jar,log4javascript*.jar,slf4j-taglib*.jar



# String cache configuration.
tomcat.util.buf.StringCache.byte.enabled=true
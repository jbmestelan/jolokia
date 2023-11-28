#!/bin/sh -x

# This script helps with development of CSS styles for Jolokia site and reference documentation.
# While the HTML pages are generated by:
#  - maven-site-plugin + asciidoctor-maven-plugin for Jolokia site
#  - antora for Jolokia reference manual
# CSS files are first packaged in maven-site skin and unpackaged by maven-site-plugin.
#
# So we can simply create symbolic links to relevant files to avoid rebuilding of the site skin.

cd ../../target/site || { echo "Please run \"mvn site -N\" first" && exit; }

rm ./css/*.css
ln -s ../../../tools/siteskin/src/main/resources/css/maven-base.css ./css
ln -s ../../../tools/siteskin/src/main/resources/css/maven-theme.css ./css
ln -s ../../../tools/siteskin/src/main/resources/css/maven-theme-jolokia.css ./css
ln -s ../../../tools/siteskin/src/main/resources/css/print.css ./css
ln -s ../../../tools/siteskin/src/main/resources/css/reset.css ./css
ln -s ../../../tools/siteskin/src/main/resources/css/site.css ./css
ln -s ../../../tools/siteskin/src/main/resources/css/text.css ./css
ln -s ../../../src/site/resources/css/style.css ./css

rm ./reference/html/_/css/jolokia.css
ln -s ../../../../../../src/documentation/supplemental-ui/css/jolokia.css ./reference/html/_/css

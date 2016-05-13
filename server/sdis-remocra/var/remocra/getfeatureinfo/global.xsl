<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:remocra="http://sdis83-remocra.priv.atolcd.com/geoserver/remocra" xmlns:gml="http://www.opengis.net/gml" xmlns:wfs="http://www.opengis.net/wfs">

<!-- Fichier global de mise en forme -->



<xsl:output method="html" 
  encoding="UTF-8" 
  doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

<xsl:template match="gml:featureMembers">
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="wfs:FeatureCollection">
  <html><body>
    <xsl:apply-templates />
  </body></html>
</xsl:template>

<xsl:template match="/">
  <xsl:apply-templates />
</xsl:template>



</xsl:stylesheet>

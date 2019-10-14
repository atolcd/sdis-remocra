<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="indispos">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="pei">
      PEI numéro <xsl:value-of select="numero"/> (<xsl:value-of select="commune"/>)
  </xsl:template>
</xsl:stylesheet>

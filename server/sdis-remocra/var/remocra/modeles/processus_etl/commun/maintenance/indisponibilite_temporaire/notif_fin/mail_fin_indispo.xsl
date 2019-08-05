<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="indispos">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="indispo">
      L'indispo de motif "<xsl:value-of select="motif"/>" sur la commune de <xsl:value-of select="commune"/> commencera le <xsl:value-of select="date_debut"/><xsl:if test="date_fin != ''"> et se terminera normalement le <xsl:value-of select="date_fin"/></xsl:if>.<br/>
      Action à réaliser : <xsl:choose><xsl:when test="bascule_auto_indispo = 'true'">aucune (mise en indisponibilité automatique) </xsl:when><xsl:otherwise>activer manuellement l'indisponibilité</xsl:otherwise></xsl:choose><br/>
      PEI concernés : <xsl:for-each select="peis/pei">PEI N° <xsl:value-of select="."/>,</xsl:for-each><br/><br/>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" encoding="UTF-8" indent="yes"/>
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="indispo">
    <xsl:if test="count(pei) > 0">
      <p>PEI passés indisponibles : <br/></p>
      <table cellpadding="5">
        <thead>
          <tr>
            <th>Commune</th>
            <th>Numéro</th>
            <th>Adresse</th>
            <th>Type</th>
            <th>Anomalies</th>
          </tr>
        </thead>
        <tbody>
          <xsl:apply-templates select="pei" mode="anomalie">
            <xsl:sort select="commune"/>
            <xsl:sort select="numero"/>
          </xsl:apply-templates>
        </tbody>
      </table>
      <br/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="non_conforme">
    <xsl:if test="count(pei) > 0">
      <p>PEI passés non conformes : <br/></p>
      <table cellpadding="5">
        <thead>
          <tr>
            <th>Commune</th>
            <th>Numéro</th>
            <th>Adresse</th>
            <th>Type</th>
          </tr>
        </thead>
        <tbody>
          <xsl:apply-templates select="pei">
            <xsl:sort select="commune"/>
            <xsl:sort select="numero"/>
          </xsl:apply-templates>
        </tbody>
      </table>
      <br/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="dispo">
    <xsl:if test="count(pei) > 0">
      <p>PEI passés disponibles : <br/></p>
      <table cellpadding="5">
        <thead>
          <tr>
            <th>Commune</th>
            <th>Numéro</th>
            <th>Adresse</th>
            <th>Type</th>
          </tr>
        </thead>
        <tbody>
          <xsl:apply-templates select="pei">
            <xsl:sort select="commune"/>
            <xsl:sort select="numero"/>
          </xsl:apply-templates>
        </tbody>
      </table>
      <br/>
    </xsl:if>
  </xsl:template>
  <xsl:template match="pei">
    <tr>
      <td><xsl:value-of select="commune"/></td>
      <td><xsl:value-of select="numero"/></td>
      <td><xsl:value-of select="voie"/></td>
      <td><xsl:value-of select="type"/></td>
    </tr>
  </xsl:template>
  <xsl:template mode="anomalie" match="pei">
    <tr>
      <td><xsl:value-of select="commune"/></td>
      <td><xsl:value-of select="numero"/></td>
      <td><xsl:value-of select="voie"/></td>
      <td><xsl:value-of select="type"/></td>
      <td><xsl:value-of select="anomalies"/></td>
    </tr>
  </xsl:template>
</xsl:stylesheet>

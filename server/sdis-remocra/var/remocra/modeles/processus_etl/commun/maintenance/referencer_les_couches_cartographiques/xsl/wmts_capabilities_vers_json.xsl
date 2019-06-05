<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xpath-default-namespace="http://www.opengis.net/wmts/1.0"
	xmlns:ows="http://www.opengis.net/ows/1.1"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:gml="http://www.opengis.net/gml">
	
	<xsl:output method="text"/>
	<xsl:template match="/">
		<xsl:apply-templates select="/Layer"/>
	</xsl:template>
	<xsl:template match="Layer">{
	"type" : "wmts",
	"libelle" : "<xsl:value-of select="ows:Title"/>",
	"id" : "[CODE]",
	"scale_min" : "0",
	"scale_max" : "1000000",
	"visibility" : true,
	"opacity" : 1,
	"interrogeable" : false,
	"items" : null,
	"format" : "<xsl:value-of select="Format"/>",
	"layers" : "<xsl:value-of select="ows:Identifier"/>",
	"url" : "[URL]",
	"styles" : [<xsl:apply-templates select="Style"/>],
	"tileMatrixSet" : {
		"nom" : "[TM_NOM]",
		"resolution_min" : [TM_RES_MIN],
		"resolution_max" : [TM_RES_MAX]
	}
}
	</xsl:template>
	<xsl:template match="Style">{
		"id" : "<xsl:value-of select="ows:Identifier"/>",
		"libelle" : "<xsl:value-of select="ows:Title"/>"
	}<xsl:if test="position() != last()">
		<xsl:text>,</xsl:text>
	</xsl:if></xsl:template>
</xsl:stylesheet>
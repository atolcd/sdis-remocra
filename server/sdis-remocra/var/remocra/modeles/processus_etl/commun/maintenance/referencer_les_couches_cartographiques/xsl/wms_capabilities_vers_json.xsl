<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://www.opengis.net/wms">
<xsl:output method="text"/>
	<xsl:template match="/">
		<xsl:apply-templates select="/Layer"/>
	</xsl:template>
	<xsl:template match="Layer">{
	"type" : "wms",
	"libelle" : "<xsl:value-of select="Title"/>",
	"id" : "[CODE]",
	"scale_min" : "0",
	"scale_max" : "1000000",
	"visibility" : true,
	"opacity" : 1,
	"interrogeable" : false,
	"items" : null,
	"wms_layer" : true,
	"layers" : "<xsl:value-of select="Name"/>",
	"url" : "[URL]",
	"sld" : null,
	"projection" : "EPSG:3758",
	"styles" : [<xsl:apply-templates select="Style"/>]
}
	</xsl:template>
	
	<xsl:template match="Style">{
		"id" : "<xsl:value-of select="Name"/>",
		"libelle" : "<xsl:value-of select="Title"/>",
		"legende" : "[URL]?REQUEST=GetLegendGraphic&amp;SERVICE=WMS&amp;VERSION=1.3.0&amp;LAYER=<xsl:value-of select="../Name"/>&amp;FORMAT=image/png&amp;STYLE=<xsl:value-of select="Name"/>"
	}<xsl:if test="position() != last()">
		<xsl:text>,</xsl:text>
	</xsl:if></xsl:template>
		
</xsl:stylesheet>
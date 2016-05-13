<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:remocra="http://sdis83-remocra.priv.atolcd.com/geoserver/remocra" xmlns:gml="http://www.opengis.net/gml" xmlns:wfs="http://www.opengis.net/wfs">

<!-- Fichier des règles Profil de droits "public" -->



<!-- Règles communes : début -->
<xsl:include href="global.xsl"/>
<xsl:include href="all_layers.xsl"/>
<!-- Règles communes : fin -->



<!-- Règles spécifiques : début -->

<xsl:template match="remocra:res_route">
  <b>Route : <xsl:value-of select="remocra:com_nme"/></b> (<xsl:value-of select="remocra:nom_usuel"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
	<tr><td><u>commune : </u></td><td><xsl:value-of select="remocra:commune"/></td></tr>
	<tr><td><u>catégorie : </u></td><td><xsl:value-of select="remocra:categorie"/></td></tr>
	<tr><td><u>vitesse : </u></td><td><xsl:value-of select="remocra:vitesse"/></td></tr>
	<tr><td><u>restriction de hauteur : </u></td><td><xsl:value-of select="remocra:hauteur_maximale"/></td></tr>
	<tr><td><u>restriction de largeur : </u></td><td><xsl:value-of select="remocra:lageur_maximale"/></td></tr>
	<tr><td><u>restriction de tonnage : </u></td><td><xsl:value-of select="remocra:tonnage_maximal"/></td></tr>
  </table>
</xsl:template>
<xsl:template match="remocra:v_permis">
  <b>Permis : <xsl:value-of select="remocra:nom"/></b> - <xsl:value-of select="remocra:numero"/>
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td>Voie : </td><td><xsl:value-of select="remocra:voie"/></td></tr>
    <tr><td>Complément : </td><td><xsl:value-of select="remocra:complement"/></td></tr>
    <tr><td>Observations : </td><td><xsl:value-of select="remocra:observations"/></td></tr>
    <tr><td>Parcelle cadastrale : </td><td><xsl:value-of select="remocra:parcelle_cadastrale"/></td></tr>
    <tr><td>Section cadastrale : </td><td><xsl:value-of select="remocra:section_cadastrale"/></td></tr>
	<tr><td>Année : </td><td><xsl:value-of select="remocra:annee"/></td></tr>
  </table>
</xsl:template>
<xsl:template match="remocra:v_hydrant_pibi">
  <b>Pibi : <xsl:value-of select="remocra:numero"/></b>
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Diamètre : </u></td><td><xsl:value-of select="remocra:diametre"/></td></tr>
    <tr><td><u>Débit : </u></td><td><xsl:value-of select="remocra:debit"/></td></tr>
    <tr><td><u>Pression statique : </u></td><td><xsl:value-of select="remocra:pression"/></td></tr>
    <tr><td><u>Presion dynamique : </u></td><td><xsl:value-of select="remocra:pression_dyn"/></td></tr>
    <tr><td><u>Disponibilité : </u></td><td><xsl:value-of select="remocra:dispo_terrestre"/></td></tr>
  </table>
</xsl:template>

<!-- Règles spécifiques : fin -->



</xsl:stylesheet>

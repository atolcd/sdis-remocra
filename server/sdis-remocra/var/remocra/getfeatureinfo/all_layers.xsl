<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:remocra="http://sdis83-remocra.priv.atolcd.com/geoserver/remocra" xmlns:gml="http://www.opengis.net/gml" xmlns:wfs="http://www.opengis.net/wfs">

<!-- Fichier des règles communes (toutes les couches) -->

<!-- Filtrer tout ce qui n'est pas traité -->
<xsl:template match="node()|@*"/>



<!-- Piste -->
<xsl:template match="remocra:piste">
  <b>Route : <xsl:value-of select="remocra:com_nme"/></b>
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Gabarit : </u></td><td><xsl:value-of select="remocra:acces"/></td></tr>
	<tr><td><u>Croisement linéaire : </u></td><td><xsl:value-of select="remocra:croisement_lineaire"/></td></tr>
	<tr><td><u>Date de relevé GPS : </u></td><td><xsl:value-of select="remocra:date_de_releve"/></td></tr>
	<tr><td><u>Largeur : </u></td><td><xsl:value-of select="remocra:largeur"/></td></tr>
	<tr><td><u>Revêtement : </u></td><td><xsl:value-of select="remocra:nature"/></td></tr>
	<tr><td><u>Pente : </u></td><td><xsl:value-of select="remocra:pente"/></td></tr>
  </table>
</xsl:template>

<!-- Route -->
<xsl:template match="remocra:res_route">
  <b>Route : <xsl:value-of select="remocra:com_nme"/></b> (<xsl:value-of select="remocra:commune"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Gabarit : </u></td><td><xsl:value-of select="remocra:gabarit"/></td></tr>
	<tr><td><u>catégorie : </u></td><td><xsl:value-of select="remocra:categorie"/></td></tr>
	<tr><td><u>numéro début droit : </u></td><td><xsl:value-of select="remocra:adresse_debut_droit"/></td></tr>
	<tr><td><u>numéro fin droit : </u></td><td><xsl:value-of select="remocra:adresse_fin_droit"/></td></tr>
	<tr><td><u>numéro début gauche : </u></td><td><xsl:value-of select="remocra:adresse_debut_gauche"/></td></tr>
	<tr><td><u>numéro fin gauche : </u></td><td><xsl:value-of select="remocra:adresse_fin_gauche"/></td></tr>
	<tr><td><u>autre nom (alias) : </u></td><td><xsl:value-of select="remocra:nom_usuel"/></td></tr>
	<tr><td><u>restriction de hauteur : </u></td><td><xsl:value-of select="remocra:hauteur_maximale"/></td></tr>
	<tr><td><u>restriction de largeur : </u></td><td><xsl:value-of select="remocra:lageur_maximale"/></td></tr>
	<tr><td><u>restriction de tonnage : </u></td><td><xsl:value-of select="remocra:tonnage_maximal"/></td></tr>
  </table>
</xsl:template>

<!-- Route numéro (pour conso) -->
<xsl:template match="remocra:res_route_numero"/>

<!-- ERP -->
<xsl:template match="remocra:erp">
  <b>ERP : <xsl:value-of select="remocra:nom"/></b> (<xsl:value-of select="remocra:commune"/> <xsl:value-of select="remocra:lieu_dit"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Adresse : </u></td><td><xsl:value-of select="remocra:adresse"/></td></tr>
    <tr><td><u>Activité : </u></td><td><xsl:value-of select="remocra:com_nme"/></td></tr>
  </table>
</xsl:template>

<!-- Service public -->
<xsl:template match="remocra:service_public">
  <b>Service public : <xsl:value-of select="remocra:com_nme"/></b> (<xsl:value-of select="remocra:commune"/> <xsl:value-of select="remocra:lieu_dit"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Adresse : </u></td><td><xsl:value-of select="remocra:adresse"/></td></tr>
    <tr><td><u>Activité : </u></td><td><xsl:value-of select="remocra:activite"/></td></tr>
  </table>
</xsl:template>

<!-- Sport plein air -->
<xsl:template match="remocra:sport_plein_air">
  <b>Sport de plein air : <xsl:value-of select="remocra:nom"/></b> (<xsl:value-of select="remocra:commune"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Adresse :  </u></td><td><xsl:value-of select="remocra:adresse"/></td></tr>
    <tr><td><u>Activité : </u></td><td><xsl:value-of select="remocra:activite"/></td></tr>
  </table>
</xsl:template>

<!-- Permis -->
<xsl:template match="remocra:v_permis">
  <b>Permis : <xsl:value-of select="remocra:nom"/></b> - <xsl:value-of select="remocra:numero"/>
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Voie : </u></td><td><xsl:value-of select="remocra:voie"/></td></tr>
    <tr><td><u>Complément : </u></td><td><xsl:value-of select="remocra:complement"/></td></tr>
    <tr><td><u>Observations : </u></td><td><xsl:value-of select="remocra:observations"/></td></tr>
    <tr><td><u>Parcelle cadastrale : </u></td><td><xsl:value-of select="remocra:parcelle_cadastrale"/></td></tr>
    <tr><td><u>Section cadastrale : </u></td><td><xsl:value-of select="remocra:section_cadastrale"/></td></tr>
	<tr><td><u>Année : </u></td><td><xsl:value-of select="remocra:annee"/></td></tr>
  </table>
</xsl:template>

<!-- Camping -->
<xsl:template match="remocra:camping">
  <b>Camping : <xsl:value-of select="remocra:nom"/></b> (<xsl:value-of select="remocra:commune"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Adresse :  </u></td><td><xsl:value-of select="remocra:adresse"/></td></tr>
  </table>
</xsl:template>

<!-- Entreprise -->
<xsl:template match="remocra:entreprise">
  <b>Entreprise : <xsl:value-of select="remocra:nom"/></b> (<xsl:value-of select="remocra:commune"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Adresse :  </u></td><td><xsl:value-of select="remocra:adresse"/></td></tr>
    <tr><td><u>Activité : </u></td><td><xsl:value-of select="remocra:activite"/></td></tr>
  </table>
</xsl:template>

<!-- infra route -->
<xsl:template match="remocra:infra_route">
  <b>Type d'infrastructure : <xsl:value-of select="remocra:sous_type"/></b> (<xsl:value-of select="remocra:commune"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Nom :  </u></td><td><xsl:value-of select="remocra:nom"/></td></tr>
	<tr><td><u>Adresse :  </u></td><td><xsl:value-of select="remocra:adresse"/></td></tr>
  </table>
</xsl:template>

<!-- Parking -->
<xsl:template match="remocra:parking_surf">
  <b>Parking : <xsl:value-of select="remocra:nom"/></b> (<xsl:value-of select="remocra:commune"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
  
  </table>
</xsl:template>

<!-- Pibi hors gestion hydrant -->
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

<!-- Pena hors gestion hydrant -->
<xsl:template match="remocra:v_hydrant_pena">
  <b>Pibi : <xsl:value-of select="remocra:numero"/></b>
  <table style="margin-left:20px;margin-bottom:10px;">
    <tr><td><u>Nature : </u></td><td><xsl:value-of select="remocra:nom"/></td></tr>
    <tr><td><u>HBE : </u></td><td><xsl:value-of select="remocra:hbe"/></td></tr>
    <tr><td><u>Disponibilité HBE : </u></td><td><xsl:value-of select="remocra:dispo_hbe"/></td></tr>
    <tr><td><u>Disponibilité terrestre: </u></td><td><xsl:value-of select="remocra:dispo_terrestre"/></td></tr>
  </table>
</xsl:template>

<!-- lieu de culte -->
<xsl:template match="remocra:lieu_culte">
  <b>Type d'infrastructure : <xsl:value-of select="remocra:nom"/></b> (<xsl:value-of select="remocra:commune"/>)
  <table style="margin-left:20px;margin-bottom:10px;">
	<tr><td><u>Adresse :  </u></td><td><xsl:value-of select="remocra:adresse"/></td></tr>
  </table>
</xsl:template>

</xsl:stylesheet>

<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:template match="historiqueCrise" >
	<html>
    <head>
		<meta harset="utf-8"/>
		<title></title>
		<style type="text/css">
		
		*{
			font-family: Arial,Helvetica Neue,Helvetica,sans-serif; 
		}
		
		body{
			background-color: silver;
		}
		
		table {
			border-collapse: collapse;
			width:100%
		}
		
		th, td{
			font-size: 0.9em;
			border: 1px solid darkgrey;
		}
		
		th {
			background-color: silver;
		}
		
		td {
			text-align: center;
		}
		
		a {
			display: block;
		}
		
		a, p{
			font-size: 0.9em;
			color : black;
		}
		
		a:hover{
			color : red;
		}
		
		h4{
			border-top:2px solid silver;
		}


		#menu {
			height: 100%;
			width: 250px;
			position: fixed;
			z-index: 1;
			top: 0;
			left: 0;
			overflow-x: hidden;
			padding-top: 25px;
			padding-left: 25px;
		
		}
		
		
		#principal{
			margin-left: 250px;
			width: 1000px;
			background-color: white;
			padding-left: 25px;
			padding-right: 25px;
			padding-top: 25px;
			padding-bottom: 25px;
		}
	
		</style>
    </head>
	<body>

	<!-- Table des matières -->
	<div id="menu">
		
		<!-- Informations sur la crise et l'export -->
		<h3><xsl:value-of select="crise/nom"/></h3>
		<p>Historique entre le<br/><b><xsl:value-of select="@dateDebutExport"/> à <xsl:value-of select="@heureDebutExport"/></b><br/>et le<br/><b><xsl:value-of select="@dateFinExport"/> à <xsl:value-of select="@heureFinExport"/></b></p>

		<!-- Dates des messages pour accès rapide--> 
		<xsl:for-each select="messages">
			<xsl:element name="a">
				<xsl:attribute name="href">#<xsl:value-of select="@dateCreation"/>
				</xsl:attribute>
				<xsl:value-of select="@dateCreation"/>
			</xsl:element>
		</xsl:for-each>
	</div>
	
	<!-- Messages -->
	<div id="principal">
		<xsl:apply-templates select="crise"/>
		<xsl:apply-templates select="messages"/>
	</div>

</body>
</html>
</xsl:template>

<xsl:template match="crise">
	<h1>
		<xsl:value-of select="nom"/>
	</h1>
	<h2>
		<xsl:value-of select="description"/>
	</h2>
	<h3>Début et fin d'export</h3>
	<table>
		<tr>
			<th style="width:25%">Date de début</th>
			<td style="width:25%">
				<xsl:value-of select="../@dateDebutExport"/>
			</td>
			<th style="width:25%">Heure de début</th>
			<td style="width:25%">
				<xsl:value-of select="../@heureDebutExport"/>
			</td>
		</tr>
		<tr>
			<th style="width:25%">Date de fin</th>
			<td style="width:25%">
				<xsl:value-of select="../@dateFinExport"/>
			</td>
			<th style="width:25%">Heure de fin</th>
			<td style="width:25%">
				<xsl:value-of select="../@heureFinExport"/>
			</td>
		</tr>
	</table>
	<h3>Activation et clôture de crise</h3>
	<table>
		<tr>
			<th style="width:25%">Date d'activation</th>
			<td style="width:25%">
				<xsl:value-of select="@dateActivation"/>
			</td>
			<th style="width:25%">Heure d'activation</th>
			<td style="width:25%">
				<xsl:value-of select="@heureActivation"/>
			</td>
		</tr>
		<tr>
			<th style="width:25%">Date de clôture</th>
			<td style="width:25%">
				<xsl:value-of select="@dateCloture"/>
			</td>
			<th style="width:25%">Heure de clôture</th>
			<td style="width:25%">
				<xsl:value-of select="@heureCloture"/>
			</td>
		</tr>
	</table>
</xsl:template>

<xsl:template match="messages" >
	<xsl:element name="h4">
		<xsl:attribute name="id">
			<xsl:value-of select="@dateCreation"/>
		</xsl:attribute>
		<xsl:value-of select="@dateCreation"/>
	</xsl:element>

	<table>
		<tr>
			<th style="width:10%">Heure</th>
			<th style="width:25%">Objet</th>
			<th style="width:65%">Message</th>
		</tr>
		<xsl:apply-templates select="message"/>
	</table>
</xsl:template>

<xsl:template match="message" >
	<tr>
		<td style="width:10%">
			<xsl:value-of select="@heureCreation"/>
		</td>
		<td style="width:25%">
			<xsl:value-of select="objet"/>
		</td>
		<td style="width:65%">
			<xsl:choose>
				<xsl:when test = "document">
					<xsl:apply-templates select="document"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="corps/ligne"/>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
</xsl:template>

<xsl:template match="ligne" >
	<xsl:value-of select="."/>
	<br/>
</xsl:template>

<xsl:template match="document" >
	<xsl:choose>
		<xsl:when test = "@type='PHOTO' or @type='CARTE_HORODATEE'">
			<xsl:element name="img">
				<xsl:attribute name="src">
					<xsl:value-of select="@url"/>
				</xsl:attribute>
			</xsl:element>
		</xsl:when>
		<xsl:otherwise>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="@url"/>
				</xsl:attribute>
				<xsl:text>Consulter</xsl:text>
			</xsl:element>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet> 

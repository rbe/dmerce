<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet version = '1.0' 
     xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

	<xsl:template match="document">
		<html>
			<head>
				<title>dmerce 3 Dokumentation gesamt</title>
				<link rel="stylesheet" type="text/css" href="styles.css"/>
			</head>
			<body>
			<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="section">
		<h1 style="text-decoration:underline">
			<xsl:value-of select="title"/>
		</h1>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="content">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:element name="{name()}">
			<xsl:for-each select="@*">
				<xsl:attribute name="{name()}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="dmerce">
		<i>dmerce3</i>&#x000AE;
	</xsl:template>

	<xsl:template match="img">
		<table align="center" style="border: 8px solid white;">
			<tr>
				<td>
					<xsl:element name="img">
						<xsl:attribute name="src">
							<xsl:text>images/</xsl:text>
							<xsl:value-of select="@src"/>
						</xsl:attribute>
					</xsl:element>
				</td>
			</tr>
			<tr>
				<td class="annotation" style="padding:4px;">
					<xsl:value-of select="."/>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="code">
		<table>
			<tr>
				<td class="code">
					<pre>
						<xsl:value-of select="."/>
					</pre>
				</td>
			</tr>
			<tr>
				<td class="annotation" style="padding:4px;">
					<xsl:value-of select="@annotation"/>
				</td>
			</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>

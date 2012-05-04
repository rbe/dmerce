<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet version = '1.0' 
     xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

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
	
	<xsl:template match="c">
		<span style="font-weight:bold; font-family: courier;"><xsl:value-of select="."/></span>
	</xsl:template>

	<xsl:template match="Oracle">
		<nobr>Oracle<sup>&#x000AE;</sup></nobr>
	</xsl:template>
	
	<xsl:template match="UNIX">
		<nobr>UNIX<sup>&#x000AE;</sup></nobr>
	</xsl:template>
	
	<xsl:template match="Windows">
		<nobr>Microsoft<sup>&#x000AE;</sup> Windows<sup>&#x000AE;</sup></nobr>
	</xsl:template>
	
	<xsl:template match="dmerce">
		<nobr>dmerce<sup>&#x000AE;</sup></nobr>
	</xsl:template>
	
	<xsl:template match="appname">&lt;appname&gt;</xsl:template>

	<xsl:template match="deploy">
		<span style="font-weight:bold; font-family: courier;"><xsl:value-of select="."/>deploy &lt;appname&gt;</span>
	</xsl:template>
	
	<xsl:template match="reconfigure">
		<span style="font-weight:bold; font-family: courier;"><xsl:value-of select="."/>reconfigure &lt;appname&gt;</span>
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

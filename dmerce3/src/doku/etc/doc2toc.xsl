<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet version = '1.0' 
     xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

	<xsl:template match="document">
		<ol type="I">
			<xsl:apply-templates select="/document/section"/>
	    </ol>
	</xsl:template>
	
	<xsl:template match="/document/section">
		<li>
			<a href="doc.jsp?id={position()}">
				<xsl:value-of select="title"/>
			</a>
		</li>
		<xsl:if test="count(section)>0">
			<ol type="1">
				<xsl:apply-templates select="section"/>
			</ol>
		</xsl:if>
	</xsl:template>

	<xsl:template match="/document/section/section">
		<li>
			<a href="doc.jsp?id={count(../preceding-sibling::*)+1}.{position()}">
				<xsl:value-of select="title"/>
			</a>
		</li>
	</xsl:template>

</xsl:stylesheet>

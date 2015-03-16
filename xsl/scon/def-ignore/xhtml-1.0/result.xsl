<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="UTF-8" indent="yes" media-type="text/html" method="html"/>

	<xsl:template match="/">
		<xsl:element name="html">
			<xsl:element name="head">
				<xsl:element name="title">data console</xsl:element>
			</xsl:element>
			<xsl:element name="body">
			<div>&lt;<xsl:element name="span"><xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute><xsl:value-of select="local-name(*)" /></xsl:element>&gt;<xsl:for-each select="child::*">
					<xsl:apply-templates />
				</xsl:for-each>&lt;/<xsl:element name="span"><xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute><xsl:value-of select="local-name(*)" /></xsl:element>&gt;</div>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()">
	<xsl:element name="span">
	<xsl:attribute name="style">font-style:italic</xsl:attribute>
		<xsl:value-of  select="." />
		</xsl:element>
	</xsl:template>

	<xsl:template match="*">
		<xsl:element name="div">
			<xsl:attribute name="style">position:relative; left: 20px;</xsl:attribute>
				&lt;<xsl:element name="span">
				<xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute>
			<xsl:value-of select="local-name()" />
		</xsl:element>&gt;
		<xsl:apply-templates select="*|text()"/>&lt;/<xsl:element name="span">
			<xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute><xsl:value-of select="local-name()" /></xsl:element>&gt;
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>


<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:trace="http://www.obqo.de/XSL/Trace" version="1.0">
	<xsl:output encoding="UTF-8" indent="yes" media-type="text/html" method="html"/>

	<xsl:template match="/"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/1')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="/"</xsl:text><xsl:text>)</xsl:text></xsl:message>
		<xsl:element name="html">
			<xsl:element name="head">
				<xsl:element name="title">data console</xsl:element>
			</xsl:element>
			<xsl:element name="body">
			<div>&lt;<xsl:element name="span"><xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute><xsl:value-of select="local-name(*)"/></xsl:element>&gt;<xsl:for-each select="child::*">
					<xsl:apply-templates><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
				</xsl:for-each>&lt;/<xsl:element name="span"><xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute><xsl:value-of select="local-name(*)"/></xsl:element>&gt;</div>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/2')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="text()"</xsl:text><xsl:text>)</xsl:text></xsl:message>
	<xsl:element name="span">
	<xsl:attribute name="style">font-style:italic</xsl:attribute>
		<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="*"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/3')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="*"</xsl:text><xsl:text>)</xsl:text></xsl:message>
		<xsl:element name="div">
		<xsl:attribute name="style">position:relative; left: 20px;</xsl:attribute>
				&lt;<xsl:element name="span"><xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute><xsl:value-of select="local-name()"/></xsl:element>&gt;<xsl:apply-templates select="*|text()"><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>&lt;/<xsl:element name="span">
			<xsl:attribute name="style">color:blue; font-weight:bold</xsl:attribute><xsl:value-of select="local-name()"/></xsl:element>&gt;
		</xsl:element>
	</xsl:template>

<xsl:template xmlns:alias="http://www.w3.org/TransformAlias" name="trace:getPath">
    <xsl:text>node: </xsl:text>
    <xsl:for-each select="ancestor::*">
      <xsl:value-of select="concat('/', name(), '[',             count(preceding-sibling::*[name()=name(current())])+1, ']')"/>
    </xsl:for-each>      
    <xsl:apply-templates select="." mode="trace:getCurrent"/>
  </xsl:template><xsl:template xmlns:alias="http://www.w3.org/TransformAlias" match="*" mode="trace:getCurrent">
    <xsl:value-of select="concat('/', name(), '[',           count(preceding-sibling::*[name()=name(current())])+1, ']')"/>
  </xsl:template><xsl:template xmlns:alias="http://www.w3.org/TransformAlias" match="@*" mode="trace:getCurrent">
    <xsl:value-of select="concat('/@', name())"/>
  </xsl:template><xsl:template xmlns:alias="http://www.w3.org/TransformAlias" match="text()" mode="trace:getCurrent">
    <xsl:value-of select="concat('/text()[', count(preceding-sibling::text())+1, ']')"/>
  </xsl:template><xsl:template xmlns:alias="http://www.w3.org/TransformAlias" match="comment()" mode="trace:getCurrent">
    <xsl:value-of select="concat('/comment()[',                          count(preceding-sibling::comment())+1, ']')"/>
  </xsl:template><xsl:template xmlns:alias="http://www.w3.org/TransformAlias" match="processing-instruction()" mode="trace:getCurrent">
    <xsl:value-of select="concat('/processing-instruction()[',           count(preceding-sibling::processing-instruction())+1, ']')"/>
  </xsl:template><xsl:template match="*" priority="-2">
    <xsl:param xmlns:alias="http://www.w3.org/TransformAlias" name="trace:callstack"/>
    <xsl:message xmlns:alias="http://www.w3.org/TransformAlias">
      <xsl:call-template name="trace:getPath"/>
      <xsl:text>
   default rule applied</xsl:text>
    </xsl:message>
    <xsl:apply-templates xmlns:alias="http://www.w3.org/TransformAlias">
      <xsl:with-param name="trace:callstack" select="$trace:callstack"/>
    </xsl:apply-templates>
  </xsl:template></xsl:stylesheet>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="UTF-8" indent="no" media-type="text/html"
		method="html"/>
		
<xsl:template match="/">
  <html>
    <head>
      <title />
    </head>
    <body>
      <table border="1">
          <xsl:apply-templates select="*" />
      </table>
    </body>
  </html>
</xsl:template>

<xsl:template match="*[not(*)]">
  <tr>
    <td>
        <xsl:value-of select="name()" />
    </td>
    <td>
      <xsl:value-of select="." />
    </td>
  </tr>
</xsl:template>
</xsl:stylesheet>

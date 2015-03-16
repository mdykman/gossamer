<?xml version="1.0" encoding="UTF-8"?><xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:trace="http://www.obqo.de/XSL/Trace" xmlns:alias="http://www.w3.org/TransformAlias" version="1.0" exclude-result-prefixes="alias">
  
  <xsl:namespace-alias stylesheet-prefix="alias" result-prefix="xsl"/>

  

  
  <xsl:template match="xsl:stylesheet | xsl:transform"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/1')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="xsl:stylesheet | xsl:transform"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:copy>
      
      <xsl:copy-of select="document('')/*/namespace::trace"/>
      
      <xsl:copy-of select="namespace::*|@*"/>
      <xsl:apply-templates><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
      
      <xsl:copy-of select="document('')/*/xsl:template                                   [@mode='trace:getCurrent' or                                     @name='trace:getPath']"/>
      
      <xsl:variable name="priority" select="xsl:template/@priority                             [not(. &gt; current()/xsl:template/@priority)]"/><xsl:message>   variable: name="priority" value="<xsl:text/><xsl:value-of select="$priority"/>" </xsl:message>
      <xsl:variable name="newpri">
        <xsl:choose>
          <xsl:when test="$priority &lt; -1">
            <xsl:value-of select="$priority - 1"/>
          </xsl:when>
          
          <xsl:otherwise>-2</xsl:otherwise> 
        </xsl:choose>
      </xsl:variable><xsl:message>   variable: name="newpri" value="<xsl:text/><xsl:value-of select="$newpri"/>" </xsl:message>
      
      <alias:template match="*" priority="{$newpri}">
        <xsl:copy-of select="document('')/*/xsl:template                              [@name='trace:defaultRule']/node()"/>
      </alias:template>
    </xsl:copy>
  </xsl:template>


  
  <xsl:template match="xsl:template"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/2')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="xsl:template"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:apply-templates select="xsl:param"><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
      <alias:param name="trace:callstack"/>
      <xsl:choose>
        <xsl:when test="@name">
          <alias:variable name="trace:current" select="concat($trace:callstack,'/{@name}')"/>
        </xsl:when>
        <xsl:otherwise>
          <alias:variable name="trace:current" select="concat($trace:callstack,                          '/{count(preceding-sibling::xsl:template)+1}')"/>
        </xsl:otherwise>
      </xsl:choose>

      
      <alias:message>
        <alias:call-template name="trace:getPath"/>
        <alias:text>
   stack: </alias:text>
        <alias:value-of select="$trace:current"/>
        <xsl:if test="@match or @mode">
          <alias:text> (</alias:text>
          <xsl:if test="@match">
            <alias:text>match="<xsl:value-of select="@match"/>"</alias:text>
            <xsl:if test="@mode">
              <alias:text><xsl:text> </xsl:text></alias:text>
            </xsl:if>
          </xsl:if>
          <xsl:if test="@mode">
            <alias:text>mode="<xsl:value-of select="@mode"/>"</alias:text>
          </xsl:if>
          <alias:text>)</alias:text>
        </xsl:if>
        <xsl:apply-templates select="xsl:param" mode="traceParams"><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
      </alias:message>

      
      <xsl:apply-templates select="node()[not(self::xsl:param)]"><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
    </xsl:copy>
  </xsl:template>


  
  <xsl:template match="xsl:apply-templates | xsl:call-template"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/3')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="xsl:apply-templates | xsl:call-template"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <alias:with-param name="trace:callstack" select="$trace:current"/>
      <xsl:apply-templates><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
    </xsl:copy>
  </xsl:template>


  
  <xsl:template match="xsl:param" mode="traceParams"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/4')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="xsl:param"</xsl:text><xsl:text> </xsl:text><xsl:text>mode="traceParams"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <alias:text>
   param: name="<xsl:value-of select="@name"/>" value="</alias:text>
    <alias:value-of select="${@name}"/>" <alias:text/>
    
  </xsl:template>

  
  <xsl:template match="xsl:variable"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/5')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="xsl:variable"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
    </xsl:copy>
    <xsl:if test="ancestor::xsl:template">
      <alias:message>   variable: name="<xsl:value-of select="@name"/>" value="<alias:text/>
      <alias:value-of select="${@name}"/>" </alias:message>
    </xsl:if>
  </xsl:template>

  
  <xsl:template match="*|@*"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/6')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="*|@*"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:copy>
      <xsl:apply-templates select="@*"><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
      <xsl:apply-templates><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
    </xsl:copy>
  </xsl:template>


  
  
  

  
  <xsl:template name="trace:getPath"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,'/trace:getPath')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/></xsl:message>
    <xsl:text>node: </xsl:text>
    <xsl:for-each select="ancestor::*">
      <xsl:value-of select="concat('/', name(), '[',             count(preceding-sibling::*[name()=name(current())])+1, ']')"/>
    </xsl:for-each>      
    <xsl:apply-templates select="." mode="trace:getCurrent"><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:apply-templates>
  </xsl:template>


  
  <xsl:template match="*" mode="trace:getCurrent"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/8')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="*"</xsl:text><xsl:text> </xsl:text><xsl:text>mode="trace:getCurrent"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:value-of select="concat('/', name(), '[',           count(preceding-sibling::*[name()=name(current())])+1, ']')"/>
  </xsl:template>

  <xsl:template match="@*" mode="trace:getCurrent"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/9')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="@*"</xsl:text><xsl:text> </xsl:text><xsl:text>mode="trace:getCurrent"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:value-of select="concat('/@', name())"/>
  </xsl:template>

  <xsl:template match="text()" mode="trace:getCurrent"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/10')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="text()"</xsl:text><xsl:text> </xsl:text><xsl:text>mode="trace:getCurrent"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:value-of select="concat('/text()[', count(preceding-sibling::text())+1, ']')"/>
  </xsl:template>

  <xsl:template match="comment()" mode="trace:getCurrent"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/11')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="comment()"</xsl:text><xsl:text> </xsl:text><xsl:text>mode="trace:getCurrent"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:value-of select="concat('/comment()[',                          count(preceding-sibling::comment())+1, ']')"/>
  </xsl:template>

  <xsl:template match="processing-instruction()" mode="trace:getCurrent"><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,                          '/12')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text> (</xsl:text><xsl:text>match="processing-instruction()"</xsl:text><xsl:text> </xsl:text><xsl:text>mode="trace:getCurrent"</xsl:text><xsl:text>)</xsl:text></xsl:message>
    <xsl:value-of select="concat('/processing-instruction()[',           count(preceding-sibling::processing-instruction())+1, ']')"/>
  </xsl:template>


  
  <xsl:template name="trace:defaultRule"><xsl:param name="trace:callstack"/><xsl:param name="trace:callstack"/><xsl:variable name="trace:current" select="concat($trace:callstack,'/trace:defaultRule')"/><xsl:message><xsl:call-template name="trace:getPath"/><xsl:text>
   stack: </xsl:text><xsl:value-of select="$trace:current"/><xsl:text>
   param: name="trace:callstack" value="</xsl:text><xsl:value-of select="$trace:callstack"/>" <xsl:text/></xsl:message>
    
    <xsl:message>
      <xsl:call-template name="trace:getPath"><xsl:with-param name="trace:callstack" select="$trace:current"/></xsl:call-template>
      <xsl:text>
   default rule applied</xsl:text>
    </xsl:message>
    <xsl:apply-templates><xsl:with-param name="trace:callstack" select="$trace:current"/>
      <xsl:with-param name="trace:callstack" select="$trace:callstack"/>
    </xsl:apply-templates>
  </xsl:template>

<xsl:template name="trace:getPath">
    <xsl:text>node: </xsl:text>
    <xsl:for-each select="ancestor::*">
      <xsl:value-of select="concat('/', name(), '[',             count(preceding-sibling::*[name()=name(current())])+1, ']')"/>
    </xsl:for-each>      
    <xsl:apply-templates select="." mode="trace:getCurrent"/>
  </xsl:template><xsl:template match="*" mode="trace:getCurrent">
    <xsl:value-of select="concat('/', name(), '[',           count(preceding-sibling::*[name()=name(current())])+1, ']')"/>
  </xsl:template><xsl:template match="@*" mode="trace:getCurrent">
    <xsl:value-of select="concat('/@', name())"/>
  </xsl:template><xsl:template match="text()" mode="trace:getCurrent">
    <xsl:value-of select="concat('/text()[', count(preceding-sibling::text())+1, ']')"/>
  </xsl:template><xsl:template match="comment()" mode="trace:getCurrent">
    <xsl:value-of select="concat('/comment()[',                          count(preceding-sibling::comment())+1, ']')"/>
  </xsl:template><xsl:template match="processing-instruction()" mode="trace:getCurrent">
    <xsl:value-of select="concat('/processing-instruction()[',           count(preceding-sibling::processing-instruction())+1, ']')"/>
  </xsl:template><xsl:template match="*" priority="-2">
    <xsl:param name="trace:callstack"/>
    <xsl:message>
      <xsl:call-template name="trace:getPath"/>
      <xsl:text>
   default rule applied</xsl:text>
    </xsl:message>
    <xsl:apply-templates>
      <xsl:with-param name="trace:callstack" select="$trace:callstack"/>
    </xsl:apply-templates>
  </xsl:template></xsl:transform>

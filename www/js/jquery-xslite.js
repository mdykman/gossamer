
if(jQuery)( function($) {

   var methods =  {
      ajax: function(url,data,success,error) {
         var opts = this.xslite('parseParams',url,data,success,error);
         var optsuc = opts.success;
         var opterr = opts.error;
         var optcomplete = opts.complete;
         var context = this;

         opts.complete = undefined;
         opts.success = function(data,textStatus, jqxhr) {
            var xslURL;
            var doc = jqxhr.responseXML;
            if(doc) xslURL = $(context).xslite('xslUri',doc);
            else doc = data;
            if(xslURL) {
               $(context).xslite('ajax',{
                  url: xslURL,
                  method: 'GET',
                  complete: function(jqhxr2,tsr) {
                     if(optcomplete) optcomplete(jqhxr2,tsr);
                  },
                  error: function(jqhxr2,tsr,et) {
                     if(opterr) opterr(jqhxr2,tsr,et + ' with ' + xslURL);
                  },
                  success: function(xsl,tsr,jqhxr2) {
                     var tr;
                     if(xsl.documentElement) {
                        tr = $(context).xslite('createTransformer',xsl);
                        if(tr) {
                           var dd = tr(doc);
                           if(dd) {
                              if(optsuc) {
                                 optsuc(dd,tsr,jqhxr2);
                              }
                           } else {
                              if(opterr) opterr(jqhxr2,'error','failed to transform with ' + xslURL);
                           }
                        } else {
                           if(opterr) opterr(jqhxr2,'error','failed to create transformer ' + xslURL);
                        }
                     } else {
                        if(opterr) opterr(jqhxr2,'error','failed to fetch transformer document' + xslURL);
                     }
                  },
                  dataType: 'xml'
               });
            
            } else if(optsuc) {
               if(optsuc) optsuc(doc,textStatus,jqxhr);
               if(optcomplete) optcomplete(jqxhr,textStatus);
            }
         };
         return $.ajax(opts);
      },

      get: function(url,data,success,error) {
         var opts = this.xslite('parseParams',url,data,success,error);
			opts.method = 'GET';
         return this.xslite('ajax',opts);
      },

      post: function(url,data,success,error) {
         var opts = this.xslite('parseParams',url,data,success,error);
			opts.method = 'POST';
         return this.xslite('ajax',opts);
      },

      load: function(url,data,success,error) {
         var opts = this.xslite('parseParams',url,data,success,error);
         var optsuccess = opts.success;
         var context = this;
         opts.success = function(dd,textStatus, jqxhr) {
               if(dd.nodeType) {
                  var d = dd;
                  if(dd.nodeType == 9) {
                     d = dd.documentElement;
                  }
                  context.replaceWith(d);
                  if(optsuccess) optsuccess(dd,textStatus,jqxhr);
               }
         };
         return this.xslite('ajax',opts);
      },

      parseParams: function(url,data,success,error) {
         var opts;
         if($.isPlainObject(url)) {
            opts = url;
         } else if($.isFunction(data)) {
            error = success;
            success = data;
            data = undefined;
         }
         if(!opts) opts = {
            url: url,
            data: data,
            success: success,
            error: error
         }
         return opts;
      },

      createTransformer: function(xslt) {
         // IE
         if("transformNode" in xslt) {
            return function(xml) {
               return xml.transformNode(xslt);
            };
         // DOM 3 browsers
         } else if(typeof XSLTProcessor != "undefined") { 
            return function(xml) {
               var processor = new XSLTProcessor();
               processor.importStylesheet(xslt);
                 return processor.transformToFragment(xml,document);
            };
         } else {
            return function(xml) {
                 alert("XSLT1.0 does not appear to be supported in your browser. XSL transforms might be disabled in your browser settings.");
            }
         }
      },

      xslUri: function(doc) {
         var pattern = /href="([^ \t]*)"/;
         var cn = doc.childNodes;     
         for(var i = 0; i < cn.length; ++i) {
            if(cn[i].nodeType == 7 && cn[i].nodeValue) { // processing instruction
               var rr = cn[i].nodeValue.match(pattern);
               if(rr) return rr[1];
            }
         }
         return;
      }, 
      
      transform: function(xslt,xml) {
         var tr = this.xslite('createTransformer',xslt);
         return tr(xml);
      }

   };


   $.fn.xslite = function(method) {
    if (methods[method]) {
      var mtd = methods[method];
      var args = Array.prototype.slice.call( arguments, 1 );
      return mtd.apply(this,args);
    } else {
      $.error( 'Method ' +  method + ' does not exist on jQuery.xslite' );
    }    
   };

}

)(jQuery);

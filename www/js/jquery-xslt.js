
if(jQuery)( function($) {

	$.fn.xsltAjax = function(opts) {
		$.xslt.ajax(opts);
	};

	$.fn.xsltGet = function(url,data,success,error) {
		$.xslt.get(url,data,success,error);
	};

	$.fn.xsltPost = function(url,data,success,error) {
		$.xslt.post(url,data,success,error);
	};

	$.fn.xsltLoad = function(url,data,success,error) {
		$.xslt.load(this,url,data,success,error);
	};

   $.xslt = {
		ajax: function(opt) {
			var optsuc = opt.success;
			var opterr = opt.error;
			var optcomplete = opt.complete;

			opt.complete = undefined;
			opt.success = function(data,textStatus, jqxhr) {
				var xslURL;
				var doc = jqxhr.responseXML;
				if(doc) xslURL = $.xslt.xslUri(doc);
				else doc = data;
				if(xslURL) {
					$.xslt.ajax({
						url: xslURL,
						method: 'GET',
						complete: function(jqhxr2,tsr) {
							if(optcomplete) optcomplete(jqhxr2,tsr);
						},
						error: function(jqhxr2,tsr,et) {
							if(opterr) opterr(jqhxr2,tsr,et + ' with ' + xslURL);
						},
						success: function(xsl,tsr,jqhxr2) {
							var tr = $.xslt.createTransformer(xsl);
							if(tr) {
								var dd = tr(doc);
								if(dd) {
									if(optsuc) {
										if(dd.nodeType == 9) dd = dd.documentElement;
										optsuc(dd,tsr,jqhxr2);
									}
								} else {
									if(opterr) opterr(jqhxr2,'error','failed to transform with ' + xslURL);
								}
							} else {
								if(opterr) opterr(jqhxr2,'error','failed to create transformer ' + xslURL);
							}
						},
						dataType: 'xml'
					});
				
				} else if(optsuc) {
					if(optsuc) optsuc(doc,textStatus,jqxhr);
					if(optcomplete) optcomplete(jqxhr,textStatus);
				}
			};
			$.ajax(opt);
		},

		get: function(url,data,success,error) {
			if($.isFunction(data)) {
				error = success;
				success = data;
				data = undefined;
			}
			$.xslt.ajax({
				method: 'GET',
				url:url,
				data:data,
				success:success,
				error: error });
		},

		post: function(url,data,success,error) {
			if($.isFunction(data)) {
				error = success;
				success = data;
				data = undefined;
			}
			$.xslt.ajax({
				method: 'POST',
				url:url,
				data:data,
				success:success,
				error: error });
		},

		load: function(el,url,data,success,error) {
			if($.isFunction(data)) {
				error = success;
				success = data;
				data = undefined;
			}
			$.xslt.ajax({
				data: data,
				url: url,
				success: function(dd,textStatus, jqXHR) {
					el = $(el);
					el.replaceWith(dd);
					if(success) success(dd,textStatus,jqXHR);
				},
				error: error,
				dataType: 'xml'
			});
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
        			alert("XSLT1.0 does not appear to be supported in your browser. XSL transforms might be disabled in your browsersettings.  Please send an error report to the Gossamer-Framework Group on Google, including your User-Agent");
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
      }

	};



})(jQuery);


if(jQuery)( function($) {

   $.fn.xslt = {
		ajax: function(opt) {
			var optsuc = opt.success;
			var opterr = opt.error;
			var optcomplete = opt.complete;

			var success = function(data,textStatus, jqxhr) {
				var context = this;
				var doc = jqXHR.responseXML;
				var xslURL = this.xslUri(doc);
				if(xslURL) {
					$.ajax({
						url: xslURL,
						method: 'GET',
						complete: function(jqhxr2,tsr) {
							if(optcomplete) optcomplete(jqhxr2,tsr);
						},
						error: function(jqxhr2,tsr,et) {
							if(opterr) opterr(jqhxr2,tsr,et + ' with ' + xslURL);
						},
						success: function(xsl,tsr,jqhxr2) {
							var tr = context.createTransformer(xsl);
							if(tr) {
								var dd = tr(doc);
								if(dd) {
									if(optsuc) optsuc(dd,tsr,jqxhr2);
								} else {
									if(opterr) opterr(jqhxr2,'error','failed to transform with ' + xslURL);
								}
							} else {
								if(opterr) opterr(jqhxr2,'error','failed to create transformer ' + xslURL);
							}
						},
						dataType: 'xml';
					});
				} else if(optsuc) {
					if(optsuc) optsuc(doc,textStatus,jqXHR);
					if(optcomplete) optcomplete(jqhxr,textStatus);
				}
			};
			opt.dataType = 'xml';
			opt.success = success;
			opt.complete = undefined;
			$.ajax(opt);
		},

		get: function(url,data,success,error) {
			if($.isFunction(data)) {
				error = success;
				success = data;
				data = undefined;
			}
			this.ajax({
				method: 'GET',
				url:url,
				data:data,
				success:success,
				error: error
			});
		},

		post: function(url,data,success,error) {
			if($.isFunction(data)) {
				error = success;
				success = data;
				data = undefined;
			}
			this.ajax({
				method: 'POST',
				url:url,
				data:data,
				success:success,
				error: error
			});
		},

		load: function(url,data,success,error) {
			if($.isFunction(data)) {
				error = success;
				success = data;
				data = undefined;
			}
			ajax({
				data: data,
				url: url,
				success: function(dd,textStatus, jqXHR) {
					$.empty();
					$.append(dd);
					if(success) success(dd,textStatus,jqXHR);
				},
				error: error;
			});
		},

		createTransformer: function(xslt) {
			// IE
		   if("transformNode" in xslt) return function(xml) {
	         return xml.transformNode(xslt);
      	// DOM 3 browsers
			} else if(typeof XSLTProcessor != "undefined") return function(xml) {
	         var processor = new XSLTProcessor();
				processor.importStylesheet(xslt);
  				return processor.transformToFragment(xml,document);
			} else return function(xml) {
        		 alert("XSLT does not appear to be supported in your browser. XSLT transforms might be disabled in your settings.  Please send an error report to the Gossamer-Framework Group on Google, including your User-Agent");
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

	}



})(jQuery);

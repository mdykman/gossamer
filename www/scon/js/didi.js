if(!window.Didi) {
var Didi = new Object();

Didi.debug = false;
Didi.warn = true;

Didi.error = function(msg) { window.alert(msg); };
Didi.trace = function(msg) { if(this.debug) window.alert(msg); };
Didi.warning = function(msg) { if(this.warn || this.debug) window.alert(msg); };

Didi.SUCCESS = 200;
Didi.FAILED_DEPENDENCY = 424;

Didi.params = function (el, attr) {
    if(!attr)  attr = 'jsdata';
    
   var code = el.getAttribute(attr);
   if(code)
   {
      this.trace('evaluating parameters -->> ' + code);
      return window.eval(code);
   }
};

Didi.idParams = function(id,attr) {
    if(!attr)  attr = 'jsdata';
   var el = document.getElementById(id);
   return Didi.params(el,attr);
};

Didi.showParams = function(params) {
   var s ='';
   for(key in params)
   {
      s = s + key + "=" + params[key] + ", ";
   }
   alert(s); 
};



//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////


Didi.Ajax = new Object();
var didiajax = Didi.Ajax;

Didi.Ajax.defaultMethod = 'get';

Didi.Ajax.XSLTcache = new Array();

Didi.Ajax.xslUri = function(doc) {
   var result;
   var c = doc.firstChild;
   if(c.nodeType == 7)
   {
      var v = c.nodeValue;
      var pattern = /href="([^ \t]*)"/;
      var matches = v.match(pattern);
      result = matches[1];
   }
   return result;
};

Didi.Ajax.create_handler = function(url,method,apre,apost,ch) {
   return function(id,params,pre,post)
   {
      var ret = true;
      var element = document.getElementById(id);
      if(ret && (pre)) ret = pre(element,params);
      if(ret && (apre)) ret = apre(element,params);
      
      if(ret)
      {
         var callback = function(status,newELement)
         {
            alert("callback");
            var ret = true;
            if(ret &&(apost)) ret = apost(status,newElement,element);
            if(ret &&(post)) ret = post(status,newElement,element);
            if(ret && newElement)
            {
               if((!status) || (status == 304) || (status >= 200 && status < 300))
               {
                  ret = this.replace_node(id, newElement,ch);
               }
            }
            return ret;
         };
         ret = this.invoke_request(url,params,method,callback);
      }
      return ret;
   };
};

Didi.Ajax.updateAsync = function(id,url,params,ch,callback) {
   var invoker = this.updateHandler(id,ch,null,callback);
   invoker(url,params);
};

Didi.Ajax.success = function(status)
{
// a null status may indicate successful resolution from localcache
// 304, server has confirmed document is unchanged
// anything in the 2XX is considered good
   return ((!status) || (status == 304) || 
      (status >= 200 && (status < 300)));
};

Didi.Ajax.updateHandler = function(id,ch,targetpre,targetpost) {

   return function(url,params,method,callerpre,callerpost) {
      var ret = true;
      if(!method) method= didiajax.defaultMethod;

      if(ret &&(callerpre)) ret = callerpre(params,element);
      if(ret &&(targetpre)) ret = targetpre(params,element);
      if(ret)
      {
         var callback = function(status,newElement)
         {
            var ret = true;
            var element = document.getElementById(id);
            if(ret &&(callerpost)) {
               ret = callerpost(status,newElement,element);
            }
            
            if(ret &&(targetpost)) {
               ret = targetpost(status,newElement,element);
            }
            
            if(ret && newElement)
            {
               if(didiajax.success(status))
               {
                  var tr = didiajax.transformer(newElement);
                  if(tr)
                  {
                     var ll = tr(newElement);
                     ret = didiajax.replace_node(id, ll,ch);
                  }
               }
               else
               {
                  ret  = false;
               }
            }
            return ret;
         };

         ret = didiajax.invoke_request(url,params,method,callback);
      }
      return ret;
   };
};

Didi.Ajax.transformer = function(uri) {
// test that it is a document
// if(uri && uri.getElementsById)
// alert(typeof uri);
   if(typeof uri == "object" )
   {
      uri = this.xslUri(uri); 
      if(uri) {
         return this.createTransformer(uri);
      }
   }
   return function(xml) { return xml; };
};

Didi.Ajax.createTransformer = function(uri) {

   xslt = this.loadXML(uri);
//    alert("getting tranfromer");

   if(!xslt)
   {
      return function(xml) {
         alert("failed to load stylesheet.");
      };
   }
   else if("transformNode" in xslt) // IE
   {
      
   //    alert("ie tranfromer fetched");
      return function(xml) {
   //    alert("ie tranfromer called");
         return xml.transformNode(xslt);
      };
   }
   else if(typeof XSLTProcessor != "undefined") // DOM 3 browsers
   {
      return function(xml) {
         var processor = new XSLTProcessor();
         processor.importStylesheet(xslt);
         return processor.transformToFragment(xml,document);
      };
   }
   else
   {
      return function(xml) {
         alert("XSLT does not appear to be supported in your browser.");
      };
   }
};

Didi.Ajax.createDocument = function() {
   if(document.implementation && 
      document.implementation.createDocument) {
      return document.implementation.createDocument("","",null);
   }
   else if(window.ActiveXObject) {
      return new ActiveXObject("MSXML2.DOMDocument");
   }
   else {
      return null;
   }
};

/// brute force synchronous document loader
Didi.Ajax.loadXML = function(url) {
   var xt = this.createXMLHTTPObject();
   xt.open('GET',url,false);
   xt.send(null);
   return xt.responseXML;
   /*
   var doc = this.createDocument();
   if(doc)
   {
      doc.async = false;
      doc.load(url);
   }
   return doc;
   */
};

Didi.Ajax.setRotation = function(id,urls,interval,callback) {

   var index = 0;
   var runner = function() {
      var dd = urls[index];
      var pp = {};
      didiajax.updateAsync(id,dd,pp,true, function(status,doc) {
         var ret = true;
         if(callback) ret = callback(status,doc);
        return ret;
      });
      index = (index+1) % urls.length;
   };
   runner();
   return window.setInterval(runner,interval);
};

// utility functions
Didi.Ajax.show_request = function(msg,uri,params,method) {
   var s = "(" + method + ") " + uri ;
   s = s + "\n";
   for(var key in params)
   {
      s = s + key + "=" + params[key] + ", ";
   }
   Didi.error(msg + ": " + s); 
// window.alert(msg + ": " + s); 
};

// default implementation
// these function should be overriden to bind to you favorite jaxa implementation
// A prototype implementation is provided in didi-prototype.js
// which must be included after this file

Didi.Ajax.invoke_request = function(url,params,mtd,callback) {
   if(Didi.debug || Didi.warn)
   {
      this.show_request('no invoker has been defined',
         url,params,mtd);
   }
   // call the callback anyhow with an approriate status code and no document
   callback(Didi.FAILED_DEPENDENCY,null);
};

// this works perfectly for firefox, excuting embedded javascript if present
// it like needs works under IE
Didi.Ajax.replace_node = function(id,el,ch) {
   if(typeof id == 'string') {
      if(ch) {
         $('#' + id).html(el);
      } else {
         $('#' + id).replaceWith(el);
      }
   } else {
      if(ch) {
         $(id).html(el);
      } else {
         $(id).replaceWith(el);
      }
   }
   return;
};

Didi.Ajax.update = function(id,url,async) {
   var data;
   if(async)
   {
      var params = [];
      didajax.invoke_request(url,params,null,
         function(status,doc) {
            if(didiajax.success(status))
            {
               didiajax.updateXML(id,doc,true);
            }
            else
            {
               Didi.warning('failed to load content');
            }
         });
   }
   else
   {
      data = this.loadXML(url);
   
      if(data)
      {
         didiajax.updateXML(id,data,true);
      }
   }
   return data;
};

Didi.Ajax.updateXML = function(id,data,ch) {
   var tr = this.transformer(data);
   this.replace_node(id,tr(data),ch);
   return true;
};


Didi.Ajax.updateContentCallback = function(id,url,params,callback) {
   this.request(url,params, function(xml) {
         Didi.Ajax.updateXML(id,xml,true);   
         callback();
      }, true);
};

Didi.Ajax.fetchContent = function(url,params,callback) {
   this.request(url,params, function(xml) {
         var tr = this.transformer(xml);
         callback(tr(xml));
    },true);
};

Didi.Ajax.updateContent = function(id,url,params,post) {
   this.request(url,params, function(xml) {
         Didi.Ajax.updateXML(id,xml,true);   
      }, post);
};

Didi.Ajax.requestJSON = function(uri, params,callback,post) {
   return this.request(uri,params,callback,post,true);
};

Didi.Ajax.request = function(uri, params,callback,post, json) {
   var req = this.createXMLHTTPObject();
   var args = this.buildArgString(params);

   var cb = function(data) { 
      callback(data);
   };
   
   if(json) {
      if(post) {
         $.post(uri,params,cb,'json');
      } else {
         $.getJSON(uri,params,cb);
      }
   } else {
      if(post) {
         $.post(uri,params,cb);
      } else {
         $.get(uri,params,cb);
      }
   }
   return true;

};


Didi.Ajax.createXMLHTTPObject = function() {
   var xmlobj;
   if(window.XMLHttpRequest) xmlobj = new XMLHttpRequest(); // dom3 browser
   if((!xmlobj) && window.ActiveXObject) { // IE
      try {
         xmlobj = new ActiveXObject("Msxml2.XMLHTTP");
      } catch(E) {
         if(!xmlobj) xmlobj = new ActiveXObject("Microsoft.XMLHTTP");
      }
   }
   if(!xmlobj)
   {
      alert("Ajax Failure");
   }
   return xmlobj;
};

Didi.Ajax.buildArgString = function(params) {
   var uri = '';
   if(params) {
      var first = true;
      for(var k in params) {
         if(first) first = false;
         else uri = uri + '&';
         uri = uri + escape(k) + '=' + escape(params[k]);
      }
   }
   return uri;
};

}

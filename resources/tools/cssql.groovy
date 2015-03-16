#!/usr/bin/groovy

classes = [:]
csslint = "csslint"
cc = [];

args.each({ arg ->
	execstr = csslint  + " " + arg
//	pp = arg.split("[/]") as List
//	page = pp.last()
	page = arg
	if(page.endsWith(".css")) {
	   page = page.substring(0,page.length()-4);
	}
	
	execstr.execute().getInputStream().eachLine({ line ->
	      if(line =~ /^[*.@a-z#].*/) {
	         defclasses(line)
	      } else if(line =~ /^\w*$/) {
	      } else if(line =~ /^\w*[}]\w*$/) {
	      } else {
	         defattr(line)
	      }
	});
	
	println "delete from css_page where css_page_name = '" + page + "';"
	println "insert into css_page (css_page_name) values('" + page + "');"
	println "select last_insert_id() into @pageid;"
	
	classes.each({ n,v ->
	   println("insert into css_section (css_page_id, css_section_name) ")
	   println("  values(@pageid,'" + n + "');")
	   println("select last_insert_id() into @sectionid;")
	   v.each({ vn, vv ->
	      println("insert into css_attribute (css_section_id, css_attribute_name, css_attribute_value) ")
	      println("  values(@sectionid,'" + vn + "','" + vv + "');")
	   })
	})
	classes = [:]
})

def defattr(input) {
   ll = input.split(":",2) as List
   n = ll[0].trim()
   v = ll[1] ? ll[1].trim() : ""
   if(v.endsWith(";")) { v = v.substring(0,v.length()-1); }
   cc.each( { classes[it][n] = v })
}

def defclasses(input) {
   def ll = input.split() as List;
   ll.pop();
   cc = []
   ll.each({
      s = it.trim();
      if(s.endsWith(",")) { s = s.substring(0,s.length()-1); }
      m = classes[s] ? classes[s] : (classes[s] = [:])
      cc << s
   })
}

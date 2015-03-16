var mloadCalled=false;
var mtransCalled=false;
var msaveCalled=false;
var runCalled=false;
var newCalled=false;
var listCalled=false;
var saveCalled=false;
var loaderCalled=false;
var archCalled=false;
var deleteCalled=false;

var sconpath = '/rq/scon/scon';
var jsonpath = '/json/scon/scon';
var filepath = '/rq/scon/filelist';

	var syntax_mapping = {
		bsh:"java",
		java:"java",
		php:"php",
		pl:"perl",
		ruby:"ruby",
		rb:"ruby",
		groovy:"groovy",
		js:"js",
		scm:null,
		py:"python",
		jy:"python",
		python:"python",
		css:"css",
		html:"html",
		map:null
	};

	function getTextFromElements(el)
	{
	   if(el) for(var i = 0 ; i < el.length; ++i)
	   {
	    	var cs = el[i].childNodes;
	    	var msg = '';
	    	for(var j = 0; j < cs.length; ++j)
	    	{
	    		msg = msg + cs[j].nodeValue;
	    	}
	 	 	return msg;
	  	}
		return null; 
	}

	function getTextContent(el,tagname) {
	  var ell = el.getElementsByTagName(tagname);
	    if(ell.length > 0)
	      return getTextFromElements(ell);
	     else
	     	return null;
	}

	
	function invoke(url,onr) {
		$.xslite('get', function() {
			onr(rq);
		});
	}

	function checkMappings(frm) {
		
		var qs = sconpath
			+ '?mloadb=' + 'y';
		if(mloadCalled) {
			mloadCalled = false;
			invoke(qs,function(req) {
				frm.config.value = getTextContent(req.responseXML,"contents");
			});
			
		} else if(msaveCalled) {
			msaveCalled = false;
			var frm = document.getElementById('mapfrm');
			var disp = document.getElementById('mdisp');
			var sink = document.getElementById("sink");

			frm.target = "sink";
			if(frm.msaveb.checked && !confirm("Updating gossamer's runtime rules.\nAre you sure?")) {
				frm.msaveb.checked = false;
				return false;
			}
			
			sink.onload = function() {
				frm.msaveb.checked = false;
				updateElement(sink.contentDocument.documentElement,disp);
			}
			return true;
		}
		else if(mtransCalled) {
			mtransCalled = false;
			var frm = document.getElementById('mapfrm');
			var disp = document.getElementById('mdisp');
			frm.target = "sink";
			var sink = document.getElementById("sink");
			sink.onload = function() {
				updateElement(sink.contentDocument.documentElement,disp);
			}
			return true;
		}

		return false;
	}

	function showEditor() {
		var ed = document.getElementById('scripteditor');
		var map = document.getElementById('mappingeditor');
		ed.style.visibility = 'visible';
		map.style.visibility = 'hidden';

		$('#mappingselect').addClass('tabblur').removeClass('tabfocus');
		$('#editorselect').addClass('tabfocus').removeClass('tabblur');
	}

	function showMappings() {
		var ed = document.getElementById('scripteditor');
		var map = document.getElementById('mappingeditor');
		ed.style.visibility = 'hidden';
		map.style.visibility = 'visible';

		$('#editorselect').addClass('tabblur').removeClass('tabfocus');
		$('#mappingselect').addClass('tabfocus').removeClass('tabblur');
	}

	function submitForm(frm) {
		if(checkSubmission(frm)) {
			frm.submit();
		}
		return false;
	}

	function newScript(skel) {
		var name = prompt('full path name: ',skel);
		if(name && editAreaLoader.getFile('editpane', name)) {
			alert("editor already opened for " + name);
		} else if(name && (name != skel)) {
			var args = {
				'id':name
			};
			var n = name.lastIndexOf('.');
			if(n != -1) {
				var dx = name.substring(n+1);
				var syntax = syntax_mapping[dx];
				if(syntax) args.syntax = syntax
			}
			editAreaLoader.openFile('editpane', args)
		}
	}

/*
	function runDexter(skel) {
		var go = confirm('dexter ' + skel);
		var name = skel;
		if(go) {
			$.post(jsonpath,{
				'dexter': true,
				'file': name
			},function(result) {
				if(result.status == 0 && result.stderr.length == 0) {
					alert("dexter succeeded");
					var n = name.lastIndexOf('/');
					var dir = name.substring(0,n);
					loadDirectory(dir,$('#dir' + dir.replace(/[\/]/g,'_')),true);	
				} else {
					alert("error: " + result.stdout.join("\n")
						+ "\n" + result.stderr.join("\n"));
				}
			},'json');
		}
	}
*/
	function newFolder(skel) {
		var name = prompt('folder name: ',skel);
		if(name && (name != skel)) {
			$.post(jsonpath,{
				'newfolder': name
			},function(result) {
				var n = name.lastIndexOf('/');
				var dir = name.substring(0,n);
				loadDirectory(dir,$('#dir' + dir.replace(/[\/]/g,'_')),true);	
			},'json');
		}
	}

	function checkSubmission(frm) {
		if (newCalled) {
			newCalled=false;
			newScript('/handlers/groovy/Untitled.groovy');
			return false;
		}
		else if (saveCalled) {
			saveCalled = false;

			var ff = editAreaLoader.getCurrentFile('editpane');
			if(ff) {
				saveScript(ff.id,ff.text,
					function(result,st) {
						editAreaLoader.setFileEditedMode('editpane',ff.id,false);
						var n = ff.id.lastIndexOf('/');
						var dir = ff.id.substring(0,n);
						loadDirectory(dir,$('#dir' + dir.replace(/[\/]/g,'_')),true);	
					});
			}
			return false;
		}
		else if (runCalled) {
			runCalled = false;

			var ff = editAreaLoader.getCurrentFile('editpane');
			var fname = ff.id;
			var ext = fname.substring(fname.lastIndexOf('.')+1);
			var args = {
					'runb': true,
					'args': frm.args.value,
					'type': ext,
					'script': ff.text
				};
			if(frm.forceddata.checked) args.forceddata = 'y';
			if(frm.forcedjson.checked) args.forcedjson = 'y';


			// HERE is the problem boy`
			var wn = window.open('',
					'codewindow','toolbar=n,status=n,scrollbars=auto',true);
			$(wn.document.documentElement).xslite('load',sconpath,args, function() {
					wn.focus();
				});
			return false;
		}
		else if (listCalled) {
			listCalled = false;
//n			listFiles(frm.type.value);
			return false;
		}
		else if (archCalled) {
			archCalled = false;
			callSideBar(frm);
		}
		return false;
	}

	function updateElement(el, targ) {
		if(targ.firstChild) {
			targ.replaceChild(el,targ.firstChild);
		} 
		else {
			targ.appendChild(el);
		}
	}

	function updateSidebar(el) {
		var sidebar = document.getElementById('sidebar');
		var sink = document.getElementById('sink');
		if(sidebar.firstChild) {
			sidebar.replaceChild(el,sidebar.firstChild);
		} 
		else {
			sidebar.appendChild(sink.contentDocument.documentElement);
		}
	}
	
	function callSideBar(frm) {
		var sink = document.getElementById('sink');
		sink.onload = function() {
			updateSidebar(sink.contentDocument.documentElement);
		}  
		frm.target = "sink";
	}
	
	function loadArchiveScript(name) {
		var ss = name.split(/[.]/);
//		var frm = document.getElementById("ctlfrm");
//		frm.file.value = ss[0];

		var qs = sconpath;
		var args = { 
			'larchb':'y',
			'type': ss[1] ,
			'file': name
			} 

		loadS(qs,args);
		
		return false;	
	}
	
	function loadScript(name) {
		var ss = name.split(/[.]/);
		var qs = sconpath;
		var args = { 
			'loadb':'load',
			'type': ss[1] ,
			'file': name
			}; 

		loadS(qs,args);
		return false;	
	}

   function loadDirectory(path,el,doit) {
		var pp = path;
		if(pp.lastIndexOf('/') != pp.length-1) { pp = pp + '/'; } 

      var li = $(el);
      li.children('ul').remove();
      if(doit ||  !li.hasClass('expanded')) {
			if(li.hasClass('folder')) {
  		      li.addClass('wait');
			}
         $.post(filepath, {
            dir: pp
         },function(data) {
            var nodes = $(data).appendTo(li).find('span');
				if(li.hasClass('wait')) {
  	     			li.removeClass('wait');
  	        		li.addClass('expanded');
				}

				nodes.filter('.folder').contextMenu({
						menu: 'folderMenu'
					}, function (action,l,pos) {
						var lp = pp + $(l).text();
						var pnt = $(l).parent();
						var n = lp.lastIndexOf('/');
						var dp = lp.substring(0,n);
						switch(action) {
							case 'open':
								if(!pnt.hasClass('expanded')) {
									loadDirectory(lp,pnt);	
								}
							break;
							case 'close':
	     							pnt.children('ul').remove();
					         	pnt.removeClass('expanded');
							break;
							case 'newfile':
								newScript(lp + '/untitled');
							break;
							case 'delete':
								deleteFolder(lp, function(result) {
										loadDirectory(dp,$('#dir' + dp.replace(/[\/]/g,'_')),true);	
									});
							break;
							case 'newfolder':
								newFolder(lp + '/untitled');
							break;
							case 'cancel': break;
							default:
								alert(action + ': ' + pos.x + ',' + pos.y);
						}
						return false;
					});
				nodes.filter('.file').contextMenu({
						menu: 'fileMenu'
					}, function (action,l,pos) {
						var pnt = $(l).parent();
						var lp = pp + $(l).text();
						var n = lp.lastIndexOf('/');
						var dp = lp.substring(0,n);

						switch(action) {
							case 'duplicate':
								var nn = prompt("duplicate name",lp);
								if(nn && nn != lp) {
									$.post(sconpath, {
											loadb: 'y',
											file: lp
										}, function(result) {
											$.post(jsonpath,{
													saveb:'y',
													file: nn,
													script: result
												}, function(r) {
													var n = nn.lastIndexOf('/');
													var dir = nn.substring(0,n);
													loadDirectory(dir,$('#dir' + dir.replace(/[\/]/g,'_')),true);	
												},'json');
										}, 'text');
									}
							break;
							case 'open':
								loadScript(lp);
							break;
//							case 'dexter':
//								runDexter(lp);
//							break;
							case 'delete':
								deleteScript(lp, function(result) {
										loadDirectory(dp,$(l).closest('.folder'),true);	
									});
							break;
							case 'rename':
								renameScript(lp, function(result){
//									alert(JSON.stringify(result));
									if(result.status == 'success') {
										var n = lp.lastIndexOf('/');
										var dir = lp.substring(0,n);
										loadDirectory(dir,$('#dir' + dir.replace(/[\/]/g,'_')),true);	
									}
								});
							break;
							case 'newfile':
								newScript(dp + '/untitled');
							break;
							case 'cancel': break;
							default:
								alert(action + ': ' + lp + pos.x + ',' + pos.y);
						}
						return false;
					});
           },'text');
      } else if(li.hasClass('expanded')) {
         li.removeClass('expanded');
      }
      return false;
   }
	function deleteFolder(name,callback) {
		if(confirm('Are you sure you want to delete folder ' + name + '?')) {
			$.post(jsonpath, {
					delfolder: 'y',
					file: name
				}, function(result) {
					editAreaLoader.closeFile('editpane',name);
					if(callback) callback(result);
				},'json');
		}
		return false;	
	}
	function renameScript(name,callback) {
		var n = prompt('rename ' + name + ' to',name);
		if(n && n != name) {
			$.post(jsonpath, {
					renamefile: 'y',
					filefrom: name,
					fileto: n
				}, function(result) {
					if(result.status == 'success') {
						var ff = editAreaLoader.getFile('editpane',name);
						if(ff) {
							editAreaLoader.closeFile('editpane',name)
							loadScript(n);
						}
						if(callback) callback(result);
					}
				},'json');	
		}
	}
	function deleteScript(name,callback) {

		if(confirm('Are you sure you want to delete ' + name + '?')) {
			$.post(jsonpath, {
					delb: 'y',
					file: name
				}, function(result) {
					editAreaLoader.closeFile('editpane',name);
//					alert(result.filename + ': ' + result.status);
					if(callback) callback(result);
				},'json');
		}
		return false;	
	}

	function loadS(url,args) {
		var name = args.file;
		var load = true;
		if(editAreaLoader.getFile('editpane', name)){
			load = confirm('replace editor text with copy from server?');
		}
		if(load) $.post(sconpath, args, function(result) {
				var params = {
					id: name,
					text: result
				}
				var syntax = syntax_mapping[args.type];
				if(syntax) params.syntax = syntax;
				editAreaLoader.openFile('editpane', params);
			});
	}
	function saveScript(fname,text,cbs,cbf) {
			if(confirm('save the current script as ' + fname + '?')) {
				$.ajax({
				   type: 'POST',
				   url: jsonpath,
					dataType: 'json',
				   data: {
						'saveb': 'true',
						'file': fname,
						'script':	text
						},
					/*
					complete: function(xhr,st) {
							alert(st);
					 	},
					*/
					success: function(result,st){
							if(cbs) cbs(result,st);
					   },
					error: function(xhr,msg,ex){
							if(cbf) cbf(xhr,msg,ex);
					      alert( "error saving " + fname + ": " + msg );
					   }
			   });
										
			}
	}

	function listBlur(el) {
		el.setAttribute('class','listblur');
		el.setAttribute('className','listblur');
	}
	function listFocus(el) {
		el.setAttribute('class','listfocus');
		el.setAttribute('className','listfocus');
	}
	
	function listRowBlur(el) {
		el.setAttribute('class','listrowblur');
		el.setAttribute('className','listrowblur');
	}
	function listRowFocus(el) {
		el.setAttribute('class','listrowfocus');
		el.setAttribute('className','listrowfocus');
	}
	


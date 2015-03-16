editAreaLoader.load_syntax["java"] = {
'COMMENT_SINGLE': { 1: '//', 2: '@' }
	, 'COMMENT_MULTI': { '/*': '*/' }
	, 'QUOTEMARKS': { 1: "'", 2: '"' }
	, 'KEYWORD_CASE_SENSITIVE': true
	, 'KEYWORDS': {
	    'constants': [
			'null', 'false', 'true'
		]
		, 'primatives': [
			'int', 'short', 'long', 'char', 'double', 'byte',
			'float', 'void',  'boolean' 
		]
		, 'types': [
			'String', 'Object','String','List','ArrayList','Map','HashMap','Exception','Number','Arrays',
			'InputStream','OutputStream','Writer','Reader',
			'Integer','Long','Short', 'Float','Double','BigInteger','BigDecimal','Byte','Boolean','Character','Byte',
			'StringBuffer','StringBuilder','URL','URI',
			'Set','SortedMap','SortedSet','Date','Collections','Calendar',
			'Properties','StringTokenizer','Random',
			'ClassLoader','Class','Package','Process','Runtime','System','Thread','Runnable'
		]
		, 'statements': [
            'this', 'extends', 'if', 'do', 'while', 'try', 'catch', 'finally',
            'throw', 'throws', 'else', 'for', 'switch', 'continue', 'implements',
            'break', 'case', 'default', 'goto'
		]
 		, 'keywords': [
           'new', 'return', 'import', 'native', 'super', 'package', 'assert', 'synchronized',
           'instanceof', 'strictfp',
				'static','protected','private',
				'public', 'const', 'class', 'final', 'abstract', 'volatile',
				'enum', 'transient', 'interface',
				'as', 'in', 'with', 'def', 'null'
		]
		,'gdk' : [
			'true','false',
			'sort','each','eachWithIndex','eachLine','collect', 
			'new', 'print','println'
		]
		,'gossamer' : [
			'args','factory','hsf','session','vm','rp','reqp','styler','writer','ctl'
		]
	}
	, 'OPERATORS': [
		'+', '-', '/', '*', '=', '<', '>', '%', '!', '?', ':', '&'
	]
	, 'DELIMITERS': [
		'(', ')', '[', ']', '{', '}'
	]
	, 'REGEXPS': {
	    'precompiler': {
	        'search': '()(#[^\r\n]*)()'
			, 'class': 'precompiler'
			, 'modifiers': 'g'
			, 'execute': 'before'
	    }
	}
	, 'STYLES': {
	    'COMMENTS': 'color: #AAAAAA;'
		, 'QUOTESMARKS': 'color: #6381F8;'
		, 'KEYWORDS': {
		    'constants': 'color: #EE0000;'
			, 'primatives': 'color: #0077EE;'
			, 'types': 'color: #0000EE;'
			, 'statements': 'color: #60CA00;'
			, 'keywords': 'color: #48BDDF;'
			,'gdk' : 'color: #009900;'
			,'gossamer' : 'color: #0099AA;'
		}
		, 'OPERATORS': 'color: #FF00FF;'
		, 'DELIMITERS': 'color: #0038E1;'
		, 'REGEXPS': {
		    'precompiler': 'color: #009900;'
			, 'precompilerstring': 'color: #994400;'
		}
	}
};


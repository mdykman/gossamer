/**
 * Groovy syntax v 1.0 
 * 
 * v1.0 by Michael Dykman (2009/08/19)
 *   
**/
editAreaLoader.load_syntax["groovy"] = {
	'COMMENT_SINGLE' : {1 : '//'}
	,'COMMENT_MULTI' : { '/*' : '*/' }
	,'QUOTEMARKS' : {1: "'", 2: '"', 3: '`'}
	,'KEYWORD_CASE_SENSITIVE' : true
	,'KEYWORDS' : {
		'reserved' : [

			'abstract',    'default',    'if',            'private',      'this',
			'boolean',     'do',         'implements',    'protected',    'throw',
			'break',       'import',     'public',       'throws',
			'byte',        'else',       'instanceof',    'return',       'transient',
			'case',        'extends',    'try',
			'catch',       'final',      'interface',     'static',       'void',
			'char',        'finally',    'strictfp',     'volatile',
			'class',       'native',     'super',        'while',
			'const',       'for',        'new',           'switch',
			'continue',    'goto',       'package',       'synchronized',

			'as',				'in', 'with', 'def'

		]
		,'primatives' : [
			'int','float','double','long','short','char'
		]
		,'constants' : [
			'null','true','false'
		]
		,'gossamer' : [
			'args','factory','hsf','session','vm','rp','reqp','styler','writer','ctl'
		]
		,'builtins' : [
			'sort','each','eachWithIndex','eachLine','collect', 
			'new', 'print','println'
		]
		,'types' : [
			'Object','String','List','ArrayList','Map','HashMap','Exception','Number','Arrays',
			'InputStream','OutputStream','Writer','Reader',
			'Integer','Long','Short', 'Float','Double','BigInt','Byte','Boolean','Character','Byte',
			'StringBuffer','StringBuilder','URL','URI',
			'Set','SortedMap','SortedSet','Date','Collections','Calendar',
			'Properties','StringTokenizer','Random',
			'ClassLoader','Class','Package','Process','Runtime'
		]
	}
	,'OPERATORS' :[
		'+', '~', '-', '/', '*', '=', '<', '>', '%', '!', '&', ';', '?', ':', ','
	]
	,'DELIMITERS' :[
		'(', ')', '[', ']', '{', '}'
	]
	,'STYLES' : {
		'COMMENTS': 'color: #333333;'
		,'QUOTESMARKS': 'color: #660066;'
		,'KEYWORDS' : {
			'reserved' : 'color: #0000AA; font-weight: bold;'
			,'constants' : 'color: #AA0000;'
			,'gossamer' : 'color: #0099AA; font-weight: bold;'
			,'builtins' : 'color: #009900;'
			,'primatives' : 'color: #00AA33; font-weight: bold;'
			,'types' : 'color: #00CC77; font-weight: bold;'
			}
		,'OPERATORS' : 'color: #993300;'
		,'DELIMITERS' : 'color: #990033;'
	}
};

package org.dykman.gossamer.script;

import athena.engine.AthenaScriptFactory;
import athena.engine.interpreter.SmallByteArray;
import athena.engine.interpreter.SmallInt;
import athena.engine.interpreter.SmallJavaObject;


public class AthenaScriptAdapter extends SimpleScriptAdapter
{

	private String imagePath;
	
	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}
	@Override
	public void setScriptEngine(String ext)
	{
		AthenaScriptFactory factory= new AthenaScriptFactory();
		factory.setImageName(imagePath);
		engine = factory.getScriptEngine();
	}
	
	@Override
	public Object toJava(Object o)
	{
		if(o == null) return null;
		Object result = null;

		if(o instanceof SmallJavaObject) {
			SmallJavaObject si = (SmallJavaObject)o;
			result = si.value;
		}
		else if(o instanceof SmallInt) {
			SmallInt si = (SmallInt)o;
			result = si.toJavaInteger();
		}
		else if(o instanceof SmallByteArray) {
			result = o.toString();
		}
		else if(o.getClass().getPackage().getName().startsWith("athena"))
		{
System.out.println("WARNING!!: not converting " + o.getClass().getName());			
			result = o;
		}
		else
		{
			result = o;
		}
		return result;
	}

}

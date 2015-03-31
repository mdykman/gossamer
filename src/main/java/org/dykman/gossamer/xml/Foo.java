/**
 * 
 */
package org.dykman.gossamer.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Foo //implements java.io.Serializable
{
	static final long serialVersionUID = 5346346343152345L;
	
	private String foo;
	private String bar;
	private String[] array;
	private List<Integer> ints = new ArrayList<Integer>();
	Map<String, String> things = new HashMap<String, String>();
	
	
	public void set(String n, String v)
	{
		things.put(n, v);
	}
	
	public Map<String, String> getThings()
    {
    	return things;
    }
	public void setThings(Map<String, String> things)
    {
    	this.things = things;
    }
	public String[] getArray()
    {
    	return array;
    }
	public void setArray(String[] array)
    {
    	this.array = array;
    }
	public String getFoo()
    {
    	return foo;
    }
	public void setFoo(String foo)
    {
    	this.foo = foo;
    }
	public String getBar()
    {
    	return bar;
    }
	public void setBar(String bar)
    {
    	this.bar = bar;
    }
	public void addINt(int n)
	{
		ints.add(n);
	}
	public List<Integer> getInts()
    {
    	return ints;
    }
	public void setInts(List<Integer> ints)
    {
    	this.ints = ints;
    }
	
}
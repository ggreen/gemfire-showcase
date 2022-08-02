package com.vmware.data.services.apache.geode.demo;

import java.io.Serializable;
import java.util.*;


public class ComplexObject implements Serializable, Cloneable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1456118896321582374L;
	private SimpleObject simpleObject;
	private ComplexObject[] complexArray;
	private ArrayList<ComplexObject> complexArrayList;
	private List<ComplexObject> complexList;
	private Collection<ComplexObject> complexColleciton;;
	private ComplexObject complexObject;
	private Map<String,String> map;

	@Override
	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}

	/**
	 * @return the simpleObject
	 */
	public SimpleObject getSimpleObject()
	{
		return simpleObject;
	}
	/**
	 * @param simpleObject the simpleObject to set
	 */
	public void setSimpleObject(SimpleObject simpleObject)
	{
		this.simpleObject = simpleObject;
	}
	/**
	 * @return the complexArray
	 */
	public ComplexObject[] getComplexArray()
	{
		return complexArray;
	}
	/**
	 * @param complexArray the complexArray to set
	 */
	public void setComplexArray(ComplexObject[] complexArray)
	{
		this.complexArray = complexArray;
	}
	/**
	 * @return the complexArrayList
	 */
	public ArrayList<ComplexObject> getComplexArrayList()
	{
		return complexArrayList;
	}
	/**
	 * @param complexArrayList the complexArrayList to set
	 */
	public void setComplexArrayList(ArrayList<ComplexObject> complexArrayList)
	{
		this.complexArrayList = complexArrayList;
	}
	/**
	 * @return the complexList
	 */
	public List<ComplexObject> getComplexList()
	{
		return complexList;
	}
	/**
	 * @param complexList the complexList to set
	 */
	public void setComplexList(List<ComplexObject> complexList)
	{
		this.complexList = complexList;
	}
	/**
	 * @return the complexColleciton
	 */
	public Collection<ComplexObject> getComplexColleciton()
	{
		return complexColleciton;
	}
	/**
	 * @param complexColleciton the complexColleciton to set
	 */
	public void setComplexColleciton(Collection<ComplexObject> complexColleciton)
	{
		this.complexColleciton = complexColleciton;
	}
	/**
	 * @return the complexObject
	 */
	public ComplexObject getComplexObject()
	{
		return complexObject;
	}
	/**
	 * @param complexObject the complexObject to set
	 */
	public void setComplexObject(ComplexObject complexObject)
	{
		this.complexObject = complexObject;
	}

	public Map<String, String> getMap()
	{
		return map;
	}

	public void setMap(Map<String, String> map)
	{
		this.map = map;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ComplexObject that = (ComplexObject) o;
		return Objects.equals(simpleObject, that.simpleObject) &&
				Arrays.equals(complexArray, that.complexArray) &&
				Objects.equals(complexArrayList, that.complexArrayList) &&
				Objects.equals(complexList, that.complexList) &&
				Objects.equals(complexColleciton, that.complexColleciton) &&
				Objects.equals(complexObject, that.complexObject) &&
				Objects.equals(map, that.map);
	}

	@Override
	public int hashCode()
	{
		int result = Objects.hash(simpleObject, complexArrayList, complexList, complexColleciton, complexObject, map);
		result = 31 * result + Arrays.hashCode(complexArray);
		return result;
	}

	@Override
	public String toString()
	{
		return "ComplexObject{" +
				"simpleObject=" + simpleObject +
				", complexArray=" + Arrays.toString(complexArray) +
				", complexArrayList=" + complexArrayList +
				", complexList=" + complexList +
				", complexColleciton=" + complexColleciton +
				", complexObject=" + complexObject +
				", map=" + map +
				'}';
	}
}

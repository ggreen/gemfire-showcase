package com.vmware.data.services.apache.geode.demo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class SimpleObject implements Serializable, Cloneable
{

	private SimpleEnum simpleEnum;

	private java.sql.Date fieldSqlDate;
	private Date fieldDate;
	private Time fieldTime;
	private Timestamp fieldTimestamp;
	private Calendar fieldCalendar;
	private Long fieldLongObject;
	private long fieldLong;
	private double fieldDouble;
	private Double fieldDoubleObject;
	private Float fieldFloatObject;
	private float fieldFloat;
	private Integer fieldInteger;
	private String fieldString;
	private int fieldInt;
	private BigDecimal bigDecimal;

	private Boolean fieldBooleanObject;
	private boolean fieldBoolean;
	private Byte fieldByteObject;
	private byte fiedByte;
	private Character fieldCharObject;
	private char fieldChar;
	private java.lang.Class<?> fieldClass;
	private Error error;
	private Exception exception;
	private Short fieldShortObject;
	private short fieldShort;

	private LocalDateTime localDateTime;
	private LocalDate localDate;
	private LocalTime localTime;
	private boolean setWithNoGet;

	private byte getWithNoSet = 23;

	private String overloadedRestriction;

	public LocalDate getLocalDate()
	{
		return localDate;
	}

	public LocalTime getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(LocalTime localTime)
	{
		this.localTime = localTime;
	}

	public void setLocalDate(LocalDate localDate)
	{
		this.localDate = localDate;
	}

	public LocalDateTime getLocalDateTime()
	{
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime)
	{
		this.localDateTime = localDateTime;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2702741187335693984L;

	enum SimpleEnum {
		ENUM1,
		ENUM2
	}

	@Override
	public Object clone()
	throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	
	
	/**
	 * @return the simpleEnum
	 */
	public SimpleEnum getSimpleEnum()
	{
		return simpleEnum;
	}
	/**
	 * @param simpleEnum the simpleEnum to set
	 */
	public void setSimpleEnum(SimpleEnum simpleEnum)
	{
		this.simpleEnum = simpleEnum;
	}
	/**
	 * @return the fieldSqlDate
	 */
	public java.sql.Date getFieldSqlDate()
	{
		return fieldSqlDate;
	}
	/**
	 * @param fieldSqlDate the fieldSqlDate to set
	 */
	public void setFieldSqlDate(java.sql.Date fieldSqlDate)
	{
		this.fieldSqlDate = fieldSqlDate;
	}
	/**
	 * @return the fieldDate
	 */
	public Date getFieldDate()
	{
		return fieldDate;
	}
	/**
	 * @param fieldDate the fieldDate to set
	 */
	public void setFieldDate(Date fieldDate)
	{
		this.fieldDate = fieldDate;
	}
	/**
	 * @return the fieldTime
	 */
	public Time getFieldTime()
	{
		return fieldTime;
	}
	/**
	 * @param fieldTime the fieldTime to set
	 */
	public void setFieldTime(Time fieldTime)
	{
		this.fieldTime = fieldTime;
	}
	/**
	 * @return the fieldTimestamp
	 */
	public Timestamp getFieldTimestamp()
	{
		return fieldTimestamp;
	}
	/**
	 * @param fieldTimestamp the fieldTimestamp to set
	 */
	public void setFieldTimestamp(Timestamp fieldTimestamp)
	{
		this.fieldTimestamp = fieldTimestamp;
	}
	/**
	 * @return the fieldCalendar
	 */
	public Calendar getFieldCalendar()
	{
		return fieldCalendar;
	}
	/**
	 * @param fieldCalendar the fieldCalendar to set
	 */
	public void setFieldCalendar(Calendar fieldCalendar)
	{
		this.fieldCalendar = fieldCalendar;
	}
	/**
	 * @return the fieldLongObject
	 */
	public Long getFieldLongObject()
	{
		return fieldLongObject;
	}
	/**
	 * @param fieldLongObject the fieldLongObject to set
	 */
	public void setFieldLongObject(Long fieldLongObject)
	{
		this.fieldLongObject = fieldLongObject;
	}
	/**
	 * @return the fieldLong
	 */
	public long getFieldLong()
	{
		return fieldLong;
	}
	/**
	 * @param fieldLong the fieldLong to set
	 */
	public void setFieldLong(long fieldLong)
	{
		this.fieldLong = fieldLong;
	}
	/**
	 * @return the fieldDouble
	 */
	public double getFieldDouble()
	{
		return fieldDouble;
	}
	/**
	 * @param fieldDouble the fieldDouble to set
	 */
	public void setFieldDouble(double fieldDouble)
	{
		this.fieldDouble = fieldDouble;
	}
	/**
	 * @return the fieldDoubleObject
	 */
	public Double getFieldDoubleObject()
	{
		return fieldDoubleObject;
	}
	/**
	 * @param fieldDoubleObject the fieldDoubleObject to set
	 */
	public void setFieldDoubleObject(Double fieldDoubleObject)
	{
		this.fieldDoubleObject = fieldDoubleObject;
	}
	/**
	 * @return the fieldFloatObject
	 */
	public Float getFieldFloatObject()
	{
		return fieldFloatObject;
	}
	/**
	 * @param fieldFloatObject the fieldFloatObject to set
	 */
	public void setFieldFloatObject(Float fieldFloatObject)
	{
		this.fieldFloatObject = fieldFloatObject;
	}
	/**
	 * @return the fieldFloat
	 */
	public float getFieldFloat()
	{
		return fieldFloat;
	}
	/**
	 * @param fieldFloat the fieldFloat to set
	 */
	public void setFieldFloat(float fieldFloat)
	{
		this.fieldFloat = fieldFloat;
	}
	/**
	 * @return the fieldInteger
	 */
	public Integer getFieldInteger()
	{
		return fieldInteger;
	}
	/**
	 * @param fieldInteger the fieldInteger to set
	 */
	public void setFieldInteger(Integer fieldInteger)
	{
		this.fieldInteger = fieldInteger;
	}
	/**
	 * @return the fieldString
	 */
	public String getFieldString()
	{
		return fieldString;
	}
	/**
	 * @param fieldString the fieldString to set
	 */
	public void setFieldString(String fieldString)
	{
		this.fieldString = fieldString;
	}
	/**
	 * @return the fieldInt
	 */
	public int getFieldInt()
	{
		return fieldInt;
	}
	/**
	 * @param fieldInt the fieldInt to set
	 */
	public void setFieldInt(int fieldInt)
	{
		this.fieldInt = fieldInt;
	}
	
	
	/**
	 * @return the bigDecimal
	 */
	public BigDecimal getBigDecimal()
	{
		return bigDecimal;
	}
	/**
	 * @param bigDecimal the bigDecimal to set
	 */
	public void setBigDecimal(BigDecimal bigDecimal)
	{
		this.bigDecimal = bigDecimal;
	}

	

	/**
	 * @return the fieldBooleanObject
	 */
	public Boolean getFieldBooleanObject()
	{
		return fieldBooleanObject;
	}
	/**
	 * @param fieldBooleanObject the fieldBooleanObject to set
	 */
	public void setFieldBooleanObject(Boolean fieldBooleanObject)
	{
		this.fieldBooleanObject = fieldBooleanObject;
	}
	/**
	 * @return the fieldBoolean
	 */
	public boolean isFieldBoolean()
	{
		return fieldBoolean;
	}
	/**
	 * @param fieldBoolean the fieldBoolean to set
	 */
	public void setFieldBoolean(boolean fieldBoolean)
	{
		this.fieldBoolean = fieldBoolean;
	}
	/**
	 * @return the fieldByteObject
	 */
	public Byte getFieldByteObject()
	{
		return fieldByteObject;
	}
	/**
	 * @param fieldByteObject the fieldByteObject to set
	 */
	public void setFieldByteObject(Byte fieldByteObject)
	{
		this.fieldByteObject = fieldByteObject;
	}
	/**
	 * @return the fiedByte
	 */
	public byte getFiedByte()
	{
		return fiedByte;
	}
	/**
	 * @param fiedByte the fiedByte to set
	 */
	public void setFiedByte(byte fiedByte)
	{
		this.fiedByte = fiedByte;
	}
	/**
	 * @return the fieldCharObject
	 */
	public Character getFieldCharObject()
	{
		return fieldCharObject;
	}
	/**
	 * @param fieldCharObject the fieldCharObject to set
	 */
	public void setFieldCharObject(Character fieldCharObject)
	{
		this.fieldCharObject = fieldCharObject;
	}
	/**
	 * @return the fieldChar
	 */
	public char getFieldChar()
	{
		return fieldChar;
	}
	/**
	 * @param fieldChar the fieldChar to set
	 */
	public void setFieldChar(char fieldChar)
	{
		this.fieldChar = fieldChar;
	}
	/**
	 * @return the fieldClass
	 */
	public java.lang.Class<?> getFieldClass()
	{
		return fieldClass;
	}
	/**
	 * @param fieldClass the fieldClass to set
	 */
	public void setFieldClass(java.lang.Class<?> fieldClass)
	{
		this.fieldClass = fieldClass;
	}
	/**
	 * @return the error
	 */
	public Error getError()
	{
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(Error error)
	{
		this.error = error;
	}
	/**
	 * @return the exception
	 */
	public Exception getException()
	{
		return exception;
	}
	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	/**
	 * @return the fieldShortObject
	 */
	public Short getFieldShortObject()
	{
		return fieldShortObject;
	}
	/**
	 * @param fieldShortObject the fieldShortObject to set
	 */
	public void setFieldShortObject(Short fieldShortObject)
	{
		this.fieldShortObject = fieldShortObject;
	}
	/**
	 * @return the fieldShort
	 */
	public short getFieldShort()
	{
		return fieldShort;
	}
	/**
	 * @param fieldShort the fieldShort to set
	 */
	public void setFieldShort(short fieldShort)
	{
		this.fieldShort = fieldShort;
	}



	/**
	 * @return the getWithNoSet
	 */
	public byte getGetWithNoSet()
	{
		return getWithNoSet;
	}
	/**
	 * @param setWithNoGet the setWithNoGet to set
	 */
	public void setSetWithNoGet(boolean setWithNoGet)
	{
		this.setWithNoGet = setWithNoGet;
	}



	/**
	 * @return the overloadedRestriction
	 */
	public String getOverloadedRestriction()
	{
		return overloadedRestriction;
	}
	/**
	 * @param overloadedRestriction the overloadedRestriction to set
	 */
	public void setOverloadedRestriction(String overloadedRestriction)
	{
		this.overloadedRestriction = overloadedRestriction;
	}
	



	/**
	 * @return the setWithNoGet
	 */
	public boolean isSetWithNoGet()
	{
		return setWithNoGet;
	}
	/**
	 * @param getWithNoSet the getWithNoSet to set
	 */
	public void setGetWithNoSet(byte getWithNoSet)
	{
		this.getWithNoSet = getWithNoSet;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleObject [simpleEnum=").append(simpleEnum).append(", fieldSqlDate=").append(fieldSqlDate)
		.append(", fieldDate=").append(fieldDate).append(", fieldTime=").append(fieldTime).append(", fieldTimestamp=")
		.append(fieldTimestamp).append(", fieldCalendar=").append(fieldCalendar).append(", fieldLongObject=")
		.append(fieldLongObject).append(", fieldLong=").append(fieldLong).append(", fieldDouble=").append(fieldDouble)
		.append(", fieldDoubleObject=").append(fieldDoubleObject).append(", fieldFloatObject=").append(fieldFloatObject)
		.append(", fieldFloat=").append(fieldFloat).append(", fieldInteger=").append(fieldInteger)
		.append(", fieldString=").append(fieldString).append(", fieldInt=").append(fieldInt).append(", bigDecimal=")
		.append(bigDecimal).append(", fieldBooleanObject=").append(fieldBooleanObject).append(", fieldBoolean=")
		.append(fieldBoolean).append(", fieldByteObject=").append(fieldByteObject).append(", fiedByte=")
		.append(fiedByte).append(", fieldCharObject=").append(fieldCharObject).append(", fieldChar=").append(fieldChar)
		.append(", fieldClass=").append(fieldClass).append(", error=").append(error).append(", exception=")
		.append(exception).append(", fieldShortObject=").append(fieldShortObject).append(", fieldShort=")
		.append(fieldShort).append(", setWithNoGet=").append(setWithNoGet).append(", getWithNoSet=")
		.append(getWithNoSet).append(", overloadedRestriction=").append(overloadedRestriction).append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SimpleObject that = (SimpleObject) o;
		return fieldLong == that.fieldLong &&
				Double.compare(that.fieldDouble, fieldDouble) == 0 &&
				Float.compare(that.fieldFloat, fieldFloat) == 0 &&
				fieldInt == that.fieldInt &&
				fieldBoolean == that.fieldBoolean &&
				fiedByte == that.fiedByte &&
				fieldChar == that.fieldChar &&
				fieldShort == that.fieldShort &&
				setWithNoGet == that.setWithNoGet &&
				getWithNoSet == that.getWithNoSet &&
				simpleEnum == that.simpleEnum &&
				Objects.equals(fieldSqlDate, that.fieldSqlDate) &&
				Objects.equals(fieldDate, that.fieldDate) &&
				Objects.equals(fieldTime, that.fieldTime) &&
				Objects.equals(fieldTimestamp, that.fieldTimestamp) &&
				Objects.equals(fieldCalendar, that.fieldCalendar) &&
				Objects.equals(fieldLongObject, that.fieldLongObject) &&
				Objects.equals(fieldDoubleObject, that.fieldDoubleObject) &&
				Objects.equals(fieldFloatObject, that.fieldFloatObject) &&
				Objects.equals(fieldInteger, that.fieldInteger) &&
				Objects.equals(fieldString, that.fieldString) &&
				Objects.equals(bigDecimal, that.bigDecimal) &&
				Objects.equals(fieldBooleanObject, that.fieldBooleanObject) &&
				Objects.equals(fieldByteObject, that.fieldByteObject) &&
				Objects.equals(fieldCharObject, that.fieldCharObject) &&
				Objects.equals(fieldClass, that.fieldClass) &&
				Objects.equals(error, that.error) &&
				Objects.equals(exception, that.exception) &&
				Objects.equals(fieldShortObject, that.fieldShortObject) &&
				Objects.equals(overloadedRestriction, that.overloadedRestriction);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(simpleEnum, fieldSqlDate, fieldDate, fieldTime, fieldTimestamp, fieldCalendar, fieldLongObject, fieldLong, fieldDouble, fieldDoubleObject, fieldFloatObject, fieldFloat, fieldInteger, fieldString, fieldInt, bigDecimal, fieldBooleanObject, fieldBoolean, fieldByteObject, fiedByte, fieldCharObject, fieldChar, fieldClass, error, exception, fieldShortObject, fieldShort, setWithNoGet, getWithNoSet, overloadedRestriction);
	}

	
}

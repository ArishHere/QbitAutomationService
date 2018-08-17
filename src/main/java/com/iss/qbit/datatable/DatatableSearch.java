
package com.iss.qbit.datatable;

import org.json.JSONException;
import org.json.JSONObject;

public class DatatableSearch
{

	public String	value;
	public boolean	regex;

	public DatatableSearch(JSONObject jsonObject) throws JSONException
	{
		this((jsonObject.isNull("value") ? null : jsonObject.getString("value")), jsonObject.getBoolean("regex"));
	}

	public DatatableSearch(String value, boolean regex)
	{
		super();
		this.value = ((value == null || value.isEmpty()) ? null : value);

		this.regex = regex;

		// this.regex = (regex.equals("false") ? false : true);
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return the regex
	 */
	public boolean isRegex()
	{
		return regex;
	}

	/**
	 * @param regex
	 *            the regex to set
	 */
	public void setRegex(boolean regex)
	{
		this.regex = regex;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DatatableSearch [value=" + value + ", regex=" + regex + "]";
	}

	public boolean valuePresent()
	{
		return value != null && !value.isEmpty();
	}

}

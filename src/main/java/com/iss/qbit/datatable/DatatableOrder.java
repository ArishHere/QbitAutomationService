
package com.iss.qbit.datatable;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatatableOrder
{

	public int		column;
	public String	dir;

	public static ArrayList<DatatableOrder> parse(JSONArray jsonArray) throws JSONException
	{
		ArrayList<DatatableOrder> order = new ArrayList<DatatableOrder>();
		for (int i = 0; i < jsonArray.length(); i++)
		{
			order.add(new DatatableOrder(jsonArray.getJSONObject(i)));
		}
		return order;
	}

	public DatatableOrder(int column, String dir)
	{
		super();
		this.column = column;
		this.dir = dir;
	}

	public DatatableOrder(JSONObject jsonObject) throws JSONException
	{
		this(jsonObject.getInt("column"), jsonObject.getString("dir"));
	}

	/**
	 * @return the column
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 * @param column
	 *            the column to set
	 */
	public void setColumn(int column)
	{
		this.column = column;
	}

	/**
	 * @return the dir
	 */
	public String getDir()
	{
		return dir;
	}

	/**
	 * @param dir
	 *            the dir to set
	 */
	public void setDir(String dir)
	{
		this.dir = dir;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DatatableOrder [column=" + column + ", dir=" + dir + "]";
	}
}

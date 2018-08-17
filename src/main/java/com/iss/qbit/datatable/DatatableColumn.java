
package com.iss.qbit.datatable;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatatableColumn
{

	public String				data, name;
	public boolean				searchable, orderable;
	public DatatableSearch		search;
	public static JSONObject	alias;

	public static ArrayList<DatatableColumn> parse(JSONArray jsonArray, JSONObject aliasObj) throws JSONException
	{
		// String temp = ApplicationSetup.robotConfig.get(robotName + ".Result.execution.query.alias");
		// if (temp != null && !temp.isEmpty()) alias = new JSONObject(temp);
		// else alias = new JSONObject();
		alias = aliasObj;

		ArrayList<DatatableColumn> order = new ArrayList<DatatableColumn>();
		for (int i = 0; i < jsonArray.length(); i++)
		{
			order.add(new DatatableColumn(jsonArray.getJSONObject(i)));
		}
		return order;
	}

	public DatatableColumn(String data, String name, DatatableSearch search, int searchable, boolean orderable) throws JSONException
	{
		super();
		this.data = data;
		this.name = name;
		this.search = search;
		this.searchable = ((searchable == 0 ? false : true));
		this.orderable = orderable;
	}

	public DatatableColumn(JSONObject map) throws JSONException
	{
		this(map.getString("data"), map.getString("name"), new DatatableSearch(map.getJSONObject("search")), map.getInt("searchable"), map.getBoolean("orderable"));
	}

	/**
	 * @return the data
	 */
	public String getData()
	{
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(String data)
	{
		this.data = data;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the searchable
	 */
	public boolean isSearchable()
	{
		return searchable;
	}

	/**
	 * @param searchable
	 *            the searchable to set
	 */
	public void setSearchable(boolean searchable)
	{
		this.searchable = searchable;
	}

	/**
	 * @return the orderable
	 */
	public boolean isOrderable()
	{
		return orderable;
	}

	/**
	 * @param orderable
	 *            the orderable to set
	 */
	public void setOrderable(boolean orderable)
	{
		this.orderable = orderable;
	}

	/**
	 * @return the search
	 */
	public DatatableSearch getSearch()
	{
		return search;
	}

	/**
	 * @param search
	 *            the search to set
	 */
	public void setSearch(DatatableSearch search)
	{
		this.search = search;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DatatableColumn [data=" + data + ", name=" + name + ", searchable=" + searchable + ", orderable=" + orderable + ", search=" + search + "]";
	}

	public String getDBName() throws JSONException
	{
		if (alias.has(data))
		{
			if (alias.isNull(data)) return null;
			else return alias.getString(data);
		}
		else return data;
	}

	public String getDBSearchColumn() throws JSONException
	{
		if (searchable)
		{
			if (alias.has(data))
			{
				if (alias.isNull(data)) return null;
				else return alias.getString(data);
			}
			else return data;
		}
		else return null;
	}
}

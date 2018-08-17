
package com.iss.qbit.datatable;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.iss.qbit.web.commons.utils.RobotConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class DatatableParameter
{

	private static org.apache.log4j.Logger	log	= Logger.getLogger(DatatableColumn.class);
	//	private static HashMap<String, String> robotConfig;

	ArrayList<DatatableColumn>				columns;
	Integer									start, length, draw;
	ArrayList<DatatableOrder>				order;
	String									robotName;
	DatatableSearch							globalSearch;

	// Boolean searchRegex;

	public DatatableParameter()
	{
	}

	//    @Autowired(required=true)
	//	public void setRobotConfig(RobotConfig2 db) throws SQLException
	//	{
	//		robotConfig=db.getConfiguration();
	//	}

	public static DatatableParameter parse(JSONObject map, String aliasObj)
	{
		try
		{
			log.warn(map);
			DatatableParameter dt = new DatatableParameter();
			String robotName = (map.getString("robotName"));
			if (map.has("draw"))
			{
				dt.draw = Integer.parseInt(map.getString("draw"));
				dt.order = DatatableOrder.parse(map.getJSONArray("order"));
				dt.start = Integer.parseInt(map.getString("start"));
				dt.length = Integer.parseInt(map.getString("length"));

				// dt.orderDirection = (map.getString("order[0][dir]"));
				dt.globalSearch = new DatatableSearch(map.getJSONObject("search"));

				// String regex = map.getString("search[regex]");
				// dt.searchRegex = ((regex.equals("false") ? false : true));

				// int i = -1;
				// while (true)
				// {
				// i++;
				// if (map.has("columns[" + i + "][data]"))
				// {
				// DatatableColumn col = new DatatableColumn(map.getString("columns[" + i + "][data]"), map.getString("columns[" + i + "][name]"), map.getString("columns[" + i + "][search][value]"), map.getString("columns[" + i + "][searchable]"), map.getString("columns[" + i + "][orderable]"), map.getString("columns[" + i + "][search][regex]"));
				// dt.column.put(i, col);
				// }
				// else break;
				// }
				String temp = RobotConfig.getConfig().get(robotName + aliasObj);
				JSONObject alias;
				if (temp != null && !temp.isEmpty()) alias = new JSONObject(temp);
				else alias = new JSONObject();

				dt.columns = DatatableColumn.parse(map.getJSONArray("columns"), alias);
			}
			else
			{

				if (map.has("order")) dt.order = DatatableOrder.parse(map.getJSONArray("order"));
				if (map.has("start")) dt.start = map.getInt("start");
				if (map.has("length")) dt.length = map.getInt("length");
				if (map.has("search")) dt.globalSearch = new DatatableSearch(map.getJSONObject("search"));

				String temp = RobotConfig.getConfig().get(robotName + aliasObj);
				JSONObject alias;
				if (temp != null && !temp.isEmpty()) alias = new JSONObject(temp);
				else alias = new JSONObject();

				if (map.has("columns")) dt.columns = DatatableColumn.parse(map.getJSONArray("columns"), alias);
			}
			dt.robotName = robotName;
			System.out.println(dt);
			return dt;
		}
		catch (Exception e)
		{
			log.warn("Error in parsing datatable parameters [" + map + "]", e);
			throw new RuntimeException("Error in parsing datatable parameters [" + map + "]", e);
		}
	}

	public String getWhereClause(boolean prefixAnd) throws JSONException
	{
		String where1 = "", where2 = "";
		 if ( columns!=null)
		{
			for (Iterator<DatatableColumn> i = columns.iterator(); i.hasNext();)
			{
				DatatableColumn col = (DatatableColumn) i.next();
				String dbName = col.getDBSearchColumn();
				if (dbName != null && globalSearch != null && globalSearch.valuePresent())
				{
					where1 += "`" + dbName + "`" + ((globalSearch.regex) ? " RLIKE " + "\"" + globalSearch.value + "\"" : " LIKE " + "\"%" + globalSearch.value + "%\"") + ((i.hasNext()) ? " OR " : "");
				}
				if (col.isSearchable() && col.getSearch().getValue() != null)
				{
					where2 += ((where2.isEmpty()) ? "" : " AND ") + "`" + dbName + "`" + ((col.getSearch().regex) ? " RLIKE " + "\"" + col.getSearch().value + "\"" : " LIKE " + "\"%" + col.getSearch().value + "%\"");
				}
			}
		}
//		System.out.println(((where1.isEmpty()) ? ((where2.isEmpty()) ? "" : "(" + where2 + ")") : (((prefixAnd) ? " AND " : " ") + "(" + where1 + ") AND (" + where2 + ")")));
		// return ((where1.isEmpty()) ? "" : (((prefixAnd) ? " AND " : " ") + "(" + where1 + ")"));
		return ((where1.isEmpty()) ? ((where2.isEmpty()) ? "" : ((prefixAnd) ? " AND " : " ") + "(" + where2 + ")") : (((prefixAnd) ? " AND " : " ") + "(" + where1 + ") AND (" + where2 + ")"));
	}

	public String getOrderByClause()
	{
		String orderBy = "";
		if (order != null)
		{
			orderBy += " Order By " + columns.get(order.get(0).column).data + " " + order.get(0).dir + " ";
		}
		return orderBy;
	}

	public String getLimitClause()
	{
		return ((start == null || length == null) ? "" : " OFFSET " + start + " ROWS FETCH NEXT " + length + " ROWS ONLY ");
	}

	/**
	 * @return the columns
	 */
	public ArrayList<DatatableColumn> getColumns()
	{
		return columns;
	}

	/**
	 * @param columns
	 *            the columns to set
	 */
	public void setColumns(ArrayList<DatatableColumn> columns)
	{
		this.columns = columns;
	}

	/**
	 * @return the start
	 */
	public Integer getStart()
	{
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Integer start)
	{
		this.start = start;
	}

	/**
	 * @return the length
	 */
	public Integer getLength()
	{
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(Integer length)
	{
		this.length = length;
	}

	/**
	 * @return the draw
	 */
	public Integer getDraw()
	{
		return draw;
	}

	/**
	 * @param draw
	 *            the draw to set
	 */
	public void setDraw(Integer draw)
	{
		this.draw = draw;
	}

	/**
	 * @return the order
	 */
	public ArrayList<DatatableOrder> getOrder()
	{
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(ArrayList<DatatableOrder> order)
	{
		this.order = order;
	}

	/**
	 * @return the robotName
	 */
	public String getRobotName()
	{
		return robotName;
	}

	/**
	 * @param robotName
	 *            the robotName to set
	 */
	public void setRobotName(String robotName)
	{
		this.robotName = robotName;
	}

	/**
	 * @return the globalSearch
	 */
	public DatatableSearch getGlobalSearch()
	{
		return globalSearch;
	}

	/**
	 * @param globalSearch
	 *            the globalSearch to set
	 */
	public void setGlobalSearch(DatatableSearch globalSearch)
	{
		this.globalSearch = globalSearch;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DatatableParameter [columns=" + columns + ", start=" + start + ", length=" + length + ", draw=" + draw + ", order=" + order + ", robotName=" + robotName + ", globalSearch=" + globalSearch + "]";
	}

}

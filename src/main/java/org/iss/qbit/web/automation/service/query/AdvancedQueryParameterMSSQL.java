
package org.iss.qbit.web.automation.service.query;

import java.util.HashMap;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
public class AdvancedQueryParameterMSSQL
{

	private String					condition;
//	private String					robotName;
	private int						calNum;
	private HashMap<String, String>	valueMaps	= new HashMap<String, String>();

	public static AdvancedQueryParameterMSSQL parse(JSONObject json, String string)
	{
		try
		{
			AdvancedQueryParameterMSSQL aq = new AdvancedQueryParameterMSSQL();
//			String robotName = (json.getString("robotName"));
			aq.condition = "";
			if (json.has("query"))
			{
				aq.condition = aq.conditionGroup(json.getJSONObject("query")).toString();
			}
//			aq.robotName = robotName;
			System.out.println(aq);
			return aq;
		}
		catch (Exception e)
		{
			log.warn("Error in parsing query parameters [" + json + "]", e);
			throw new RuntimeException("Error in parsing query parameters [" + json + "]", e);
		}
	}

	private String conditionGroup(JSONObject j)
	{
		if (j.has("$q"))
		{
			log.info("Group {}", j);
			JSONArray arr = j.getJSONArray("$q");
			int arrLength;
//			StringBuilder con = new StringBuilder("(");//"";
			StringJoiner con = new StringJoiner(parseCondition(j.getString("$op")),"(",")");
			for (int i = 0; i < (arrLength = arr.length()); i++)
			{
				//				con.append();
				String c_con = conditionGroup(arr.getJSONObject(i));
				if(!c_con.trim().isEmpty())
					con.add(c_con);
					
//				appendCondition(con, c_con,i < arrLength - 1,j.getString("$op"));

			}
			if (con.length() > 2)
			{
				return con.toString();
			}
			else
			{
				return "";
			}
		}
		else
		{
			return condition(j);
		}
	}

	private String parseCondition(String condition)
	{
		switch (condition)
		{
			case "or":
				return(" OR ");
			case "and":
				return(" AND ");
			default:
				throw new RuntimeException("Unknown condition:" + condition);
		}
	}

//	private void appendCondition(StringBuilder con, StringBuilder c_con, boolean isLast, String condition)
//	{
//		if (c_con.length() > 0)
//		{
//			if (isLast) switch (condition)
//			{
//				case "or":
//					c_con.append(" OR ");
//					break;
//				case "and":
//					c_con.append(" AND ");
//					break;
//			}
//			con.append(c_con);
//		}
//	}

	private String condition(JSONObject j)
	{
		log.info("Condition {}", j);
		if (j.has("$column"))
		{
			JSONObject col = j.getJSONObject("$column");
			String type = j.getJSONObject("$type").getString("op");
			switch (type)
			{
				case "$eq":
					valueMaps.put("col" + calNum, j.getString("$value"));
					return new StringBuilder().append("[")//
							.append(col.getString("propName"))//
							.append("]=:col")//
							.append(calNum++).toString();
				case "$cont":
					//				case "regex":
					valueMaps.put("col" + calNum, "%" + j.getString("$value") + "%");
					String colName=col.getString("propName");
					return new StringBuilder().append("[")//
							.append(colName)//
							.append("] like :col")//
							.append(calNum++).toString();
				default:
					throw new RuntimeException("Unknown type:" + type);
			}
		}
		throw new RuntimeException("$column not found:" + j);

	}

	public String getWhereClause(boolean prefixAnd)
	{
		return (condition.trim().isEmpty()) ? "" : (prefixAnd) ? " AND " + condition : condition;
	}
	
	public String getWhereClauseOr(boolean prefixOr){
		return (condition.trim().isEmpty()) ? "" : (prefixOr) ? " OR " + condition : condition;

		
	}

}

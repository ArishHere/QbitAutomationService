
package org.iss.qbit.web.automation.service.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
public class AdvancedQueryParameterMongo
{

	private BasicDBObject					condition;
	private String					robotName;
	private int						calNum;
	private HashMap<String, String>	valueMaps	= new HashMap<String, String>();

	public static AdvancedQueryParameterMongo parse(JSONObject json, String string)
	{
		try
		{
			AdvancedQueryParameterMongo aq = new AdvancedQueryParameterMongo();
			String robotName = (json.getString("robotName"));
			if (json.has("query"))
			{
				aq.condition = aq.conditionGroup(json.getJSONObject("query"));
			}
			aq.robotName = robotName;
			System.out.println(aq);
			return aq;
		}
		catch (Exception e)
		{
			log.warn("Error in parsing query parameters [" + json + "]", e);
			throw new RuntimeException("Error in parsing query parameters [" + json + "]", e);
		}
	}

	private BasicDBObject conditionGroup(JSONObject j)
	{
		if (j.has("$q"))
		{
			log.info("Group {}", j);
			JSONArray arr = j.getJSONArray("$q");
			int arrLength;
			//			StringBuilder con = new StringBuilder("(");//"";
			List<BasicDBObject> con = new ArrayList<BasicDBObject>();
			for (int i = 0; i < (arrLength = arr.length()); i++)
			{
				//				con.append();
				BasicDBObject c_con = conditionGroup(arr.getJSONObject(i));
				con.add(c_con);

			}
			return appendCondition(con, parseCondition(j.getString("$op")));
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
				return ("$or");
			case "and":
				return ("$and");
			default:
				throw new RuntimeException("Unknown condition:" + condition);
		}
	}

	private BasicDBObject appendCondition(List<BasicDBObject> con, String condition)
	{
		BasicDBObject join = new BasicDBObject();
		join.put(condition, con);
		return join;

	}

	private BasicDBObject condition(JSONObject j)
	{
		log.info("Condition {}", j);
		if (j.has("$column"))
		{
			JSONObject col = j.getJSONObject("$column");
			String type = j.getJSONObject("$type").getString("op");
			switch (type)
			{
				case "$eq":
					BasicDBObject whereQuery = new BasicDBObject();
					whereQuery.put(col.getString("data"), j.get("$value"));
					return whereQuery;
				case "$rgx":
					whereQuery = new BasicDBObject();
					whereQuery.put(col.getString("data"), new BasicDBObject("$regex", j.get("$value")));
					return whereQuery;
				case "$rgxi":
					whereQuery = new BasicDBObject();
					whereQuery.put(col.getString("data"), new BasicDBObject("$regex", j.get("$value")).append("$options", "i"));
					return whereQuery;
				case "$lt":
					whereQuery = new BasicDBObject();
					whereQuery.put(col.getString("data"), new BasicDBObject("$gt", j.get("$value")));
					return whereQuery;
				case "$gt":
					whereQuery = new BasicDBObject();
					whereQuery.put(col.getString("data"), new BasicDBObject("$lt", j.get("$value")));
					return whereQuery;
				case "$ne":
					whereQuery = new BasicDBObject();
					whereQuery.put(col.getString("data"), new BasicDBObject("$ne", j.get("$value")));
					return whereQuery;
				default:
					throw new RuntimeException("Unknown type:" + type);
			}
		}
		throw new RuntimeException("$column not found:" + j);

	}

	public BasicDBObject getWhereClause(boolean prefixAnd)
	{
		return condition;
	}

}

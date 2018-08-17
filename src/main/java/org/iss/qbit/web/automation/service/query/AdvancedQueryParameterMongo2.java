
package org.iss.qbit.web.automation.service.query;

import java.util.HashMap;

import org.iss.qbit.web.spring.mongodb.CustomQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
public class AdvancedQueryParameterMongo2
{

	private Criteria			condition;
	private String					robotName;
	private int						calNum;
	private HashMap<String, String>	valueMaps	= new HashMap<String, String>();

	public static AdvancedQueryParameterMongo2 parse(JSONObject json, String string)
	{
		try
		{
			AdvancedQueryParameterMongo2 aq = new AdvancedQueryParameterMongo2();
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

	private Criteria conditionGroup(JSONObject j)
	{
		if (j.has("$q"))
		{
			log.info("Group {}", j);
			JSONArray arr = j.getJSONArray("$q");
			int arrLength;
			//			StringBuilder con = new StringBuilder("(");//"";
//			List<Criteria> con = new ArrayList<Criteria>();
			arrLength = arr.length();
			Criteria con[]=new Criteria[arrLength];
			for (int i = 0; i < arrLength; i++)
			{
				//				con.append();
				Criteria c_con = conditionGroup(arr.getJSONObject(i));
				con[i]=c_con;
			}
			if( arrLength>0)
			return appendCondition(con, parseCondition(j.getString("$op")));
			return new Criteria();
			
		}
		else
		{
			return condition(j);
		}
	}

	private String parseCondition(String condition)
	{
		return condition;
	}

	private Criteria appendCondition(Criteria[] con, String condition)
	{
		Criteria c = new Criteria();
		
		switch (condition)
		{
			case "or":
				c.orOperator(con);
				return c;
			case "and":
				c.andOperator(con);
				return c;
			default:
				throw new RuntimeException("Unknown condition:" + condition);
		}
	}

	private Criteria condition(JSONObject j)
	{
		log.info("Condition {}", j);
		if (j.has("$column"))
		{
			JSONObject col = j.getJSONObject("$column");
			String type = j.getJSONObject("$type").getString("op");
			String colName=col.isNull("filter")?col.getString("propName"):col.getString("filter");
			switch (type)
			{
				case "$eq":
					return Criteria.where(colName).is(j.get("$value"));
				case "$rgx":
					return Criteria.where(colName).regex(j.getString("$value"));
				case "$rgxi":
					return Criteria.where(colName).regex(j.getString("$value"), "i");
				case "$lt":
					return Criteria.where(colName).lt(j.get("$value"));
				case "$gt":
					return Criteria.where(colName).gt(j.get("$value"));
				case "$ne":
					return Criteria.where(colName).ne(j.get("$value"));
				case "$cont":
					return Criteria.where(colName).regex(".*"+j.get("$value")+".*");
				default:
					throw new RuntimeException("Unknown type:" + type);
			}
		}
		log.warn("Using empty Criteria as '$column' not found in param:{}" , j);
		return new Criteria();
	}

	public Criteria getWhereClause(boolean prefixAnd)
	{
		return condition;
	}

	public Query getQuery(boolean b)
	{
		Query q = new CustomQuery(condition);
//		q.fields()
//		q.fields().include("").include("");
		return q;
	}
}

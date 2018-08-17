
package org.iss.qbit.web.automation.service.bot.results;

import java.util.List;
import java.util.stream.Stream;

import org.iss.qbit.web.automation.serivce.mongodb.MongoDB;
import org.iss.qbit.web.automation.service.query.AdvancedQueryParameterMongo;
import org.iss.qbit.web.automation.service.query.AdvancedQueryParameterMongo2;
import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class ExecutionsResult
{

	private static Logger	log	= LoggerFactory.getLogger(ExecutionsResult.class);

	private DBReader		db;

	private MongoDB			mongoDb;

	//	private static HashMap<String, String> robotConfig;

	@Autowired(required = true)
	public void setRobotConfig(DBReader db)
	{
		this.db = db;
	}

	@Autowired(required = true)
	public void setMongoDB(MongoDB mongoDb)
	{
		this.mongoDb = mongoDb;
	}

	//	@Autowired(required=true)
	//	public void setRobotConfig(RobotConfig2 db) throws SQLException
	//	{
	//		robotConfig=db.getConfiguration();
	//	}

	@RequestMapping(value = "/v2/Results/{robotName}/{execId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Stream<Object> results(@PathVariable String robotName, @PathVariable String execId, @RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.trace(jsonS);
		JSONObject json = new JSONObject(jsonS);
		AdvancedQueryParameterMongo2 aq2 = AdvancedQueryParameterMongo2.parse(json, ".Result.select.query.alias");
		Integer execID = new Integer(json.getString("execId"));

		//		JSONObject jsonResponse = new JSONObject();
		try
		{
			log.trace(robotName + ".Result." + "select.query");
			log.trace(RobotConfig.getConfig().toString());
			log.info("AQ2 : {}", aq2.getWhereClause(true).getCriteriaObject());
			Stream<Object> d = mongoDb.find(aq2.getQuery(true));
			return d;
		}
		catch (Exception e2)
		{
			log.error("Error fetching Results", e2);
			//			jsonResponse.put("error", "Error fetching Results");

		}
		finally
		{
		}
		return null;

		//		log.trace(jsonResponse.toString());
		//		return jsonResponse.toString();
	}

	@RequestMapping(value = "/v2/Results/{robotName}/{execId}/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String resultsList(@PathVariable String robotName, @PathVariable String execId, @RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.trace(jsonS);
		JSONObject json = new JSONObject(jsonS);
		AdvancedQueryParameterMongo aq = AdvancedQueryParameterMongo.parse(json, ".Result.select.query.alias");
		AdvancedQueryParameterMongo2 aq2 = AdvancedQueryParameterMongo2.parse(json, ".Result.select.query.alias");
		Integer execID = new Integer(json.getString("execId"));

		JSONObject jsonResponse = new JSONObject();
		try
		{
			log.trace(robotName + ".Result." + "select.query");
			log.trace(RobotConfig.getConfig().toString());
			//			String query = RobotConfig.getConfig().get(robotName + ".Result.select.query") 
			//					+ " where execid=" + execID + aq.getWhereClause(true) //+ aq.getOrderByClause()
			//			// + " limit 7"
			//			//+ aq.getLimitClause() 
			//			+ ";";

			log.info("AQ1 : {}", aq.getWhereClause(true));
			log.info("AQ2 : {}", aq2.getWhereClause(true).getCriteriaObject());
			List<String> d = mongoDb.findList(aq2.getQuery(true));
			//			return d;
			jsonResponse.put("data", d);
			//			jsonResponse = db.getTableData(query);
			//			rowCount = db.getColumnValue("SELECT FOUND_ROWS();");
			//if ((dt.getStart() == null || dt.getLength() == null)) rowTotalCount = rowCount;
			//			else 
			//				rowTotalCount = db.getColumnValue("SELECT count(*) FROM " + RobotConfig.getConfig().get(robotName + ".Result." + "tableName") + " where execid=" + execID + " ;");
		}
		catch (Exception e2)
		{
			log.error("Error fetching Results", e2);
			//			jsonResponse.put("error", "Error fetching Results");

		}
		finally
		{
		}
		//		return null;

		//		log.trace(jsonResponse.toString());
		return jsonResponse.toString();
	}
}

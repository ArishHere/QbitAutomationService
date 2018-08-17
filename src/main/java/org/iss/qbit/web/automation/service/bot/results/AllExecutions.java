
package org.iss.qbit.web.automation.service.bot.results;

import java.sql.SQLException;

import org.iss.qbit.web.automation.serivce.mongodb.MongoDB;
import org.iss.qbit.web.automation.service.query.AdvancedQueryParameterMSSQL;
import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AllExecutions
{

	private static Logger	log	= LoggerFactory.getLogger(AllExecutions.class);

	private DBReader	db;
//	private MongoDB mongoDb;

	//	private static HashMap<String, String> robotConfig;

	@Autowired(required = true)
	public void setRobotConfig(DBReader db) throws SQLException
	{
		this.db = db;
	}
	//	@PreAuthorize("@authenticator.result(#robotName)")
	@RequestMapping(value = "/v2/AllExecutions/{robotId}", method = RequestMethod.POST, produces = "application/json")
	public String allExecutions(@PathVariable String robotId,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.debug(jsonS);
		JSONObject json = new JSONObject(jsonS);
		
		AdvancedQueryParameterMSSQL aq=  AdvancedQueryParameterMSSQL.parse(json,".Result.execution.query.alias");
		
		JSONObject jsonResponse = null;

		try
		{
			String query = RobotConfig.getConfig().get(robotId + ".Result.execution.query") + aq.getWhereClause(true) //+ dt.getOrderByClause() + dt.getLimitClause() 
			+ ";";
			//query = query.replace("DATEDIFF(MINUTE,[ExecutionMaster].[StartTime],[ExecutionMaster].[ElapseTime]) as ElapsedTime", "[ExecutionMaster].[ElapseTime]");
			log.info(query);
			log.info(aq.getValueMaps().toString() + " is the map");
			jsonResponse = db.getTableData(query,aq.getValueMaps());
		}
		catch (Exception e2)
		{
			jsonResponse=new JSONObject();
			jsonResponse.put("error", true);
			jsonResponse.put("errorMgs", "Error fetching execution list");
			log.error("Error fetching execution list.", e2);
		}
		finally
		{
		}
		log.debug("{}",jsonResponse);
		return jsonResponse.toString();
//		return query;
	}
	
	
	
	@RequestMapping(value = "/v2/AllExecutions/", method = RequestMethod.POST, produces = "application/json")
	public String allExecutionsAllRobots(@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.debug(jsonS);
		JSONObject json = new JSONObject(jsonS);
		
		JSONArray jarray = json.getJSONArray("robotIDs");
		String robotString = jarray.toString().replace("[", "(").replace(']', ')').replace("\"", "'");
		
		AdvancedQueryParameterMSSQL aq=  AdvancedQueryParameterMSSQL.parse(json,".Result.execution.query.alias");
		
		JSONObject jsonResponse = null;

		try
		{
			
			String query = "SELECT top 100 [ExecutionMaster].[ExecId] as _id,[ExecutionMaster].[ExecId],[ExecutionMaster].[RobotName],[ExecutionMaster].[EnvName],[ExecutionMaster].[Version],[ExecutionMaster].[Host],[ExecutionMaster].[BrowserVersion],[ExecutionMaster].[StartTime] as StartTime,DATEDIFF(MINUTE,[ExecutionMaster].[StartTime],[ExecutionMaster].[ElapseTime]) as ElapsedTime,[ExecutionMaster].[Status],[ExecutionMaster].[Description] FROM [Governance].[ExecutionMaster] where [ExecutionMaster].[RobotName] in" +
					" (Select RobotName from Governance.Robot where temp_Id IN " + robotString + ")" + aq.getWhereClause(true) //+ dt.getOrderByClause() + dt.getLimitClause() 
			+ " order by StartTime desc;";
			log.info(query);
			log.info(aq.getValueMaps().toString() + " is the map");
			jsonResponse = db.getTableData(query,aq.getValueMaps());
		}
		catch (Exception e2)
		{
			jsonResponse=new JSONObject();
			jsonResponse.put("error", true);
			jsonResponse.put("errorMgs", "Error fetching execution list");
			log.error("Error fetching execution list.", e2);
		}
		finally
		{
		}
		//log.debug("{}",jsonResponse);
		System.out.println(robotString);
		return jsonResponse.toString();
//		return query;FORWARD_ONLY
		
	}
	
	
}

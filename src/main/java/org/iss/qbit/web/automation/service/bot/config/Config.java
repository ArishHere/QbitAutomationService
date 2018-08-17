
package org.iss.qbit.web.automation.service.bot.config;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.iss.qbit.web.automation.service.query.AdvancedQueryParameterMSSQL;
import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.iss.qbit.web.spring.db.DBWriter;
import org.json.JSONArray;
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

import com.iss.qbit.datatable.DatatableParameter;

@RestController
public class Config
{

	private static Logger	log	= LoggerFactory.getLogger(Config.class);

	private DBReader	db;
	private DBWriter	dbw;

	//	private static HashMap<String, String> robotConfig;

	@Autowired(required = true)
	public void setRobotConfig(DBReader db) throws SQLException
	{
		this.db = db;
	}

	@Autowired(required = true)
	public void setRobotConfigWriter(DBWriter dbw) throws SQLException
	{
		this.dbw = dbw;
	}


	@RequestMapping(value = "/v2/Config/list/{robotName}/{configFor}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String config(@PathVariable String robotName,@PathVariable String configFor,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);
		//		String configFor = json.getString("configFor");
		AdvancedQueryParameterMSSQL aq=  AdvancedQueryParameterMSSQL.parse(json, ".Configuration." + configFor + ".select.query.alias");
		JSONObject jsonResponse = null;
		try
		{
			String where = aq.getWhereClause(false);
			jsonResponse = db.getTableData("" + RobotConfig.getConfig().get(robotName + ".Configuration." + configFor + ".select.query") + (((where != null) && !where.isEmpty()) ? " where " + where : "") //+ aq.getOrderByClause() + qa.getLimitClause() 
					+ ";",aq.getValueMaps());
		}
		catch (Exception e2)
		{
			Config.log.error("Error fetching configuration from database", e2);
		}
		finally
		{

		}
		Config.log.debug(jsonResponse.toString());
		return jsonResponse.toString();
	}


	@RequestMapping(value = "/v2/Config/update/{operation}/{configFor}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String configUpdate(@PathVariable String operation,@PathVariable String configFor,@RequestBody String jsonS) throws JSONException
	{
		
		
		if(configFor.equals("Smoketests")){



			
			if(operation.equals("replace")){
				//can maybe clear smoketest tables here before adding, i.e. replace with uploaded excel file.

			}

			//now do the inserting of values. 
			JSONArray jarray = new JSONArray(jsonS);
			//TestCaseId, Configuration, Comments, Description, TestProcedure, ExpectedResult, Automated (bit), RobotId
			//for(){
			PreparedStatement ps = null;
			try {
				ps = dbw.getPreparedStatement("Insert into [Automation].[Robots].[SmokeTestCaseDetails] (TestCaseId, Configuration, Comments, Description, TestProcedure, ExpectedResult, Automated, RobotId) VALUES (?,?,?,?,?,?,?,?);");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				Config.log.error("Error creating prepared statment.", e1);
			}

			for(int i = 0; i< jarray.length(); i++){

				JSONObject curr = jarray.getJSONObject(i);

				String TestCaseId = curr.has("TestCaseId") ? curr.getString("TestCaseId"): "" ;
				String configuration = curr.has("Configuration") ? curr.getString("Configuration"): "" ;
				String comments = curr.has("Comments") ? curr.getString("Comments"): "" ;
				String description = curr.has("Description") ? curr.getString("Description"): "" ;
				String testProcedure = curr.has("TestProcedure") ? curr.getString("TestProcedure"): "" ;
				String expectedResult = curr.has("ExpectedResult") ? curr.getString("ExpectedResult"): "" ;
				int automated = curr.has("Automated") ? curr.getInt("Automated"): 0 ;
				int robotID = curr.has("RobotId") ? curr.getInt("RobotId"): 0 ;

				try {
					ps.setString(1, TestCaseId);

					ps.setString(2, configuration);
					ps.setString(3, comments);
					ps.setString(4, description);
					ps.setString(5, testProcedure);
					ps.setString(6, expectedResult);
					ps.setInt(7, automated);
					ps.setInt(8, robotID);

					ps.execute();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					Config.log.error("Error populating prepared statement.", e);
				}


				//System.out.println(ps.toString());

			}



			//}


			

		}
		
		//can add more here if there is need to support excel upload to other tables besides smoketest.
		
		
		
		return null;
	}


}


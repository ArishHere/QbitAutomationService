
package org.iss.qbit.web.automation.service.view;

import java.sql.SQLException;

import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutomatedTestCaseList
{

	private static Logger	log	= LoggerFactory.getLogger(AutomatedTestCaseList.class);

	private DBReader	db;

	//	private static HashMap<String, String> robotConfig;

	@Autowired(required = true)
	public void setRobotConfig(DBReader db) throws SQLException
	{
		this.db = db;
	}

	//	@Autowired(required=true)
	//	public void setRobotConfig(RobotConfig2 db) throws SQLException
	//	{
	//		robotConfig=db.getConfiguration();
	//	}

	//@RequestMapping(value = "/v1/{robotName}/Automated/TestCases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String results(@PathVariable String robotName) throws JSONException
	{
		// Actual logic goes here.

		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonTableData = null;
		String rowCount = "N/A";

		String rowTotalCount = null;
		try
		{
			log.trace(robotName + ".Run.testcase.automated.select.query");
			log.trace(RobotConfig.getConfig().toString());
			String query = RobotConfig.getConfig().get(robotName + ".Run.testcase.automated.select.query");
			log.trace(query);
			jsonTableData = db.getRowJSON(query);
			rowCount = db.getColumnValue("SELECT FOUND_ROWS();");
			rowTotalCount = db.getColumnValue(RobotConfig.getConfig().get(robotName + ".Run.testcase.total.count.query"));
		}
		catch (SQLException e2)
		{
			log.error("Error fetching Results", e2);
			jsonResponse.put("error", "Error fetching Results");

		}
		finally
		{
		}

		try
		{
			jsonResponse.put("draw", "");
			jsonResponse.put("recordsFiltered", (rowCount));
			jsonResponse.put("recordsTotal", (rowTotalCount));
			jsonResponse.put("data", jsonTableData);
			// jsonObject.put("id", "viewResultsTable");
		}
		catch (JSONException e)
		{
			log.error("Error inserting values in json", e);
			try
			{
				jsonResponse.put("error", "error inserting values in json");
			}
			catch (JSONException e1)
			{
				log.error("Error inserting values in json", e1);
			}
		}
		log.trace(jsonResponse.toString());
		return jsonResponse.toString();
	}
}

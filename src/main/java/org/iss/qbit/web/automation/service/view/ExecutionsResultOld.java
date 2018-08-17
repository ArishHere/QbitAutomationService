
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iss.qbit.datatable.DatatableParameter;

@RestController
public class ExecutionsResultOld
{

	private static Logger	log	= LoggerFactory.getLogger(ExecutionsResultOld.class);

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

	@RequestMapping(value = "/v1/Results/{robotName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String results(@PathVariable String robotName,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.trace(jsonS);
		JSONObject json = new JSONObject(jsonS);
		DatatableParameter dt = DatatableParameter.parse(json, ".Result.select.query.alias");
		Integer execID = new Integer(json.getString("execId"));

		JSONObject jsonResponse = new JSONObject();
//		JSONArray jsonTableData = null;
//		String rowCount = "N/A";

//		String rowTotalCount = null;
		try
		{
			log.trace(robotName + ".Result." + "select.query");
			log.trace(RobotConfig.getConfig().toString());
			String query = RobotConfig.getConfig().get(robotName + ".Result." + "select.query") + " where execid=" + execID + dt.getWhereClause(true) + dt.getOrderByClause()
			// + " limit 7"
			+ dt.getLimitClause() + ";";
			log.info(query);
			jsonResponse = db.getTableData(query);
//			rowCount = db.getColumnValue("SELECT FOUND_ROWS();");
			//if ((dt.getStart() == null || dt.getLength() == null)) rowTotalCount = rowCount;
//			else 
//				rowTotalCount = db.getColumnValue("SELECT count(*) FROM " + RobotConfig.getConfig().get(robotName + ".Result." + "tableName") + " where execid=" + execID + " ;");
		}
		catch (SQLException e2)
		{
			log.error("Error fetching Results", e2);
			jsonResponse.put("error", "Error fetching Results");

		}
		finally
		{
		}

//		try
//		{
//			jsonResponse.put("draw", "");
//			jsonResponse.put("recordsFiltered", (rowCount));
//			jsonResponse.put("recordsTotal", (rowTotalCount));
//			jsonResponse.put("data", jsonTableData);
//			// jsonObject.put("id", "viewResultsTable");
//		}
//		catch (JSONException e)
//		{
//			log.error("Error inserting values in json", e);
//			try
//			{
//				jsonResponse.put("error", "error inserting values in json");
//			}
//			catch (JSONException e1)
//			{
//				log.error("Error inserting values in json", e1);
//			}
//		}
		log.trace(jsonResponse.toString());
		return jsonResponse.toString();
	}
}

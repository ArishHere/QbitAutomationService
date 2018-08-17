
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iss.qbit.datatable.DatatableParameter;

@RestController
public class AllExecutionsOld
{

	private static Logger	log	= LoggerFactory.getLogger(AllExecutionsOld.class);

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

	@PreAuthorize("@authenticator.result(#robotName)")
	@RequestMapping(value = "/v1/AllExecutions/{robotName}", method = RequestMethod.POST, produces = "application/json")
	public String allExecutions(@PathVariable String robotName,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);
		DatatableParameter dt = DatatableParameter.parse(json, ".Result.execution.query.alias");

		// String dataStart = request.getParameter("");
		// String dataLength = request.getParameter("length");
		// String robotName = request.getParameter("robotName");

		// String robotName = dt.getRobotName();

		// Integer dataLength = dt.getLength();
		// Integer dataStart = dt.getStart();

		JSONObject jsonResponse = null;
//		String rowCount = "N/A";

//		String rowTotalCount = null;
		try
		{
			String query = RobotConfig.getConfig().get(robotName + ".Result.execution.query") + dt.getWhereClause(true) + dt.getOrderByClause() + dt.getLimitClause() + ";";
			log.info(query);
			jsonResponse = db.getTableData(query);
//			rowTotalCount = db.getColumnValue(RobotConfig.getConfig().get(robotName + ".Result.execution.total.query"));
		}
		catch (SQLException e2)
		{
			log.error("Error fetching execution list.", e2);
		}
		finally
		{
		}
//
//		JSONObject jsonObject = new JSONObject();
//		try
//		{
//			jsonObject.put("draw", "");
//			jsonObject.put("recordsFiltered", (rowCount));
//			jsonObject.put("recordsTotal", (rowTotalCount));
//			jsonObject.put("data", jsonResponse);
//			// jsonObject.put("id", "viewResultsTable");
//		}
//		catch (JSONException e)
//		{
//			log.error("Error inserting values in json.", e);
//			try
//			{
//				jsonObject.put("error", "error inserting values in json");
//			}
//			catch (JSONException e1)
//			{
//				log.error("Error inserting values in json.", e1);
//			}
//		}
		log.debug("{}",jsonResponse);
		return jsonResponse.toString();
	}
}

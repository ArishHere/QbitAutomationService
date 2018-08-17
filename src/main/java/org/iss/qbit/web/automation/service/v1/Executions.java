
package org.iss.qbit.web.automation.service.v1;

import java.sql.SQLException;

import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iss.qbit.datatable.DatatableParameter;

@RestController(value="testExecutions")
@RequestMapping(value="/v1")
public class Executions
{

	private static Logger	log	= LoggerFactory.getLogger(Executions.class);

	private DBReader	db;

	@Autowired(required = true)
	public void setRobotConfig(DBReader db) throws SQLException
	{
		this.db = db;
	}

	@RequestMapping(value = "/Executions", method = RequestMethod.GET, produces = "application/json")
	public String allExecutions(@RequestBody String jsonS) throws JSONException
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

		JSONArray jsonResponse = null;
		String rowCount = "N/A";

		String rowTotalCount = null;
		try
		{
			String query = RobotConfig.getConfig().get(dt.getRobotName() + ".Result.execution.query") + dt.getWhereClause(true) + dt.getOrderByClause() + dt.getLimitClause() + ";";
			log.info(query);
			jsonResponse = db.getRowJSON(query);
			// + "where `ExecutionMaster`.`RobotName`='" + robotName + "'" + ((order == null) ? "" : " order by ExecId " + order) + " " + ((dataStart == null || dataLength == null) ? "" : "limit " + dataStart + "," + dataLength) + ";");
			rowCount = db.getColumnValue("SELECT FOUND_ROWS();");
			if ((dt.getStart() == null || dt.getLength() == null)) rowTotalCount = rowCount;
			else rowTotalCount = db.getColumnValue(RobotConfig.getConfig().get(dt.getRobotName() + ".Result.execution.total.query"));
		}
		catch (SQLException e2)
		{
			log.error("Error fetching execution list.", e2);
		}
		finally
		{
		}

		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject.put("draw", "");
			jsonObject.put("recordsFiltered", (rowCount));
			jsonObject.put("recordsTotal", (rowTotalCount));
			jsonObject.put("data", jsonResponse);
			// jsonObject.put("id", "viewResultsTable");
		}
		catch (JSONException e)
		{
			log.error("Error inserting values in json.", e);
			try
			{
				jsonObject.put("error", "error inserting values in json");
			}
			catch (JSONException e1)
			{
				log.error("Error inserting values in json.", e1);
			}
		}
		log.debug(jsonObject.toString());
		return jsonObject.toString();
	}
}


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
public class Config_Old
{

	private static Logger	log	= LoggerFactory.getLogger(Config_Old.class);

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

	@RequestMapping(value = "/v1/Config/{robotName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String config(@PathVariable String robotName,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);
		String configFor = json.getString("configFor");
		DatatableParameter dt = DatatableParameter.parse(json, ".Configuration." + configFor + ".select.query.alias");
		// String dataStart = request.getParameter("start");
		// String dataLength = request.getParameter("length");
		//
		// String robot = request.getParameter("robotName");

		JSONObject jsonResponse = null;
//		String rowCount = "N/A";
//
//		String rowTotalCount = null;
		try
		{
			String where = dt.getWhereClause(false);
			jsonResponse = db.getTableData("" + RobotConfig.getConfig().get(robotName + ".Configuration." + configFor + ".select.query") + (((where != null) && !where.isEmpty()) ? " where " + where : "") + dt.getOrderByClause() + dt.getLimitClause() + ";");
//			rowCount = db.getColumnValue("SELECT FOUND_ROWS();");
//			if (((dt.getStart() == null) || (dt.getLength() == null)))
//			{
//				rowTotalCount = rowCount;
//			}
//			else
//			{
//				rowTotalCount = db.getColumnValue("SELECT count(*) FROM " + RobotConfig.getConfig().get(robotName + ".Configuration." + configFor + ".tableName") + " ;");
//			}
		}
		catch (SQLException e2)
		{
			Config_Old.log.error("Error fetching configuration from database", e2);
		}
		finally
		{

		}
//		JSONObject jsonResponse = new JSONObject();
//		try
//		{
//			jsonResponse.put("draw", (dt.getDraw()));
//			jsonResponse.put("iTotalDisplayRecords", (rowCount));
//			jsonResponse.put("iTotalRecords", (rowTotalCount));
//			jsonResponse.put("data", jsonResult);
//			// jsonObject.put("aoColumns", columns);
//		}
//		catch (JSONException e)
//		{
//			Config.log.error("Error inserting values in json", e);
//			try
//			{
//				jsonResponse.put("error", "error inserting values in json");
//			}
//			catch (JSONException e1)
//			{
//				Config.log.error("Error inserting values in json", e1);
//			}
//		}
		Config_Old.log.debug(jsonResponse.toString());
		return jsonResponse.toString();
	}
}

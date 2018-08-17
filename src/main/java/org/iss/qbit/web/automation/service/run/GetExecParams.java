
package org.iss.qbit.web.automation.service.run;

import java.sql.SQLException;

import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/Run/{robotName}/param/{paramName}")
@Slf4j
public class GetExecParams
{


	private DBReader		db;

	@Autowired(required = true)
	public void setRobotConfig(DBReader db) throws SQLException
	{
		this.db = db;
	}

	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getParamValues(@PathVariable String robotName, @PathVariable String paramName, @RequestBody String jsonS) throws JSONException
	{
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);
		boolean status = false;
		paramName=paramName.toLowerCase();
		JSONObject jsonResponse = new JSONObject();

		String path = robotName + ".Run";

		try
		{
			String query = RobotConfig.getConfig().get(path + ".param." + paramName + ".query");
			String queryParams = RobotConfig.getConfig().get(path + ".param." + paramName + ".query.params");

			//			JSONObject execReq = new JSONObject();
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();
			if (queryParams != null && !queryParams.isEmpty())
			{
				for (String param : queryParams.split(","))
				{
					namedParameters.addValue(param, json.getString(param));
				}
			}

			//	    NamedParameterStatement query = new NamedParameterStatement(requestQuery);

			try
			{
				log.info("Param query :::: "+query);
				JSONArray resp = db.namedQuery(query, namedParameters,DBReader.jsonMapper());
				jsonResponse.put("data", resp);
			}
			finally
			{
			}

			status = true;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			status = false;
			GetExecParams.log.error("Error preparing execution request.", e);
		}

		try
		{
			jsonResponse.put("status", status);
		}
		catch (JSONException e1)
		{
			GetExecParams.log.error("Error inserting values in json", e1);
			try
			{
				jsonResponse.put("error", "error inserting values in json");
			}
			catch (JSONException e2)
			{
				GetExecParams.log.error("Error inserting error meaasge in json", e2);
			}
		}

		GetExecParams.log.trace("{}", jsonResponse);
		return jsonResponse.toString();
	}
}

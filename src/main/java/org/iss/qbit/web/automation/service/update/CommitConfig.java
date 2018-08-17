
package org.iss.qbit.web.automation.service.update;

import java.sql.SQLException;
import java.util.Iterator;

import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBWriter;
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
@Slf4j
public class CommitConfig
{


	private DBWriter		db;

	@Autowired(required = true)
	public void setRobotConfig(DBWriter db) throws SQLException
	{
		this.db = db;
	}

	@RequestMapping(value = "/v1/Update/Configuration/{robotName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String config(@PathVariable String robotName,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);

		if (CommitConfig.log.isTraceEnabled())
		{
			CommitConfig.log.trace(robotName + ".Configuration." + json.getString("configFor") + ".update.put.query");
		}
		String updateQuery = RobotConfig.getConfig().get(robotName + ".Configuration." + json.getString("configFor") + ".update.put.query");
		CommitConfig.log.trace(updateQuery);

		boolean status = false;
		if (updateQuery == null)
		{
			CommitConfig.log.error("NULL");
		}
		else
		{
			int updateCount;
			
			try
			{
				if (CommitConfig.log.isTraceEnabled())
				{
					CommitConfig.log.trace("query: " + updateQuery);
				}

				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				JSONObject queryJSON = json.getJSONObject("parm");
				for (Iterator<String> iterator = queryJSON.keys(); iterator.hasNext();)
				{
					String key = iterator.next();
					String value = queryJSON.getString(key);
					value = ((value.isEmpty()) ? null : value);
					CommitConfig.log.trace(key + ":" + value);
					namedParameters.addValue(key, value);
				}
				//		if (CommitConfig.log.isDebugEnabled())
				{
					CommitConfig.log.error("Query " + updateQuery);
					CommitConfig.log.error("Parms " + namedParameters);
				}
				updateCount = db.namedUpdateQuery(updateQuery, namedParameters);
				if (CommitConfig.log.isTraceEnabled())
				{
					CommitConfig.log.trace("Updated " + updateCount);
				}
				status = true;
			}
			catch (Exception e2)
			{
				CommitConfig.log.error("Error in updating configuration", e2);
				//jsonObject.put("error", "Error in updating configuration");
			}
			finally
			{
			}
		}

		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject.put("status", status);
		}
		catch (JSONException e)
		{
			CommitConfig.log.error("Error inserting values in json", e);
		}
		CommitConfig.log.trace(jsonObject.toString());
		return jsonObject.toString();

	}
}

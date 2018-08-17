
package org.iss.qbit.web.automation.service.bot.config;

import java.sql.SQLException;
import java.util.Iterator;

import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.iss.qbit.web.spring.db.DBWriter;
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
@Slf4j
public class ConfigEditor
{


	private DBWriter	db;

	@Autowired(required = true)
	public void setRobotConfig(DBWriter db) throws SQLException
	{
		this.db = db;
	}

	@RequestMapping(value = "/v2/Configuration/{robotName}/{configFor}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String get(@PathVariable String robotName,@PathVariable String configFor,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);
		String selectQuery = RobotConfig.getConfig().get(robotName + ".Configuration." + configFor + ".select.query");
		String queryCondition = RobotConfig.getConfig().get(robotName + ".Configuration." + configFor + ".update.get.condition");

		log.trace(selectQuery + " " + queryCondition);

		boolean status = false;
		if (selectQuery == null || queryCondition == null)
		{
			log.error("NULL");
			JSONObject jsonObject = new JSONObject();
			try
			{
				jsonObject.put("status", status);
				jsonObject.put("error", "Cant complete request");

			}
			catch (JSONException e)
			{
				log.error("Error inserting values in json", e);
				try
				{
					jsonObject.put("error", "error inserting values in json");
				}
				catch (JSONException e1)
				{
					log.error("Error inserting error meaasge in json", e);
				}
			}
			log.debug(jsonObject.toString());
			return jsonObject.toString();
		}
		else
		{
			selectQuery += " " + queryCondition;

			//            NamedParameterStatement query = null;
			JSONArray jsonResult = null;
			try
			{
				log.trace("query: " + selectQuery);
				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				
				String[] keys=RobotConfig.getConfig().get(robotName + ".Configuration." + configFor + ".key").split("-");
				String[] values=json.getString("key").split("-");
				if(keys.length!=values.length)
				throw new RuntimeException("Please provide all the required keys only");
				for (int i = 0; i < keys.length; i++)
				{
					namedParameters.addValue(keys[i], values[i]);
				}
				
//				JSONObject queryJSON = json.getJSONObject("parm");
//				for (Iterator<String> iterator = queryJSON.keys(); iterator.hasNext();)
//				{
//					String key = iterator.next();
//					String value = queryJSON.getString(key);
//					System.out.println(key + ":" + value);
//					namedParameters.addValue(key, ((value.isEmpty()) ? null : value));
//				}

				jsonResult = db.namedQuery(selectQuery, namedParameters, DBReader.toJSONArray);

				//                query = new NamedParameterStatement(selectQuery);
				//                JSONObject queryJSON = json.getJSONObject("parm");
				//                

				//                jsonResult = query.getRowJSON();
				status = true;
			}
			catch (Exception e2)
			{
				log.error("Error fetching configuration from database", e2);
			}
			finally
			{
				//                if (query != null) query.closeConnection();
			}
			JSONObject jsonResponse = new JSONObject();
			try
			{
				jsonResponse.put("result", jsonResult);
				// jsonObject.put("aoColumns", columns);
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
			log.debug(jsonResponse.toString());
			return jsonResponse.toString();
		}
	}

	@RequestMapping(value = "/v2/Configuration/{robotName}/{configFor}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String put(@PathVariable String robotName,@PathVariable String configFor,@RequestBody String jsonS) throws JSONException
	{
		// Actual logic goes here.
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);

		String updateQuery = RobotConfig.getConfig().get(robotName + ".Configuration." + configFor + ".update.put.query");
		log.trace(updateQuery);

		boolean status = false;
		if (updateQuery == null)
		{
			log.error("NULL");
		}
		else
		{
			int updateCount;
			
			try
			{
				if (log.isTraceEnabled())
				{
					log.trace("query: " + updateQuery);
				}

				MapSqlParameterSource namedParameters = new MapSqlParameterSource();
				JSONObject queryJSON = json.getJSONObject("value");
				for (Iterator<String> iterator = queryJSON.keys(); iterator.hasNext();)
				{
					String key = iterator.next();
					String value = queryJSON.getString(key);
					//value = ((value.isEmpty()) ? null : value);
					log.trace(key + ":" + value);
					namedParameters.addValue(key, value);
				}
				{
					log.info("Query " + updateQuery);
					log.info("Parms " + namedParameters.getValues());
				}
				updateCount = db.namedUpdateQuery(updateQuery, namedParameters);
				if (log.isTraceEnabled())
				{
					log.trace("Updated " + updateCount);
				}
				status = true;
			}
			catch (Exception e2)
			{
				log.error("Error in updating configuration", e2);
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
			log.error("Error inserting values in json", e);
		}
		log.trace(jsonObject.toString());
		return jsonObject.toString();

	}
}

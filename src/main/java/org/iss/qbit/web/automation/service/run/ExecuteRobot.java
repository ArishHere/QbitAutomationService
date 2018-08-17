
package org.iss.qbit.web.automation.service.run;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/Run/{robotName}")
@Slf4j
public class ExecuteRobot
{

//	private static Logger	log	= LoggerFactory.getLogger(ExecutionsResult.class);
	
//	org.apache.log4j.Logger l=org.apache.log4j.Logger.getLogger("org.iss.qbit.web.automation.service.view.ExecutionsResult");

	private DBWriter		db;

	@Autowired(required = true)
	public void setRobotConfig(DBWriter db) throws SQLException
	{
		this.db = db;
	}

	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String runRequest(@PathVariable String robotName, @RequestBody String jsonS) throws JSONException
	{
		log.warn(jsonS);
		JSONObject json = new JSONObject(jsonS);
		boolean status = false;

		JSONObject jsonResponse = new JSONObject();


		try
		{
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		      String name = auth.getName();
			String path = robotName + ".Run";
			int paraCount = Integer.parseInt(RobotConfig.getConfig().get(path + ".jar.parameters"));

			JSONObject execReq = new JSONObject();
			for (int i = 1; i <= paraCount; i++)
			{
				String dName = RobotConfig.getConfig().get(path + ".parameter." + i);
				if (dName != null)
				{
					execReq.put(dName.toLowerCase(), json.get(dName).toString());
				}
			}

			String requestQuery = "INSERT INTO `AutomationUI`.`ExecutionRequest` (`RobotName`,`RobotId`,`Recieved`,`Config`,`Status`,`RequestBy`,`Browser`,`Threads`) VALUES(:robotName,:robotId,:recieved,:config,:status,:RequestBy,:Browser,:Threads);";

			//	    NamedParameterStatement query = new NamedParameterStatement(requestQuery);
			MapSqlParameterSource namedParameters = new MapSqlParameterSource();

			try
			{
				namedParameters.addValue("robotName", RobotConfig.getConfig().get(robotName + ".RobotName"));
				namedParameters.addValue("robotId", robotName);
				namedParameters.addValue("recieved", new Date(new java.util.Date().getTime()), Types.DATE);
				namedParameters.addValue("config", "jar.parm=" + execReq.toString().replace("\\", "\\\\\\"));
				namedParameters.addValue("status", "WAITING");
				namedParameters.addValue("RequestBy", name);
				namedParameters.addValue("Browser", json.getString("Browser"));
				namedParameters.addValue("Threads", json.getString("Threads"));
//				l.trace("requestQuery");
				ExecuteRobot.log.trace("ExecutionRequest Parms {} Inserted {}" ,execReq, db.namedUpdateQuery(requestQuery, namedParameters));

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
			ExecuteRobot.log.error("Error preparing execution request.", e);
		}

		try
		{
			jsonResponse.put("status", status);
		}
		catch (JSONException e1)
		{
			ExecuteRobot.log.error("Error inserting values in json", e1);
			try
			{
				jsonResponse.put("error", "error inserting values in json");
			}
			catch (JSONException e2)
			{
				ExecuteRobot.log.error("Error inserting error meaasge in json", e2);
			}
		}

		ExecuteRobot.log.trace("{}", jsonResponse);
		return jsonResponse.toString();
	}
}

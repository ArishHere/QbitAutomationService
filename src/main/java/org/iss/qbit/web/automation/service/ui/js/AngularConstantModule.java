
package org.iss.qbit.web.automation.service.ui.js;

import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.iss.qbit.web.commons.utils.RobotConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AngularConstantModule
{

	//	@ModelAttribute
	//	public void setVaryResponseHeader(HttpServletResponse response) {
	//	    response.setHeader("x-custom", "value");
	//	    System.out.println("=================================== Response =======================================");
	//	    System.out.println(response);
	//	    System.out.println("====================================================================================");
	//	    
	//	}

	@RequestMapping(value = "/constant.js")
	public String getConfigString(Principal user)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Collection<String> roles = auth.getAuthorities().stream().map((a) -> a.getAuthority()).collect(Collectors.toList());

		return "angular.module('webUI.Constants', []).constant('Constants', {CONFIG: " + getConfig(roles) + ",USER: "+getUser(auth,roles)+"});";
	}

	@RequestMapping(value = { "/config.js", "/config" }, produces = "application/json")
	@ResponseBody
	public final String getConfigJson()
	{
		Collection<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map((a) -> a.getAuthority()).collect(Collectors.toList());
		return getConfig(roles).toString();
	}
	
	public final String getUser(Principal user, Collection<String> roles)
	{
		JSONObject obj= new JSONObject();
		obj.put("name", user.getName());
		JSONArray opt= new JSONArray();
		opt.put(new JSONObject("{name:'Logout',href:'/qbit/CAS/logout',icon:'img/icons/logout.svg'}"));
		if(roles.contains("ROLE_ADMIN"))
			opt.put(new JSONObject("{name:'Manage Accounts',href:'admin/manage.html',icon:'img/icons/folder-account.svg'}"));
		obj.put("options",opt);
		return obj.toString();
	}

	public final JSONArray getConfig(Collection<String> roles)
	{
//		Collection<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map((a) -> a.getAuthority()).collect(Collectors.toList());
		JSONArray jsonArray = new JSONArray();
		String key;
		String path="";
		for (String robot : RobotConfig.getConfig().get("robots").split(","))
		{
			try {
				JSONObject tab, json = new JSONObject();
				JSONArray tabs = new JSONArray();

				json.put("name", RobotConfig.getConfig().get(robot + ".RobotName"));
				json.put("id", robot);
				String[] types = { ".Run", ".Result", ".Configuration" };

				path = robot + types[0];
				// if (request.isUserInRole(RobotConfig.getConfig().get(robot + ".RolePrefix") + "-E") && RobotConfig.getConfig().containsKey(path))
				//			if (RobotConfig.getConfig().containsKey(path))
				if (roles.contains("ROLE_" + RobotConfig.getConfig().get(robot + ".id") + "-E") && RobotConfig.getConfig().containsKey(path))
				{
					tab = new JSONObject();
					tab.put("T", "Execution Dashboard");
					tab.put("t1", "E");

					key = "jar.parameters.json";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));

					tabs.put(tab);
				}

				path = robot + types[1];
				// if (request.isUserInRole(RobotConfig.getConfig().get(robot + ".RolePrefix")+"-R")&&RobotConfig.getConfig().containsKey(path))
				if (roles.contains("ROLE_" + RobotConfig.getConfig().get(robot + ".id") + "-R") && RobotConfig.getConfig().containsKey(path))
				{
					tab = new JSONObject();
					tab.put("T", "View Results");
					tab.put("t1", "R");

					key = "execution.DataTableColumn";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
					key = "select.DataTableColumn";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
					key = "compare.constant.DataTableColumn";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
					key = "compare.DataTableColumn";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
					
					key = "execution.datatable.options";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
					key = "select.datatable.options";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
					key = "compare.datatable.options";
					tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));

					tabs.put(tab);
				}

				path = robot + types[2];
				// if (request.isUserInRole(RobotConfig.getConfig().get(robot + ".RolePrefix")+"-C")&&RobotConfig.getConfig().containsKey(path))
				if (roles.contains("ROLE_" + RobotConfig.getConfig().get(robot + ".id") + "-C") && RobotConfig.getConfig().containsKey(path))
				{
					String[] configFor = StringUtils.split(RobotConfig.getConfig().get(path + ".configFor"),',');// .split(",");
					for (String robotConfigTypes : configFor)
					{
						tab = new JSONObject();
						tab.put("T", RobotConfig.getConfig().get(path + "." + robotConfigTypes + ".name"));
						tab.put("t1", "C");
						tab.put("t2", robotConfigTypes);
						key = "select.DataTableColumn";
						tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + robotConfigTypes + "." + key));
						
						key = "select.datatable.options";
						tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + robotConfigTypes + "." + key));

						tabs.put(tab);
					}
				}
				json.put("tabs", tabs);
				if (tabs.length() != 0) jsonArray.put(json);
			} catch (Exception e) {
				throw new RuntimeException("Error processing config for ["+robot +"] last path was ["+path+"]",e);
			}
		}
		System.out.println("returning Bots");
		return jsonArray;
	}

	//	@RequestMapping(value="/configA.js",produces="application/json")
	//	@ResponseBody
	//    public ArrayList<JSONObject> getConfigArray() {
	//		ArrayList<JSONObject> jsonArray = new ArrayList<JSONObject>();
	//		String key;
	//		for (String robot : RobotConfig.getConfig().get("robots").split(","))
	//		{
	//		    JSONObject tab, json = new JSONObject();
	//		    JSONArray tabs = new JSONArray();
	//
	//		    json.put("name", robot);
	//		    String[] types = { ".Run", ".Result", ".Configuration" };
	//
	//		    String path = robot + types[0];
	//		    // if (request.isUserInRole(RobotConfig.getConfig().get(robot + ".RolePrefix") + "-E") && RobotConfig.getConfig().containsKey(path))
	//		    if (RobotConfig.getConfig().containsKey(path))
	//		    {
	//		        tab = new JSONObject();
	//		        tab.put("T", "Execution Dashboard");
	//		        tab.put("t1", "E");
	//
	//		        key = "jar.parameters.json";
	//		        tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
	//
	//		        tabs.put(tab);
	//		    }
	//
	//		    path = robot + types[1];
	//		    // if (request.isUserInRole(RobotConfig.getConfig().get(robot + ".RolePrefix")+"-R")&&RobotConfig.getConfig().containsKey(path))
	//		    if (RobotConfig.getConfig().containsKey(path))
	//		    {
	//		        tab = new JSONObject();
	//		        tab.put("T", "View Results");
	//		        tab.put("t1", "R");
	//
	//		        key = "execution.DataTableColumn";
	//		        tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
	//		        key = "select.DataTableColumn";
	//		        tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
	//		        key = "compare.constant.DataTableColumn";
	//		        tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
	//		        key = "compare.DataTableColumn";
	//		        tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + key));
	//
	//		        tabs.put(tab);
	//		    }
	//
	//		    path = robot + types[2];
	//		    // if (request.isUserInRole(RobotConfig.getConfig().get(robot + ".RolePrefix")+"-C")&&RobotConfig.getConfig().containsKey(path))
	//		    if (RobotConfig.getConfig().containsKey(path))
	//		    {
	//		        String[] configFor = RobotConfig.getConfig().get(path + ".configFor").split(",");
	//		        for (String robotConfigTypes : configFor)
	//		        {
	//		            tab = new JSONObject();
	//		            tab.put("T", RobotConfig.getConfig().get(path + "." + robotConfigTypes + ".name"));
	//		            tab.put("t1", "C");
	//		            tab.put("t2", robotConfigTypes);
	//		            key = "select.DataTableColumn";
	//		            tab.put(key.replaceAll("\\.", "_"), RobotConfig.getConfig().get(path + "." + robotConfigTypes + "." + key));
	//
	//		            tabs.put(tab);
	//		        }
	//		    }
	//		    json.put("tabs", tabs);
	//		    jsonArray.add(json);
	//		}
	//		System.out.println("returning Bots "+jsonArray);
	//		return jsonArray;
	//    }
}

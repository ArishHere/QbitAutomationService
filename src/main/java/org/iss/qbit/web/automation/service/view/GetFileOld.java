
package org.iss.qbit.web.automation.service.view;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.NullInputStream;
import org.iss.qbit.web.commons.utils.RobotConfig;
import org.iss.qbit.web.spring.db.DBReader;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@RestController
//@RequestMapping("/v1/View")
@Slf4j
public class GetFileOld
{

	private static InputStream emptyStream=new NullInputStream(0);
	
	private DBReader	db;
	HttpHeaders headers = new HttpHeaders();
	
	
    
	//	private static HashMap<String, String> robotConfig;

	@Autowired(required = true)
	public void setRobotConfig(DBReader db) throws SQLException
	{
		this.db = db;
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
	    headers.add("Pragma", "no-cache");
	    headers.add("Expires", "0");
//	    headers.add("Expires", "0");
	}

	//	@Autowired(required=true)
	//	public void setRobotConfig(RobotConfig2 db) throws SQLException
	//	{
	//		robotConfig=db.getConfiguration();
	//	}
//	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/pdf")
//	public ResponseEntity<InputStreamResource> downloadPDFFile()
//	        throws IOException {
//
//	    ClassPathResource pdfFile = new ClassPathResource("pdf-sample.pdf");
//
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//	    headers.add("Pragma", "no-cache");
//	    headers.add("Expires", "0");
//	    
//	    return ResponseEntity
//	            .ok()
//	            .headers(headers)
//	            .contentLength(pdfFile.contentLength())
//	            .contentType(MediaType.parseMediaType("application/octet-stream"))
//	            .body(new InputStreamResource(pdfFile.getInputStream()));
//	}
	@RequestMapping(value = "/v1/get/{robotName}/file/{sid}", method = RequestMethod.GET)
	//    @GET
	//    @Path("/File")
	//    @Produces(MediaType.WILDCARD)
	public ResponseEntity<? extends Object> result(@PathVariable("sid") String sid, @PathVariable("robotName") String robot, HttpServletResponse response) throws JSONException, IOException
	{
		//        ResultSet rs = null;
		// log.trace(robot + ".Result." + "getFile");
		log.trace(RobotConfig.getConfig() + "");
		// log.trace(robot + ".Result." + "selectQuery");
		// log.trace(config.get(robot + ".Result." + "selectQuery"));
		String query = RobotConfig.getConfig().get(robot + ".Result." + "getFile");
		if (query == null)
		{
//			response.setContentType("text/html");
//			out.println("<html><head><title>File Not Found</title></head>");
//			out.println("<body><h1>Fatal Error.</h1></body></html>");
			return new ResponseEntity<String>("<html><head><title>File Not Found</title></head><body><h1>File not found in DB.</h1></body></html>",headers,HttpStatus.OK);
		}
		// String queryStatement = "select screenshot from `QSAutomation`.`SmokeTest` where  `SmokeTest`.`Id` = " + sid + ";";
		String queryStatement = query + sid + ";";

		try
		{
//			InputStreamResource l;
//			ResultSetExtractor<ResponseEntity<? extends Object>> a = ;
			return db.query(queryStatement,(rs) -> {
				try
				{
					if (rs.next() && rs.getString(1) != null)
					{

						int len = rs.getString(1).length();
						byte[] rb = new byte[len];
						InputStream readImg = rs.getBinaryStream(1);
						int index = readImg.read(rb, 0, len);
						log.info("index" + index);
						response.reset();
						response.getOutputStream().write(rb, 0, len);
						response.getOutputStream().flush();
//						return ResponseEntity
//			            .ok()
//			            .headers(headers)
//			            .contentLength(len)
////			            .contentType(MediaType.)
//			            .body(new InputStreamResource(rs.getBinaryStream(1)));
						return new ResponseEntity<InputStreamResource>(new InputStreamResource(emptyStream), headers, HttpStatus.OK);

					}

					else
					{
//						response.setContentType("text/html");
//						response.sendError(HttpStatus.SC_NO_CONTENT);
//						out.println("<html><head><title>File Not Found</title></head>");
//						out.println("<body><h1>No file found for id= " + sid + " </h1></body></html>");
//						return response;
						return new ResponseEntity<String>("<html><head><title>File Not Found</title></head><body><h1>No file found for id= " + sid + "</h1></body></html>",headers, HttpStatus.OK);

					}
				}
				catch (Exception e)
				{
					throw new RuntimeException("Exception fetching File from DB",e);
				}
			} );
		}
		catch (SQLException e)
		{
			
			log.error("Error fetching file.", e);
//			response.setContentType("text/html");
//			out.println("<html><head><title>Error: File Not Found</title></head>");
//			out.println("<body><h1>Error=" + e.getMessage() + "</h1></body></html>");
			return new ResponseEntity<String>("<html><head><title>Error: File Not Found</title></head><body><h1>No file found for id= " + sid + "</h1></body></html>",headers, HttpStatus.OK);
		}
		finally
		{

		}

	}
}

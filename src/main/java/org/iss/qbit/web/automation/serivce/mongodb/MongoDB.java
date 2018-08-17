
package org.iss.qbit.web.automation.serivce.mongodb;

import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CustomMongoTemplate;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Repository
public class MongoDB
{
	private CustomMongoTemplate mongoTemplate;
	
	@Autowired
	public MongoDB(CustomMongoTemplate mongoTemplate)
	{
		this.mongoTemplate=mongoTemplate;
	}
	
	public String findOne(CriteriaDefinition query)
	{
		return mongoTemplate.findOne(query(query), String.class);
	}
	
	public Stream<String> find(CriteriaDefinition query)
	{
		return mongoTemplate.findAsStream(query(query), String.class,"testResultRecord");
	}
	
	public List<String> findList(Query query)
	{
		return mongoTemplate.find(query, String.class,"testResultRecord");
	}
	
	public Stream<Object> find(Query query)
	{
		return mongoTemplate.findAsStream(query, Object.class,"testResultRecord");
	}
}

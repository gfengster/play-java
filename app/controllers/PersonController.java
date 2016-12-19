package controllers;

import static play.libs.Json.toJson;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import models.Person;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;

public class PersonController extends Controller {
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX 
					= Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	
	private final FormFactory formFactory;
	
	private DBCollection table = null;
	
	@Inject
    public PersonController(FormFactory formFactory) {
    	this.formFactory = formFactory;
    	
    	MongoClient client = null;
    	DB db = null;
    	
    	try {
    		client = new MongoClient("localhost");
    		db = client.getDB("test");
    		
    		if (!db.collectionExists("person")){
    			db.createCollection("person", null);
    		}
    		table = db.getCollection("person");
    	} catch (UnknownHostException e) {
			e.printStackTrace();
			
			if (client!= null)
				client.close();
		}
    }

    public Result index() {
        return ok(views.html.index.render());
    }
    
    public Result searchPerson() {
        return ok(views.html.search.render());
    }
    
    public Result error() {
        return ok(views.html.error.render());
    }

    public Result errordb() {
        return ok(views.html.errordb.render());
    }

    public Result addPerson() {
    	
    	if (table == null)
    		return ok(views.html.errordb.render());
    	
    	final Person person = formFactory.form(Person.class).bindFromRequest().get();
    	
    	final Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(person.email);
        
    	if (!matcher.find())
    		return redirect(routes.PersonController.error());
    	
    	
    	final BasicDBObject obj = new BasicDBObject();
    	
    	obj.put("name", person.name);
    	obj.put("email", person.email);	
    	
    	table.insert(obj);
    	    	
        return redirect(routes.PersonController.index());
    }


    public Result getPersons() {
    	if (table == null)
    		return ok(views.html.errordb.render());
    	
    	final List<Person> persons = new LinkedList<>();
    	final DBCursor cursor = table.find();
    	
    	while(cursor.hasNext()) {
    		final DBObject obj = cursor.next();
    		final Person p = new Person();
    		p.name = obj.get("name").toString();
    		p.email = obj.get("email").toString();
    		
    		persons.add(p);
    	}
    	
        return ok(toJson(persons));
    }
    
    public Result search() {
    	if (table == null)
    		return ok(views.html.errordb.render());
    	
    	final Person person = formFactory.form(Person.class).bindFromRequest().get();
    	final BasicDBObject obj = new BasicDBObject();
    	
    	obj.put("name", person.name);
    	
    	final List<Person> persons = new LinkedList<>();
    	final DBCursor cursor = table.find(obj);
    	
    	while(cursor.hasNext()) {
    		final DBObject o = cursor.next();
    		final Person p = new Person();
    		p.name = o.get("name").toString();
    		p.email = o.get("email").toString();
    		
    		persons.add(p);
    	}
    	
        return ok(toJson(persons));
    }
}

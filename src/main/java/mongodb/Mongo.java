package mongodb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.util.JSON;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;


public class Mongo {
	
	static BufferedReader br;
	static DBCollection collection;
	
	public static void main(String args[]) throws IOException {
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB( "mydb" );
		collection = db.getCollection("carCollection");
		
        br = new BufferedReader(new InputStreamReader(System.in));
        boolean loop = true;
        while(loop) {
	        int choice1 = selectionMenu();
	        String car;
        
        
	        switch(choice1) {
	        	case 1: 
	        		car = createCarJson();
	                System.out.println(car);
	
	        		DBObject dbObject = (DBObject)JSON.parse(car);
	        		collection.insert(dbObject);
	        		break;
	        	case 2:
	        		getCars();
	        		break;
	        	case 3:
	        		collection.drop();
	        		break;
	        		
	        	default: System.out.print("Invalid choice. Shutting down."); loop = false; break;
	        }
        }

	}
	private static void getCars() throws JsonParseException, JsonMappingException, IOException {

		DBCursor cursorDocJSON = collection.find(new BasicDBObject(), 
                new BasicDBObject("_id", 0));
		System.out.println("MAKE|MODEL|MSRP|TYPE|WEIGHT|DATE_CREATED");
		while (cursorDocJSON.hasNext()) {
			ObjectMapper mapper = new ObjectMapper();
			 mapper.registerModule(new JodaModule());
			StringBuilder sb = new StringBuilder();
			Car car = mapper.readValue(cursorDocJSON.next().toString(), Car.class);
			sb.append(car.getMake());
			sb.append("|");
			sb.append(car.getModel());
			sb.append("|");
			sb.append(car.getMsrp());
			sb.append("|");
			sb.append(car.getType());
			sb.append("|");
			sb.append(car.getWeight());
			sb.append("|");
			sb.append(car.getDt().getYear());
			sb.append("/");
			sb.append(car.getDt().getMonthOfYear());
			sb.append("/");
			sb.append(car.getDt().getDayOfMonth());
			System.out.println(sb);
		}
		
	}
	private static int selectionMenu() throws NumberFormatException, IOException {

		// Display menu graphics
		System.out.println("============================");
		System.out.println("|   MENU SELECTION DEMO    |");
		System.out.println("============================");
		System.out.println("| Options:                 |");
		System.out.println("|        1. Create new car |");
		System.out.println("|        2. View cars      |");
		System.out.println("|        3. Clear all	   |");
		System.out.println("============================");
		System.out.println(" Select option: ");
		return Integer.parseInt(br.readLine());
	}
	
	private static String createCarJson() throws IOException{
        System.out.print("Enter make:");
        String make = br.readLine();
        System.out.print("Enter model:");
        String model = br.readLine();
        System.out.print("Enter type:");
        String type = br.readLine();
        System.out.print("Enter msrp (int):");
        long msrp = Long.parseLong(br.readLine());
        System.out.print("Enter weight:");
        int weight = Integer.parseInt(br.readLine());
        
        Car car = new Car();
        car.setMake(make);
        car.setModel(model);
        car.setMsrp(msrp);
        car.setType(type);
        car.setWeight(weight);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule()).writerWithDefaultPrettyPrinter();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);        
        String jsonString = mapper.writeValueAsString(car);

        return jsonString;
	}
}
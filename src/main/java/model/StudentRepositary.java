package model;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import controller.StudentServices;

public class StudentRepositary {

	private MongoCollection<Document> collection;
	Student student;
	StudentServices studentService;

	public StudentRepositary() {

		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);

			MongoDatabase database = mongoClient.getDatabase("StudentDataBase");

			collection = database.getCollection("Students");
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public boolean checkIfStudentExists(int id) {

		// create query with id
		BasicDBObject query = new BasicDBObject("id", id);

		// check the id is matching in db
		Document result = collection.find(query).first();

		return result != null;

	}

	public void addStudent(Student student) {

		try {

			Document studentDocument = new Document("id", student.getId()).append("name", student.getName())
					.append("age", student.getAge());

			collection.insertOne(studentDocument);
		} catch (MongoException e) {
			e.printStackTrace();

		}

	}
	
	public Student getStudentById(int studentId,HttpServletResponse resp) throws IOException {
		
		
		Document query = new Document ("id",studentId);
		
		Document result= collection.find(query).first();
		System.out.println(result);
		
			
			int id=result.getInteger("id");
			String name=result.getString("name");
			int age=result.getInteger("age");
		
		return new Student(id,name,age);
		
		
	
		
		
		
		
		
	}

}

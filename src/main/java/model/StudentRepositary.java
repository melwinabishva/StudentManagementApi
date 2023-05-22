package model;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import controller.StudentServices;

public class StudentRepositary {

	private MongoCollection<Document> collection;
	private Student student;
	private StudentServices studentService;

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
		if (result!=null) {
			return true;
		} else {
        return false;
		}
//		return result;

	}

	public void addStudent(Gson gson, Student student,HttpServletResponse resp) throws IOException {

		try {
			
          Document lastStuentDocument=collection.find().sort(new Document("id",-1)).limit(1).first();
			
            int lastId=0;
            
            if(lastStuentDocument !=null) {
          	  lastId=lastStuentDocument.getInteger("id");
            }
            int nextId=lastId+1;
            
            //Generate the UUID 
            UUID generatedUUID=UUID.randomUUID();
            
            
            //change the format
            int formattedId=nextId;
            
            student.setId(formattedId);
              
              
			Document studentDocument = new Document("id", student.getId()).append("name", student.getName())
					.append("age", student.getAge());

			collection.insertOne(studentDocument);
			Student insertedStudent = gson.fromJson(studentDocument.toJson(), Student.class);
			 PrintWriter writer = resp.getWriter();
		        writer.println(gson.toJson(insertedStudent));
			
//			return studentDocument;
		} catch (MongoException e) {
			e.printStackTrace();

		}
		

	}

	public Student getStudentById(int studentId, HttpServletResponse resp) throws IOException {

		Document query = new Document("id", studentId);

		Document result = collection.find(query).first();

		int id = result.getInteger("id");
		String name = result.getString("name");
		int age = result.getInteger("age");
		
		

		return new Student(id, name, age);

	}
	
	public List<Student> getAllStudents(){
		
		List<Student> students=new ArrayList();
		
		FindIterable<Document> studentDocuments=collection.find();
		
		for(Document doc: studentDocuments) {
			Student student=documentToStudent(doc);
			students.add(student);
		}
		return students;
	}
	private Student documentToStudent(Document doc) {
        int id = doc.getInteger("id");
        String name = doc.getString("name");
        int age = doc.getInteger("age");
       
        return new Student(id, name, age);
    }

	public void updateStudent(int id, Student updatedStudent, Gson gson,HttpServletResponse resp) throws IOException {
		
		Document fillter =new Document("id",id);
		
	   Document updateQuery=new Document("$set",new Document("name", updatedStudent.getName())
			   .append("age", updatedStudent.getAge()));
		collection.updateOne(fillter, updateQuery);
		
		Student insertedStudent = gson.fromJson(updateQuery.toJson(), Student.class);
		
		Document updatedDocument = collection.find(fillter).first();
		
		if(updatedDocument!=null) {
		
		 PrintWriter writer = resp.getWriter();
	        writer.println(gson.toJson(insertedStudent));
		}
		
//		
	}

	public void deleteStudent(int id) {
		
		Document filter = new Document("id", id);
		
		collection.deleteOne(filter);
		
	}
	
	
	

}

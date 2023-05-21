package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import model.Student;
import model.StudentRepositary;

@WebServlet(name = "StudentService", urlPatterns = { "/hello" })

public class StudentServices extends HttpServlet {

	public StudentRepositary studentRepository;

	@Override
	public void init() throws ServletException {

		studentRepository = new StudentRepositary();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			// get the student id from the url path
			String pathInfo = req.getPathInfo();

			if (pathInfo == null || pathInfo.isEmpty()) {
				sendErrorResponse(resp, "Missing Student id");

				return;
			}
			String[] pathParts = pathInfo.split("/");

			if (pathParts.length >= 2) {
				String studentId = pathParts[1];

				
				int id = Integer.parseInt(studentId);
				
				boolean studentExist = studentRepository.checkIfStudentExists(id);
				if (!studentExist) {
					resp.setStatus(HttpServletResponse.SC_CONFLICT);
					resp.getWriter().write("Student  ID not Available");
				} else {

					// Reterive the student information form the database
					Student student = studentRepository.getStudentById(id, resp);

					// convert the student object to JSON
					Gson gson = new Gson();
					String json = gson.toJson(student);

					// send the Json response
					resp.setContentType("application/json");
					resp.getWriter().write(json);
				}
			} else {
				sendErrorResponse(resp, "Invalid URL path");
			}

		} catch (IOException e) {

			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("internal sever error occurred");

		}catch(NumberFormatException e) {
			sendErrorResponse(resp, "Enter valid number");
            return;

			
		}

	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			// Rertrive the JSON data from req
			BufferedReader read = req.getReader();
			StringBuilder requestBody = new StringBuilder();
			String line;
			while ((line = read.readLine()) != null) {
				requestBody.append(line);
			}
			read.close();
			String userData = requestBody.toString();

			// Change the JSON data into student obj
			Gson gson = new Gson();
			Student newStudent = gson.fromJson(userData, Student.class);

			// check the students already exists
			boolean studentExist = studentRepository.checkIfStudentExists(newStudent.getId());

			if (studentExist) {
				resp.setStatus(HttpServletResponse.SC_CONFLICT);
				resp.getWriter().write("Student  ID already exists");
			} else {
				// Insert the new student into database
				studentRepository.addStudent(newStudent);

				resp.setStatus(HttpServletResponse.SC_CREATED);
				resp.getWriter().write("Student created successfully");

			}
		} catch (IOException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("put");
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		System.out.println("delete");
	}

	public void sendErrorResponse(HttpServletResponse resp, String message) throws IOException {
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		resp.setContentType("text/plain");
		resp.getWriter().write(message);
	}

}

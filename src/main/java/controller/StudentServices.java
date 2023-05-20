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

		System.out.println("get");

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

}

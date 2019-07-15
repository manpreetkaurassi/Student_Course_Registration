package dbadmin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@EnableAutoConfiguration
public class ViewApplication {

	static String CONSTANT_HOST = "courseregistration-cluster.cluster-cnkddx6wiq36.us-east-1.rds.amazonaws.com";
	static String CONSTANT_DBNAME = "courseregistration";
	static String CONSTANT_USERNAME = "dbadmin1";
	static String CONSTANT_PASSWORD = "dbadmin1";
	static String CONSTANT_PORT = "3306";

	@CrossOrigin(origins = "http://localhost:8080")
	@RequestMapping(value = "getdata", method = RequestMethod.GET)
	String getitem(@RequestParam("studentid") String student_id, Model model) {
		System.out.println(student_id);

		Connection connection = getRemoteConnection();
		Statement statement = null;
		ArrayList<Registration> regList = new ArrayList<Registration>();
		ArrayList<String> subjectIDList = new ArrayList<String>();
		ArrayList<Subject> subjectList = new ArrayList<Subject>();
		Student student = new Student();
		try {
			statement = connection.createStatement();
			ResultSet studentresults = statement
					.executeQuery("SELECT * FROM student WHERE student_id = " + student_id + ";");
			while (studentresults.next()) {
				int studentID = studentresults.getInt(1);
				String studentName = studentresults.getString(2);
				student.setStudentID(Integer.toString(studentID));
				student.setStudentName(studentName);
			}
			statement = connection.createStatement();
			ResultSet registrationresults = statement
					.executeQuery("SELECT * FROM registration WHERE student_id = " + student_id + ";");
			while (registrationresults.next()) {
				int regID = registrationresults.getInt(1);
				int subjectID = registrationresults.getInt(3);
				Registration reg = new Registration();
				reg.setRegID(Integer.toString(regID));
				reg.setSubjectID(Integer.toString(subjectID));
				reg.setStudentID(student_id);
				regList.add(reg);
				subjectIDList.add(Integer.toString(subjectID));
			}
			for (int i = 0; i < subjectIDList.size(); i++) {
				String currentSubjectID = subjectIDList.get(i);
				statement = connection.createStatement();
				ResultSet subjectresults = statement
						.executeQuery("SELECT * FROM subject WHERE subject_id = " + currentSubjectID + ";");
				while (subjectresults.next()) {
					Subject subject = new Subject();
					int subjectID = subjectresults.getInt(1);
					String subjectCode = subjectresults.getString(2);
					String subjectName = subjectresults.getString(3);
					subject.setSubjectID(Integer.toString(subjectID));
					subject.setSubjectCode(subjectCode);
					subject.setSubjectName(subjectName);
					subjectList.add(subject);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Map<String, Subject> viewMap = new HashMap<String, Subject>();
		for (int i = 0; i < subjectList.size(); i++) {
			Subject currentSubject = subjectList.get(i);
			viewMap.put(currentSubject.getSubjectID(), currentSubject);
		}
		Subject studentDetails = new Subject();
		studentDetails.setSubjectID(student.getStudentID());
		studentDetails.setSubjectName(student.getStudentName());
		Gson gson = new Gson();
		String jsonData = gson.toJson(viewMap);
		model.addAttribute("studentID", student.getStudentID());
		model.addAttribute("studentName", student.getStudentName());
		model.addAttribute("jsonData", jsonData);
		return jsonData;
	}

	private static Connection getRemoteConnection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			String dbName = CONSTANT_DBNAME;
			String userName = CONSTANT_USERNAME;
			String password = CONSTANT_PASSWORD;
			String hostname = CONSTANT_HOST;
			String port = CONSTANT_PORT;
			String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password="
					+ password;
			con = DriverManager.getConnection(jdbcUrl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return con;
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ViewApplication.class, args);
	}

}
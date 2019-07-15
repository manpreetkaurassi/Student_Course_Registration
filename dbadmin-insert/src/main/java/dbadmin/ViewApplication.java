package dbadmin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class ViewApplication {

	static String CONSTANT_HOST = "courseregistration-cluster.cluster-cnkddx6wiq36.us-east-1.rds.amazonaws.com";
	static String CONSTANT_DBNAME = "courseregistration";
	static String CONSTANT_USERNAME = "dbadmin1";
	static String CONSTANT_PASSWORD = "dbadmin1";
	static String CONSTANT_PORT = "3306";

	@CrossOrigin(origins = "http://localhost:8080")
	@RequestMapping(value = "insertdata", method = RequestMethod.GET)
	String getitem(@RequestParam("studentid") String student_id, @RequestParam("subjectid") String subject_id,
			Model model) {
		System.out.println(student_id);
		System.out.println(subject_id);
		Connection connection = getRemoteConnection();
		Statement statement = null;
		String status = "";
		try {
			statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO registration (reg_id, student_id, subject_id) VALUES " + "("
					+ Integer.parseInt(student_id + subject_id) + " , " + Integer.parseInt(student_id) + " , "
					+ Integer.parseInt(subject_id) + ");");
			status = "Course successfully registered!";
		} catch (SQLException e) {
			status = "Registration failed, please retry!";
		}
		return status;
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
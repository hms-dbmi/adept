package edu.harvard.hms.dbmi.avillach.cliniscope;

import java.security.SecureRandom;
import java.util.Base64;

import javax.sql.DataSource;

import org.junit.Test;

public class UserRepositoryTest {
	@Test
	public void testGetUserByUsername(){
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		System.out.println(Base64.getEncoder().encodeToString(key));
//		DataSource ds = new DriverManagerDataSource("jdbc:hsqldb:mem:cliniscope", "sa", "");
		
//		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
//		
//		jdbcTemplate.execute("CREATE TABLE User (id int, username varchar(256))");
//		jdbcTemplate.execute("INSERT INTO User VALUES (1,'foo')");
//		jdbcTemplate.execute("CREATE PROCEDURE READ_USER_BY_NAME(IN name VARCHAR(16), INOUT id INT, INOUT username VARCHAR(16)) READS SQL DATA BEGIN ATOMIC  set id=(SELECT id FROM User limit 1); set username=(select username FROM User limit 1); END; COMMIT;");
////		new UserRepository(jdbcTemplate).getUserByUsername("foo");
	}
}

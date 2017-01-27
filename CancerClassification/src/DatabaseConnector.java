
import java.sql.*;

public class DatabaseConnector {

	private String url;
	private String user;
	private String pwd;
	private String database;
	private String schema;
	private Connection con;

	public DatabaseConnector(String host, String port, String database, String schema, String pwd, String user){
		
            url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
		this.database = database;
		this.pwd = pwd;
		this.user = user;
		this.schema = schema;
                
		// check for jdbc-driver
		try {
			Class.forName("org.postgresql.Driver");
			System.err.println("Driver found.");
		} catch (ClassNotFoundException e) {
			System.err.println("PostgreSQL JDBC Driver not found ... ");
			e.printStackTrace();
			return;
		}
		// establish connection
		try {
			con = DriverManager.getConnection(url, user, pwd);
			System.err.println("Connection established.");
		} catch (Exception e) {
			System.err.println("Could not establish connection.");
			e.printStackTrace();
			return;
		}
	}

        public Connection getConnection() throws Exception {
		return con;
	}

	public void getTableNames() {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			String[] types = {"TABLE"};
			ResultSet rs = dbmd.getTables(null, schema, "%", types);
			System.out.println("Query sucessful.\n---\n" +
					"List of tables in database '" +
					database + "' in schema '" + schema + "':");
			while (rs.next()) {
				System.out.println("- " + rs.getString("TABLE_NAME"));
			}
			System.out.println("----");
		}
		catch (SQLException e) {
		}
	}
	
}

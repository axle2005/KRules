package net.kaikk.mc.krules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;

public class DataStore {
	private KRules instance;
	private String dbUrl;
	private String username;
	private String password;
	protected Connection db = null;
	
	DataStore(KRules instance, String url, String username, String password) throws Exception {
		this.instance=instance;
		this.dbUrl = url;
		this.username = username;
		this.password = password;

		try {
			//load the java driver for mySQL
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			this.instance.log(Level.SEVERE, "KRULES: Unable to load Java's mySQL database driver.  Check to make sure you've installed it properly.");
			throw e;
		}
		
		
		try {
			this.dbCheck();
		} catch(Exception e) {
			this.instance.log(Level.SEVERE, "KRULES: Unable to connect to database.  Check your config file settings.");
			throw e;
		}
		
		try {
			Statement statement = db.createStatement();

			// Creates the table on the database
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS krules (player binary(16) NOT NULL, PRIMARY KEY (`player`));");
		} catch(Exception e) {
			this.instance.log(Level.SEVERE, "KRULES: Unable to create the necessary database tables. Details:");
			throw e;
		}
		
	}
	
	synchronized void dbCheck() throws SQLException {
		if (this.db == null || this.db.isClosed()) {
			Properties connectionProps = new Properties();
			connectionProps.put("user", this.username);
			connectionProps.put("password", this.password);
			
			this.db = DriverManager.getConnection(this.dbUrl, connectionProps); 
		}
	}
	
	synchronized boolean hasPlayerAgreedWithRules(UUID uuid) {
		// fake players check
		if (instance.isFakePlayer(uuid)) {
			return true;
		}
		
		// cache
		Boolean cache = instance.cache.get(uuid);
		if (cache!=null) {
			return cache;
		}
		
		// check the database
		try {
			this.dbCheck();
			
			Statement statement = this.db.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT player FROM krules WHERE player = "+UUIDtoHexString(uuid));
			boolean res = resultSet.next();
			instance.cache.put(uuid, res);
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	synchronized void addPlayerToAgreed(UUID uuid) {
		try {
			this.dbCheck();
			this.db.createStatement().executeUpdate("INSERT IGNORE INTO krules VALUES("+UUIDtoHexString(uuid)+")");
			instance.cache.put(uuid, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static UUID toUUID(byte[] bytes) {
	    if (bytes.length != 16) {
	        throw new IllegalArgumentException();
	    }
	    int i = 0;
	    long msl = 0;
	    for (; i < 8; i++) {
	        msl = (msl << 8) | (bytes[i] & 0xFF);
	    }
	    long lsl = 0;
	    for (; i < 16; i++) {
	        lsl = (lsl << 8) | (bytes[i] & 0xFF);
	    }
	    return new UUID(msl, lsl);
	}
	
	public static String UUIDtoHexString(UUID uuid) {
		if (uuid==null) return "0x0";
		return "0x"+StringUtils.leftPad(Long.toHexString(uuid.getMostSignificantBits()), 16, "0")+StringUtils.leftPad(Long.toHexString(uuid.getLeastSignificantBits()), 16, "0");
	}
}

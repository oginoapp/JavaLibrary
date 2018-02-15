package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBとの接続を取得するクラス
 *
 * ConnectionInfo info = new ConnectionInfo();
 * info.connectionString = DBConnection.DEFAULT_CONNECTIONSTRING_MYSQL;
 * info.username = "root";
 * info.password = "";
 * DBConnection.setConnectionInfo(info);
 * Connection conn = DBConnection.getConnection(true);
 */
public class DBConnection{

	public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	public static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";

	public static final String DEFAULT_CONNECTIONSTRING_MYSQL = "jdbc:mysql://localhost:3306/test"
			+ "?useSSL=false"
			+ "&requireSSL=false"
			+ "&zeroDateTimeBehavior=convertToNull"
			+ "&characterEncoding=utf8";
	public static final String DEFAULT_CONNECTIONSTRING_ORACLE = "jdbc:oracle:thin:@localhost:1521:XE";

	private static Connection conn = null;
	private static ConnectionInfo info = null;

	public static class ConnectionInfo {
		public String driver = DRIVER_MYSQL;
		public String connectionString = DEFAULT_CONNECTIONSTRING_MYSQL;
		public String username = null;
		public String password = null;
	}

    /* プライベートコンストラクタ */
	private DBConnection() { }

	/**
	 * DBの接続情報を設定する
	 * @param info DBの接続情報
	 */
	public static void setConnectionInfo(ConnectionInfo info) {
		DBConnection.info = info;
	}

	/**
	 * DBとの接続を取得する
	 * @param newConnection 既存の接続オブジェクトに上書きするかどうか
	 * @return 接続オブジェクト
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		return getConnection(false);
	}
	public static Connection getConnection(boolean newConnection) throws SQLException, ClassNotFoundException {
		if(newConnection){
			close();
		}

		if(conn == null || conn.isClosed()){
			Class.forName(info.driver);
			Connection conn = DriverManager.getConnection(
					info.connectionString, info.username, info.password);
			DBConnection.conn = conn;
		}

		return conn;
	}

	/**
	 * DBとの接続を明示的に閉じる
	 */
	public static void close() {
		try {
			if(conn != null) conn.close();
		} catch (SQLException e) { }
	}

}

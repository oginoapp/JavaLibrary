package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBとの接続を取得するクラス（シングルトン実装）
 */
public class DBConnection{
	private static Connection conn = null;
	private static long lastConnectTime = 0;
	private static long reConnectInterval = 1000 * 60 * 60 * 1;
	private static ConnectionInfo info = null;

	public static class ConnectionInfo{
		public String host = "localhost";
		public int port = 3306;
		public String database = null;
		public String username = null;
		public String password = null;
	}

    /* プライベートコンストラクタ */
	private DBConnection(){}

	/**
	 * DBの接続情報を設定する
	 * @param info DBの接続情報
	 */
	public static void setConnectionInfo(ConnectionInfo info){
		DBConnection.info = info;
	}

	/**
	 * DBとの接続を取得する
	 * @param newConnection 既存の接続オブジェクトに上書きするかどうか
	 * @return 接続オブジェクト
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection() throws SQLException, ClassNotFoundException{
		return getConnection(false);
	}
	public static Connection getConnection(boolean newConnection) throws SQLException, ClassNotFoundException{
		if(System.currentTimeMillis() > lastConnectTime + reConnectInterval){
			newConnection = true;
		}

		if(conn == null || conn.isClosed() || newConnection){
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://" + info.host + ":" + info.port + "/" + info.database
					+ "?useSSL=false"
					+ "&requireSSL=false"
					+ "&zeroDateTimeBehavior=convertToNull"
					+ "&characterEncoding=utf8"
					,info.username, info.password);
			DBConnection.conn = conn;
            lastConnectTime = System.currentTimeMillis();
		}

		return conn;
	}

	/**
	 * DBとの接続を閉じる
	 * @throws SQLException
	 */
	public static void close() throws SQLException{
		if(conn != null) conn.close();
	}
}

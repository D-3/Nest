package ml.that.pigeon.conn;

import java.io.IOException;
import java.net.Socket;

import ml.that.pigeon.util.LogUtils;

/**
 * Creates a socket connection to a JT/T808 server. To create a connection to a JT/T808 server a
 * simple usage of this API might looks like the following:
 * <p>
 * <pre>
 *   // Create a configuration for new connection
 *   ConnectionConfiguration cfg = new ConnectionConfiguration("10.1.5.21", 29930);
 *   // Create a connection to the JT/T808 server
 *   Connection conn = new Connection(cfg);
 *   // Connect to the server
 *   conn.connect();
 *   // JT/T808 servers require you to login before performing other tasks
 *   conn.login();
 *   // Create a message to send
 *   Message msg = new Message.Builder().build();
 *   // Send the server a message
 *   conn.sendMessage(msg);
 *   // Disconnect from the server
 *   conn.disconnect();
 * </pre>
 * <p>
 * Connection can be reused between connections. This means that a Connection may be connected,
 * disconnected, and then connected again. Listeners of the Connection will be retained across
 * connections.
 * <p>
 * If a connected Connection gets disconnected abruptly and automatic reconnection is enabled
 * ({@link ConnectionConfiguration#isReconnectionAllowed()}, the default), then it will try to
 * reconnect again. To stop the reconnection process, use {@link #disconnect()}. Once stopped you
 * can use {@link #connect()} to manually connect to the server.
 *
 * @author ThatMrL (thatmr.l@gmail.com)
 */
public class Connection {

  private static final String TAG = LogUtils.makeTag(Connection.class);

  // Holds the initial configuration used while creating the connection
  private ConnectionConfiguration mConfig;

  // The socket which is used for this connection
  private Socket mSocket;

  private boolean mConnected = false;

  /**
   * Creates a new connection to a JT/T808 server.
   *
   * @param cfg the configuration which is used to establish the connection
   */
  public Connection(ConnectionConfiguration cfg) {
    mConfig = cfg;
  }

  /**
   * Establishes a connection to the JT/T808 server and performs an automatic login only if the
   * previous connection state was logged (authenticated). It basically creates and maintains a
   * connection to the server.
   * <p>
   * Listeners will be preserved from a previous connection.
   */
  public void connect() throws IOException {
    mSocket = new Socket(mConfig.getHost(), mConfig.getPort());
    // TODO: 10/23/2016 finish this method
  }

  /**
   * Closes the connection. The Connection can still be used for connecting to the server again.
   */
  public void disconnect() {
    // TODO: 10/23/2016 implement this method
  }

  /**
   * Returns the configuration used to connect to the server.
   *
   * @return the configuration used to connect to the server
   */
  public ConnectionConfiguration getConfig() {
    return mConfig;
  }

  /**
   * Returns true if currently connected to the JT/T808 server.
   *
   * @return true if connected
   */
  public boolean isConnected() {
    return mConnected;
  }

}

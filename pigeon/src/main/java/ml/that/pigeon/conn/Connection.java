package ml.that.pigeon.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import ml.that.pigeon.msg.Message;
import ml.that.pigeon.util.LogUtils;

/**
 * Creates a socket connection to a JT/T808 server.
 * <p>
 * To create a connection to a JT/T808 server a simple usage of this API might looks like the
 * following:
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
  private Socket        mSocket;
  private InputStream   mInput;
  private OutputStream  mOutput;
  private MessageReader mReader;
  private MessageWriter mWriter;

  private boolean mConnected = false;

  // mSocketClosed is used concurrent by Connection, MessageReader, MessageWriter
  private volatile boolean mSocketClosed = false;

  /**
   * Creates a new JT/T808 connection using the specified connection configuration.
   * <p>
   * Note that Connection constructors do not establish a connection to the server and you must call
   * {@link #connect()}.
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
    mSocketClosed = false;
    initConnection();
  }

  /**
   * Closes the connection. The Connection can still be used for connecting to the server again.
   */
  public void disconnect() {
    // TODO: 10/23/2016 implement this method
  }

  public void sendMessage(Message msg) {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected to server.");
    }
    if (msg == null) {
      throw new NullPointerException("Message is null.");
    }

    mWriter.sendMessage(msg);
  }

  /**
   * Returns the configuration used to connect to the server.
   *
   * @return the configuration used to connect to the server
   */
  public ConnectionConfiguration getConfig() {
    return mConfig;
  }

  public InputStream getInput() {
    return mInput;
  }

  public OutputStream getOutput() {
    return mOutput;
  }

  /**
   * Returns true if currently connected to the JT/T808 server.
   *
   * @return true if connected
   */
  public boolean isConnected() {
    return mConnected;
  }

  public boolean isSocketClosed() {
    return mSocketClosed;
  }

  /**
   * Initializes the connection by creating a message reader and writer.
   */
  private void initConnection() throws IOException {
    boolean isFirstInit = (mReader == null || mWriter == null);

    // Set the input stream and output stream instance variables
    try {
      mInput = mSocket.getInputStream();
      mOutput = mSocket.getOutputStream();
    } catch (IOException ioe) {
      // An exception occured in setting up the connection. Make sure we shut down the input
      // stream and output stream and close the socket
      if (mWriter != null) {
        mWriter.shutdown();
        mWriter = null;
      }
      if (mReader != null) {
        mReader.shutdown();
        mReader = null;
      }
      if (mInput != null) {
        try {
          mInput.close();
        } catch (IOException e) {
          // Ignore
        }
        mInput = null;
      }
      if (mOutput != null) {
        try {
          mOutput.close();
        } catch (IOException e) {
          // Ignore
        }
        mOutput = null;
      }
      if (mSocket != null) {
        try {
          mSocket.close();
        } catch (IOException e) {
          // Ignore
        }
        mSocket = null;
      }

      mConnected = false;

      throw ioe;
    }

    if (isFirstInit) {
      mWriter = new MessageWriter(this);
      mReader = new MessageReader(this);
    } else {
      mWriter.init();
      mReader.init();
    }

    // Start the message writer
    mWriter.startup();
    // Start the message reader, the startup() method will block until we get a packet from server
    mReader.startup();

    // Make note of the fact that we're now connected
    mConnected = true;
  }

}

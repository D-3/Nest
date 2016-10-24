package ml.that.pigeon.conn;

import java.io.InputStream;

import ml.that.pigeon.util.LogUtils;

/**
 * Listens for packet traffic from the JT/T808 server and parse it into message objects.
 * <p>
 * The message reader also invokes all message listeners and collectors.
 *
 * @author ThatMrL (thatmr.l@gmail.com)
 */
class MessageReader {

  private static final String TAG = LogUtils.makeTag(MessageReader.class);

  private Connection  mConnection;
  private InputStream mInput;
  private Thread      mThread;

  private boolean mDone;

  /**
   * Creates a new message reader with the specified connection.
   *
   * @param conn the connection
   */
  MessageReader(Connection conn) {
    mConnection = conn;
    init();
  }

  /**
   * Initializes the reader in order to be used. The reader is initialized during the first
   * connection and when reconnecting due to an abruptly disconnection.
   */
  void init() {
    mDone = false;
    mInput = mConnection.getInput();

    mThread = new ReadThread();
    // TODO: 10/24/2016 add connection count to the name
    mThread.setName("Pigeon Message Reader ( )");
    mThread.setDaemon(true);
  }

  /**
   * Starts the packet read thread and returns once a connection to the server has been established.
   * A connection will be attempted for a maximum of five seconds. An Jtt808Exception will be thrown
   * if the connection fails.
   */
  public void startup() {
    mThread.start();
    // TODO: 10/24/2016 complete this method
  }

  /**
   * Shuts the message reader down.
   */
  public void shutdown() {
    // TODO: 10/24/2016 implement this method
  }

  private void readPackets() {
    // TODO: 10/24/2016 implement this method
  }

  private class ReadThread extends Thread {

    @Override
    public void run() {
      super.run();
      readPackets();
    }

  }

}

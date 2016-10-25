package ml.that.pigeon.conn;

import java.io.IOException;
import java.io.InputStream;

import ml.that.pigeon.msg.Message;
import ml.that.pigeon.msg.Packet;
import ml.that.pigeon.util.LogUtils;

/**
 * Listens for packet traffic from the JT/T808 server and parse it into message objects.
 * <p>
 * The message reader also invokes all message listeners and collectors.
 *
 * @author That Mr.L (thatmr.l@gmail.com)
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
   * Starts the packet read thread.
   */
  public synchronized void startup() {
    mThread.start();
  }

  /**
   * Shuts the message reader down.
   */
  public void shutdown() {
    mDone = true;
  }

  /**
   * Parses packets in order to process them further.
   */
  private void readPackets() {
    try {
      byte[] buf = new byte[128];
      int len;
      while (!mDone && (len = mInput.read(buf)) != -1) {
        if (len > 0) {
          byte[] raw = new byte[len];
          System.arraycopy(buf, 0, raw, 0, len);
          Packet packet = new Packet(raw);
          Message message = new Message.Builder(packet).build();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO: 10/24/2016 implement this method
  }

  /**
   * Processes a message after it's been fully parsed by looping through the installed message
   * collectors and listeners and letting them examine the message to see if they are a match with
   * the filter.
   *
   * @param msg the message to process
   */
  private void processMessage(Message msg) {
    if (msg == null) {
      return;
    }
    // Loop through all collectors and notify the appropriate ones.
    for (MessageCollector collector : mConnection.getCollectors()) {
      collector.processMessage(msg);
    }
  }

  private class ReadThread extends Thread {

    @Override
    public void run() {
      super.run();
      readPackets();
    }

  }

}

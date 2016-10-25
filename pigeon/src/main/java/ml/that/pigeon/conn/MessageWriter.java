package ml.that.pigeon.conn;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ml.that.pigeon.msg.Message;
import ml.that.pigeon.msg.Packet;
import ml.that.pigeon.util.LogUtils;

/**
 * Writes messages to a JT/T808 server.
 * <p>
 * Messages are sent using a dedicated thread. Message interceptors can be registered to dynamically
 * modify message before they're actually sent. Message listeners can be registered to listen for
 * all outgoing messages.
 *
 * @author That Mr.L (thatmr.l@gmail.com)
 */
class MessageWriter {

  private static final String TAG = LogUtils.makeTag(MessageWriter.class);

  private final BlockingQueue<Packet> mQueue;

  private Connection   mConnection;
  private OutputStream mOutput;
  private Thread       mThread;

  private boolean mDone;

  /**
   * Creates a new message writer with the specified connection.
   *
   * @param conn the connection
   */
  MessageWriter(Connection conn) {
    mQueue = new ArrayBlockingQueue<>(500, true);
    mConnection = conn;
    init();
  }

  /**
   * Initializes the writer in order to be used. It is called at the first connection and also is
   * invoked if the connection is disconnected by an error.
   */
  void init() {
    mDone = false;
    mOutput = mConnection.getOutput();
    
    mThread = new WriteThread();
    // TODO: 10/24/2016 add connection count to the name
    mThread.setName("Pigeon Message Writer ( )");
    mThread.setDaemon(true);
  }

  /**
   * Starts the packet write thread. The message writer will continue writing packets until {@link
   * #shutdown} or an error occurs.
   */
  public void startup() {
    mThread.start();
  }

  /**
   * Shuts down the message writer. Once this method has been called, no further packets will be
   * written to the server.
   */
  public void shutdown() {
    mDone = true;
    synchronized (mQueue) {
      mQueue.notifyAll();
    }
  }

  /**
   * Sends the specified message to the server.
   *
   * @param msg the message to send
   */
  public void sendMessage(Message msg) {
    if (!mDone) {
      try {
        for (Packet packet : msg.getPackets()) {
          mQueue.put(packet);
        }
      } catch (InterruptedException ie) {
        ie.printStackTrace();
        return;
      }
      synchronized (mQueue) {
        mQueue.notifyAll();
      }
    }
  }

  private void writePackets() {
    try {
      // Write out packets from the queue
      while (!mDone) {
        Packet packet = nextPacket();
        if (packet != null) {
          synchronized (mOutput) {
            mOutput.write(packet.getBytes());
            mOutput.flush();
          }
        }
      }

      // Flush out the rest of the queue. If the queue is extremely large, it's possible we won't
      // have time to entirely flush it before the socket is forced closed by the shutdown process.
      synchronized (mOutput) {
        while (!mQueue.isEmpty()) {
          Packet packet = mQueue.remove();
          mOutput.write(packet.getBytes());
        }
        mOutput.flush();
        mOutput.close();
      }

      // Delete the queue contents (hopefully nothing is left)
      mQueue.clear();
    } catch (IOException ioe) {
      // TODO: 10/24/2016 the exception can be ignored if the connection is done
      ioe.printStackTrace();
    }
  }

  private Packet nextPacket() {
    Packet packet = null;

    // Wait until there's a packet or we're done
    while (!mDone && (packet = mQueue.poll()) == null) {
      try {
        synchronized (mQueue) {
          mQueue.wait();
        }
      } catch (InterruptedException ie) {
        // Do nothing
      }
    }

    return packet;
  }

  private class WriteThread extends Thread {

    @Override
    public void run() {
      super.run();
      writePackets();
    }

  }

}

package ml.that.pigeon;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import ml.that.pigeon.MessageService.TaskSubmitter;
import ml.that.pigeon.MessageService.TaskTracker;
import ml.that.pigeon.conn.Connection;
import ml.that.pigeon.conn.ConnectionConfiguration;
import ml.that.pigeon.conn.MessageListener;
import ml.that.pigeon.filter.MessageIdFilter;
import ml.that.pigeon.msg.Message;
import ml.that.pigeon.msg.RegisterReply;
import ml.that.pigeon.msg.RegisterRequest;
import ml.that.pigeon.util.LogUtils;

/**
 * This class is to manage the JT/T808 connection between client and server.
 *
 * @author That Mr.L (thatmr.l@gmail.com)
 */
public class ConnectionManager {

  private static final String TAG = LogUtils.makeTag(ConnectionManager.class);

  private SharedPreferences mPrefs;
  private String            mHost;
  private int               mPort;

  private Connection mConnection;

  private List<Runnable> mTasks;
  private TaskSubmitter  mSubmitter;
  private TaskTracker    mTracker;
  private Future         mFuture;

  private boolean mRunning = false;

  public ConnectionManager(MessageService svc) {
    mPrefs = svc.getPrefs();
    mHost = mPrefs.getString(ClientConstants.PREF_KEY_HOST, ClientConstants.PREF_DEFAULT_HOST);
    mPort = mPrefs.getInt(ClientConstants.PREF_KEY_PORT, ClientConstants.PREF_DEFAULT_PORT);

    mTasks = new ArrayList<>();
    mSubmitter = svc.getSubmitter();
    mTracker = svc.getTracker();
  }

  public void connect() {
    Log.d(TAG, "connect: ");

    addTask(new ConnectTask());
    addTask(new RegisterTask());
    addTask(new LoginTask());
  }

  private boolean isConnected() {
    return mConnection != null && mConnection.isConnected();
  }

  private boolean isRegistered() {
    return mPrefs.contains(ClientConstants.PREF_KEY_AUTH_CODE);
  }

  private void addTask(Runnable task) {
    Log.d(TAG, "addTask: ");

    mTracker.increase();
    synchronized (mTasks) {
      if (mTasks.isEmpty() && !mRunning) {
        mRunning = true;
        mFuture = mSubmitter.submit(task);
        if (mFuture == null) {
          mTracker.decrease();
        }
      } else {
        mTasks.add(task);
      }
    }

    Log.d(TAG, "addTask: Done.");
  }

  private void runTask() {
    Log.d(TAG, "runTask: ");

    synchronized (mTasks) {
      if (!mTasks.isEmpty()) {
        Runnable task = mTasks.get(0);
        mTasks.remove(0);
        mRunning = true;
        mFuture = mSubmitter.submit(task);
        if (mFuture == null) {
          mTracker.decrease();
        }
      } else {
        mRunning = false;
        mFuture = null;
      }
    }
    mTracker.decrease();

    Log.d(TAG, "runTask: Done.");
  }

  /** A runnable task to connect to the server. */
  private class ConnectTask implements Runnable {

    @Override
    public void run() {
      Log.i(TAG, "run: Connecting...");

      if (!isConnected()) {
        // Create the configuration for this new connection
        ConnectionConfiguration cfg = new ConnectionConfiguration(mHost, mPort);
        // Create a new connection
        mConnection = new Connection(cfg);
        // Connect to the server
        try {
          mConnection.connect();
          Log.i(TAG, "run: Connected successfully.");
        } catch (IOException ioe) {
          Log.e(TAG, "run: Connection failed.", ioe);
        }
        runTask();
      } else {
        Log.i(TAG, "run: Connected already.");
        runTask();
      }
    }

  }

  /** A runnable task to register a new client onto the server. */
  private class RegisterTask implements Runnable {

    @Override
    public void run() {
      Log.i(TAG, "run: Registering...");

      if (!isRegistered()) {
        mConnection.addRcvListener(new RegisterListener(), new MessageIdFilter(RegisterReply.ID));
        RegisterRequest request = new RegisterRequest.Builder().build();
        mConnection.sendMessage(request);
      }
    }

  }

  /** A runnable task to log into the server. */
  private class LoginTask implements Runnable {

    @Override
    public void run() {
      Log.i(TAG, "run: Logging in...");
      // TODO: 10/23/2016 implement this method
    }

  }

  /** A message listener to process register reply. */
  private class RegisterListener implements MessageListener {

    @Override
    public void processMessage(Message msg) {
      Log.d(TAG, "processMessage: msg=" + msg);

      if (RegisterReply.ID == msg.getId()) {
        RegisterReply reply = new RegisterReply.Builder(msg).build();
      }
    }

  }

}

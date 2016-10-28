package ml.that.pigeon.auth;

import ml.that.pigeon.conn.Connection;
import ml.that.pigeon.conn.MessageCollector;
import ml.that.pigeon.filter.MessageIdFilter;
import ml.that.pigeon.msg.AuthenticateRequest;
import ml.that.pigeon.msg.ChallengeCommand;

public class AdvancedAuthentication {

  private Connection mConnection;

  public AdvancedAuthentication(Connection conn) {
    mConnection = conn;
  }

  public boolean authenticate(String auth) {
    MessageCollector collector =
        mConnection.createMessageCollector(new MessageIdFilter(ChallengeCommand.ID));
    AuthenticateRequest request = new AuthenticateRequest.Builder(auth).build();
    // Send the message
    mConnection.sendMessage(request);
    // Wait up to a certain number of seconds for a command from the server
    ChallengeCommand command = (ChallengeCommand) collector.nextResult(5000L);
    if (command == null) {
      throw new NullPointerException("No command from the server.");
    }
    // Otherwise, no error so continue processing
    collector.cancel();

    // TODO: 2016/10/28 finish this method
    return false;
  }
}

package ml.that.pigeon.auth;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import ml.that.pigeon.conn.Connection;
import ml.that.pigeon.conn.MessageCollector;
import ml.that.pigeon.filter.MessageIdFilter;
import ml.that.pigeon.msg.AuthenticateRequest;
import ml.that.pigeon.msg.ChallengeCommand;
import ml.that.pigeon.msg.ChallengeResponse;
import ml.that.pigeon.msg.LoginCommand;
import ml.that.pigeon.msg.LoginResponse;
import ml.that.pigeon.util.ArrayUtils;
import ml.that.pigeon.util.CryptoUtils;

import static android.content.ContentValues.TAG;

public class AdvancedAuthentication {

  private static final String[] SVR_KEYS = new String[]{ "0123456789ABCDEF" };
  private static final String[] CLT_KEYS = new String[]{ "123456789JIANGSU" };

  private Connection mConnection;

  public AdvancedAuthentication(Connection conn) {
    mConnection = conn;
  }

  public boolean authenticate(String auth) {
    MessageCollector challengeCollector =
        mConnection.createMessageCollector(new MessageIdFilter(ChallengeCommand.ID));
    // Send the request
    AuthenticateRequest request = new AuthenticateRequest.Builder(auth).build();
    mConnection.sendMessage(request);
    // Wait up to a certain number of seconds for a challenge command from the server
    ChallengeCommand challenge = (ChallengeCommand) challengeCollector.nextResult(5000L);
    if (challenge == null) {
      throw new NullPointerException("No command from the server.");
    }
    // Otherwise, no error so continue processing
    challengeCollector.cancel();

    MessageCollector loginCollector =
        mConnection.createMessageCollector(new MessageIdFilter(LoginCommand.ID));

    byte algorithm = challenge.getAlgorithm();
    switch (algorithm) {
      case ChallengeCommand.ALGORITHM_AES128:
        break;
      default:
        Log.e(TAG, "authenticate: No such algorithm - " + algorithm);
        return false;
    }

    short sKeyIdx = challenge.getSvrKeyIndex();
    if (sKeyIdx < 0 || sKeyIdx >= SVR_KEYS.length) {
      Log.e(TAG, "authenticate: Sever key " + sKeyIdx + " not found.");
      return false;
    }
    String svrKey = SVR_KEYS[sKeyIdx];
    Log.d(TAG, "authenticate: svrKey=" + svrKey + ", " + sKeyIdx);

    short cKeyIdx = challenge.getCltKeyIndex();
    if (cKeyIdx < 0 || cKeyIdx >= CLT_KEYS.length) {
      Log.e(TAG, "authenticate: Client key " + cKeyIdx + " not found.");
      return false;
    }
    String cltKey = CLT_KEYS[cKeyIdx];
    Log.d(TAG, "authenticate: cltKey=" + cltKey + ", " + cKeyIdx);

    try {
      byte[] rdmA = CryptoUtils.decrypt(challenge.getEncryptedRdmA(), svrKey);
      Log.d(TAG, "authenticate: rdmA=" + new String(rdmA, "ascii"));

      byte[] rdmB = "aaaaaaaaaaaaaaaa".getBytes("ascii");
      Log.d(TAG, "authenticate: rdmB=aaaaaaaaaaaaaaaa");

      // TODO: 10/29/2016 replace fake data
      byte[] encryptedRdmB =
          CryptoUtils.encrypt(ArrayUtils.leftXor(rdmB, "8619800".getBytes("ascii")), cltKey);
      byte[] encryptedCltChk = CryptoUtils.encrypt(ArrayUtils.leftXor(rdmA, rdmB), cltKey);
      byte[] encryptedDvcSn = CryptoUtils.encrypt(ArrayUtils.leftXor(new byte[16], rdmA), cltKey);
      byte[] encryptedSvrAddr =
          CryptoUtils.encrypt(ArrayUtils.leftXor("218.90.189.222  ".getBytes("ascii"), rdmA),
                              cltKey);

      // Send the response
      ChallengeResponse response = new ChallengeResponse.Builder(cKeyIdx,
                                                                 encryptedRdmB,
                                                                 encryptedCltChk,
                                                                 encryptedDvcSn,
                                                                 encryptedSvrAddr).build();
      mConnection.sendMessage(response);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      Log.e(TAG, "authenticate: Encryption failed.", e);
      return false;
    } catch (UnsupportedEncodingException uue) {
      Log.e(TAG, "authenticate: Encode failed.", uue);
      return false;
    }
    // Wait up to a certain number of seconds for a login command from the server
    LoginCommand command = (LoginCommand) loginCollector.nextResult(5000L);
    if (command == null) {
      throw new NullPointerException("No command from the server.");
    }
    // Otherwise, no error so continue processing
    loginCollector.cancel();

    switch (command.getResult()) {
      case LoginCommand.RESULT_OK:
        // Send the last message
        LoginResponse login = new LoginResponse.Builder().build();
        mConnection.sendMessage(login);
        return true;
      case LoginCommand.RESULT_AUTH_CODE_NOT_FOUND:
      case LoginCommand.RESULT_AUTH_CODE_NOT_MATCH:
      case LoginCommand.RESULT_WRONG_PROTOCOL_VER:
      case LoginCommand.RESULT_WRONG_PLATE:
      case LoginCommand.RESULT_WRONG_SCHOOL:
      case LoginCommand.RESULT_WRONG_SVR_ADDR:
      case LoginCommand.RESULT_UNEXPECTED_CLT_KEY:
      case LoginCommand.RESULT_WRONG_HARDWARE:
      case LoginCommand.RESULT_WRONG_CLT_CHK:
      case LoginCommand.RESULT_TEST:
      case LoginCommand.RESULT_OTHER_ERROR:
      default:
        return false;
    }
  }

}

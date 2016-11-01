package ml.that.pigeon.msg;

import ml.that.pigeon.util.LogUtils;

/**
 * A heart beat request message.
 *
 * @author That Mr.L (thatmr.l@gmail.com)
 */
public class HeartbeatRequest extends Message {

  private static final String TAG = LogUtils.makeTag(HeartbeatRequest.class);

  public static final short ID = 0x0002;

  private HeartbeatRequest(Builder builder) {
    super(ID, builder.cipher, builder.phone, builder.body);
  }

  public static class Builder extends MessageBuilder {

    @Override
    public HeartbeatRequest build() {
      this.body = EMPTY_BODY;
      return new HeartbeatRequest(this);
    }

  }

}

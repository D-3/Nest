package ml.that.pigeon.msg;

import android.util.Log;

import ml.that.pigeon.util.ArrayUtils;
import ml.that.pigeon.util.IntegerUtils;
import ml.that.pigeon.util.LogUtils;

/**
 * Represents JT/T808 message packets.
 *
 * @author ThatMrL (thatmr.l@gmail.com)
 */
public class Packet {

  private static final String TAG = LogUtils.makeTag(Packet.class);

  static final short MAX_LENGTH = 0x03ff;

  private static final short PREFIX = 0x7e;
  private static final short SUFFIX = 0x7e;

  private final short   mMsgId;
  private final boolean mIsLongMsg;
  private final byte    mCipher;
  private final byte[]  mPhone;
  private final short   mSn;
  private final short   mTotal;
  private final short   mIndex;
  private final byte[]  mPayload;

  public Packet(short id,
                boolean isLong,
                byte cipher,
                byte[] phone,
                short sn,
                int total,
                int index,
                byte[] payload) {
    switch (cipher) {
      case Message.CIPHER_NONE:
      case Message.CIPHER_RSA:
        mCipher = cipher;
        break;
      default:
        mCipher = Message.CIPHER_NONE;
        Log.w(TAG, "Packet: Unknown cipher mode, set to none.");
    }

    if (phone != null && phone.length == 6) {
      mPhone = phone;
    } else {
      mPhone = Message.EMPTY_PHONE;
      Log.w(TAG, "Packet: Illegal phone number, set to empty.");
    }

    if (payload != null) {
      mPayload = payload;
    } else {
      mPayload = ArrayUtils.EMPTY_BYTE_ARRAY;
      Log.w(TAG, "Packet: Payload not specified, set to empty");
    }

    mMsgId = id;
    mIsLongMsg = isLong;
    mSn = sn;
    mTotal = (short) (isLong ? total : 0);
    mIndex = (short) (isLong ? index : 0);
  }

  public byte[] getBytes() {
    short attr = (short) ((mIsLongMsg ? 1 << 13 : 0) | (mCipher << 8) | (mPayload.length));

    byte[] header = ArrayUtils.concatenate(IntegerUtils.asBytes(mMsgId),
                                           IntegerUtils.asBytes(attr),
                                           mPhone,
                                           IntegerUtils.asBytes(PacketManager.getSn()));
    if (mIsLongMsg) {
      header = ArrayUtils.concatenate(header,
                                      IntegerUtils.asBytes(mTotal),
                                      IntegerUtils.asBytes(mIndex));
    }

    byte checksum = ArrayUtils.xorCheck(ArrayUtils.concatenate(header, mPayload));

    byte[] main = ArrayUtils.concatenate(header, mPayload, IntegerUtils.asBytes(checksum));

    return ArrayUtils.concatenate(IntegerUtils.asBytes(PREFIX),
                                  ArrayUtils.escape(main),
                                  IntegerUtils.asBytes(SUFFIX));
  }

  public int length() {
    return mPayload.length;
  }

  public short getMsgId() {
    return mMsgId;
  }

  public boolean isLongMsg() {
    return mIsLongMsg;
  }

  public byte getCipher() {
    return mCipher;
  }

  public byte[] getPhone() {
    return mPhone;
  }

  public short getSn() {
    return mSn;
  }

  public short getTotal() {
    return mTotal;
  }

  public short getIndex() {
    return mIndex;
  }

  public byte[] getPayload() {
    return mPayload;
  }

}

package ml.that.pigeon.msg;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import ml.that.pigeon.Jtt415Constants;
import ml.that.pigeon.util.ArrayUtils;
import ml.that.pigeon.util.IntegerUtils;
import ml.that.pigeon.util.LogUtils;

/**
 * A challenge response message.
 *
 * @author That Mr.L (thatmr.l@gmail.com)
 */
public class ChallengeResponse extends Message {

  private static final String TAG = LogUtils.makeTag(ChallengeResponse.class);

  public static final short ID = 0x1001;

  private static final byte[] EMPTY_MFRS_ID    = new byte[5];
  private static final byte[] EMPTY_CLT_ID     = new byte[7];
  private static final String EMPTY_PLATE_TEXT = "";
  private static final byte[] EMPTY_SCHOOL_NO  = new byte[6];
  private static final byte[] RESERVED_FIELD   = new byte[16];

  private final byte[] mMfrsId;
  private final byte[] mCltId;
  private final short  mHardwareVer;
  private final short  mSoftwareVer;
  private final short  mProtocolVer;
  private final byte   mCustomVer;
  private final byte   mPlateColor;
  private final String mPlateText;
  private final byte[] mSchoolNo;
  private final short  mCltKeyIndex;
  private final byte[] mEncryptedRdmB;
  private final byte[] mEncryptedCltChk;
  private final byte[] mEncryptedDvcSn;
  private final byte[] mEncryptedSvrAddr;

  private ChallengeResponse(Builder builder) {
    super(ID, builder.cipher, builder.phone, builder.body);

    this.mMfrsId = builder.mfrsId;
    this.mCltId = builder.cltId;
    this.mHardwareVer = builder.hardwareVer;
    this.mSoftwareVer = builder.softwareVer;
    this.mProtocolVer = builder.protocolVer;
    this.mCustomVer = builder.customVer;
    this.mPlateColor = builder.plateColor;
    this.mPlateText = builder.plateText;
    this.mSchoolNo = builder.schoolNo;
    this.mCltKeyIndex = builder.cltKeyIndex;
    this.mEncryptedRdmB = builder.encryptedRdmB;
    this.mEncryptedCltChk = builder.encryptedCltChk;
    this.mEncryptedDvcSn = builder.encryptedDvcSn;
    this.mEncryptedSvrAddr = builder.encryptedSvrAddr;
  }

  @Override
  public String toString() {
    return new StringBuilder("{ id=1001")
        .append(", mfrs=").append(mMfrsId)
        .append(", cltId=").append(mCltId)
        .append(", hwV=").append(mHardwareVer)
        .append(", swV=").append(mSoftwareVer)
        .append(", proV=").append(mProtocolVer)
        .append(", cusV=").append(mCustomVer)
        .append(", pClr=").append(mPlateColor)
        .append(", pTxt=").append(mPlateText)
        .append(", sch=").append(mSchoolNo)
        .append(", cKey=").append(mCltKeyIndex)
        .append(", rdmB=").append(mEncryptedRdmB)
        .append(", cChk=").append(mEncryptedCltChk)
        .append(", dvc=").append(mEncryptedDvcSn)
        .append(", sAddr=").append(mEncryptedSvrAddr)
        .append(" }").toString();
  }

  public static class Builder extends MessageBuilder {

    // Required parameters
    private final short  cltKeyIndex;
    private final byte[] encryptedRdmB;
    private final byte[] encryptedCltChk;
    private final byte[] encryptedDvcSn;
    private final byte[] encryptedSvrAddr;

    // Optional parameters - initialized to default values
    private byte[] mfrsId      = EMPTY_MFRS_ID;
    private byte[] cltId       = EMPTY_CLT_ID;
    private short  hardwareVer = 0;
    private short  softwareVer = 0;
    private short  protocolVer = 0;
    private byte   customVer   = 0;
    private byte   plateColor  = Jtt415Constants.PLATE_COLOR_TEST;
    private String plateText   = EMPTY_PLATE_TEXT;
    private byte[] schoolNo    = EMPTY_SCHOOL_NO;

    public Builder(short idx, byte[] rdm, byte[] chk, byte[] sn, byte[] addr) {
      if (idx < 0) {
        throw new IllegalArgumentException("Client key index is less than 0.");
      }
      if (rdm == null || rdm.length != 17) {
        throw new IllegalArgumentException("Encrypted random number B incorrect.");
      }
      if (chk == null || chk.length != 17) {
        throw new IllegalArgumentException("Encrypted client checksum incorrect.");
      }
      if (sn == null || sn.length != 17) {
        throw new IllegalArgumentException("Encrypted device SN incorrect.");
      }
      if (addr == null || addr.length != 17) {
        throw new IllegalArgumentException("Encrypted server address incorrect.");
      }

      this.cltKeyIndex = idx;
      this.encryptedRdmB = rdm;
      this.encryptedCltChk = chk;
      this.encryptedDvcSn = sn;
      this.encryptedSvrAddr = addr;
    }

    public Builder mfrsId(byte[] id) {
      if (id != null && id.length == 5) {
        this.mfrsId = id;
      } else {
        Log.w(TAG, "mfrsId: Illegal manufacturer ID, use default.");
      }

      return this;
    }

    public Builder cltId(byte[] id) {
      if (id != null && id.length == 7) {
        this.cltId = id;
      } else {
        Log.w(TAG, "cltId: Illegal client ID, use default.");
      }

      return this;
    }

    public Builder hardwareVer(short ver) {
      if (ver >= 0 && ver <= 9999) {
        this.hardwareVer = ver;
      } else {
        Log.w(TAG, "hardwareVer: Illegal hardware version, use default.");
      }

      return this;
    }

    public Builder softwareVer(short ver) {
      if (ver >= 0 && ver <= 9999) {
        this.softwareVer = ver;
      } else {
        Log.w(TAG, "softwareVer: Illegal software version, use default.");
      }

      return this;
    }

    public Builder protocolVer(short ver) {
      if (ver >= 0 && ver <= 9999) {
        this.protocolVer = ver;
      } else {
        Log.w(TAG, "protocolVer: Illegal protocol version, use default.");
      }

      return this;
    }

    public Builder customVer(byte ver) {
      if (ver >= 0 && ver <= 99) {
        this.customVer = ver;
      } else {
        Log.w(TAG, "customVer: Illegal custom version, use default.");
      }

      return this;
    }

    public Builder plateColor(byte color) {
      switch (color) {
        case Jtt415Constants.PLATE_COLOR_NONE:
        case Jtt415Constants.PLATE_COLOR_BLUE:
        case Jtt415Constants.PLATE_COLOR_YELLOW:
        case Jtt415Constants.PLATE_COLOR_BLACK:
        case Jtt415Constants.PLATE_COLOR_WHITE:
        case Jtt415Constants.PLATE_COLOR_TEST:
          this.plateColor = color;
          break;
        default:
          Log.w(TAG, "plateColor: Unknown plate color, use default.");
      }

      return this;
    }

    public Builder plateText(String text) {
      if (text != null) {
        this.plateText = text;
      } else {
        Log.w(TAG, "plateText: Plate text is null, use default.");
      }

      return this;
    }

    public Builder schoolNo(byte[] school) {
      if (school != null && school.length == 6) {
        this.schoolNo = school;
      } else {
        Log.w(TAG, "schoolNo: Illegal school number, use default.");
      }

      return this;
    }

    @Override
    public ChallengeResponse build() {
      try {
        this.body = ArrayUtils.concatenate(this.mfrsId,
                                           this.cltId,
                                           IntegerUtils.toBcd(this.hardwareVer),
                                           IntegerUtils.toBcd(this.softwareVer),
                                           IntegerUtils.toBcd(this.protocolVer),
                                           IntegerUtils.toBcd(this.customVer),
                                           RESERVED_FIELD,
                                           IntegerUtils.asBytes(this.plateColor),
                                           this.plateText.getBytes("ascii"),
                                           this.schoolNo,
                                           RESERVED_FIELD,
                                           IntegerUtils.asBytes(this.cltKeyIndex),
                                           this.encryptedRdmB,
                                           this.encryptedCltChk,
                                           this.encryptedDvcSn,
                                           this.encryptedSvrAddr);
      } catch (UnsupportedEncodingException uee) {
        Log.e(TAG, "build: Encode message body failed.", uee);
      }

      return new ChallengeResponse(this);
    }

  }

}

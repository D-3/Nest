package ml.that.pigeon.msg;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import ml.that.pigeon.util.ArrayUtils;
import ml.that.pigeon.util.IntegerUtils;
import ml.that.pigeon.util.LogUtils;

import static ml.that.pigeon.Jtt415Constants.PLATE_COLOR_BLACK;
import static ml.that.pigeon.Jtt415Constants.PLATE_COLOR_BLUE;
import static ml.that.pigeon.Jtt415Constants.PLATE_COLOR_NONE;
import static ml.that.pigeon.Jtt415Constants.PLATE_COLOR_TEST;
import static ml.that.pigeon.Jtt415Constants.PLATE_COLOR_WHITE;
import static ml.that.pigeon.Jtt415Constants.PLATE_COLOR_YELLOW;

public class RegisterRequest extends Message {

  private static final String TAG = LogUtils.makeTag(RegisterRequest.class);

  public static final short ID = 0x0100;

  private static final byte[] EMPTY_MANU_ID   = new byte[5];
  private static final byte[] EMPTY_CLT_MODEL = new byte[20];
  private static final byte[] EMPTY_CLT_ID    = new byte[7];

  private final short  mProvId;
  private final short  mCityId;
  private final byte[] mManuId;
  private final byte[] mCltModel;
  private final byte[] mCltId;
  private final byte   mPlateColor;
  private final String mPlateText;

  private RegisterRequest(Builder builder) {
    super(ID, builder.cipher, builder.phone, builder.body);

    mProvId = builder.provId;
    mCityId = builder.cityId;
    mManuId = builder.manuId;
    mCltModel = builder.cltModel;
    mCltId = builder.cltId;
    mPlateColor = builder.plateColor;
    mPlateText = builder.plateText;
  }

  @Override
  public String toString() {
    return new StringBuilder("{ id=0100")
        .append(", prov=").append(mProvId)
        .append(", city=").append(mCityId)
        .append(", manu=").append(Arrays.toString(mManuId))
        .append(", model=").append(Arrays.toString(mCltModel))
        .append(", cltId=").append(Arrays.toString(mCltId))
        .append(", pClr=").append(mPlateColor)
        .append(", pTxt=").append(mPlateText)
        .append(" }").toString();
  }

  public static class Builder {

    // Optional parameters - initialized to default values
    private byte   cipher     = Message.CIPHER_NONE;
    private byte[] phone      = Message.EMPTY_PHONE;
    private short  provId     = 0;
    private short  cityId     = 0;
    private byte[] manuId     = EMPTY_MANU_ID;
    private byte[] cltModel   = EMPTY_CLT_MODEL;
    private byte[] cltId      = EMPTY_CLT_ID;
    private byte   plateColor = PLATE_COLOR_TEST;
    private String plateText  = null;
    private byte[] body       = Message.EMPTY_BODY;

    public Builder cipher(byte cipher) {
      switch (cipher) {
        case CIPHER_NONE:
        case CIPHER_RSA:
          this.cipher = cipher;
          break;
        default:
          Log.w(TAG, "cipher: Unknown cipher mode, use default.");
      }

      return this;
    }

    public Builder phone(byte[] phone) {
      if (phone != null && phone.length == 6) {
        this.phone = phone;
      } else {
        Log.w(TAG, "phone: Illegal phone number, use default.");
      }

      return this;
    }

    public Builder provId(byte id) {
      this.provId = id;
      return this;
    }

    public Builder cityId(byte id) {
      this.cityId = id;
      return this;
    }

    public Builder manuId(byte[] id) {
      if (id != null && id.length == 5) {
        this.manuId = id;
      } else {
        Log.w(TAG, "manuId: Illegal manufacturer ID, use default.");
      }

      return this;
    }

    public Builder cltModel(byte[] model) {
      if (model != null && model.length == 20) {
        this.cltModel = model;
      } else {
        Log.w(TAG, "cltModel: Illegal client model, use default.");
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

    public Builder plateColor(byte color) {
      switch (color) {
        case PLATE_COLOR_NONE:
        case PLATE_COLOR_BLUE:
        case PLATE_COLOR_YELLOW:
        case PLATE_COLOR_BLACK:
        case PLATE_COLOR_WHITE:
        case PLATE_COLOR_TEST:
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

    public RegisterRequest build() {
      try {
        this.body = ArrayUtils.concatenate(IntegerUtils.asBytes(this.provId),
                                           IntegerUtils.asBytes(this.cityId),
                                           this.manuId,
                                           this.cltModel,
                                           this.cltId,
                                           IntegerUtils.asBytes(this.plateColor),
                                           this.plateText.getBytes("gbk"));
      } catch (UnsupportedEncodingException uee) {
        Log.e(TAG, "build: Encode message body failed.", uee);
      }

      return new RegisterRequest(this);
    }

  }

}

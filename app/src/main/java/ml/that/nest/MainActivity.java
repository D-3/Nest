package ml.that.nest;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ml.that.pigeon.ServiceManager;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate: ");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Start the message service
    new ServiceManager(this).startService();
  }

  @Override
  protected void onRestart() {
    Log.d(TAG, "onRestart: ");
    super.onRestart();
  }

  @Override
  protected void onStart() {
    Log.d(TAG, "onStart: ");
    super.onStart();
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    Log.d(TAG, "onRestoreInstanceState: ");
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume: ");
    super.onResume();
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause: ");
    super.onPause();
  }

  @Override
  public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    Log.d(TAG, "onSaveInstanceState: ");
    super.onSaveInstanceState(outState, outPersistentState);
  }

  @Override
  protected void onStop() {
    Log.d(TAG, "onStop: ");
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestroy: ");
    super.onDestroy();
  }

}

package ru.iu3.fclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import ru.iu3.fclient.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity implements TransactionEvents{

    ActivityResultLauncher activityResultLauncher;
    private String pin;

    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex) {
            hex = null;
        }
        return hex;
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("mbedcrypto");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult> () {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
//                            String pin = data.getStringExtra("pin");
//                            Toast.makeText(MainActivity.this, pin,
//                                    Toast.LENGTH_SHORT).show();
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                });

        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());

        int res = initRng();
//        byte[] rnd = randomBytes(10);
//        byte[] key = randomBytes(16);
//        byte[] data = randomBytes(200);

//        byte[] encdata = encrypt(key, data);
//        byte[] decdata = decrypt(key, encdata);
//
//        boolean equal = true;
//        if (decdata.length != data.length) equal = false;
//        for (int i = 0; i < decdata.length; i++) {
//            if (data[i] != decdata[i]) equal = false;
//        }
//        System.out.print(equal);
    }

    public void onButtonClick(View v)
    {
//        Intent it = new Intent(this, PinpadActivity.class);
//        activityResultLauncher.launch(it);

        // Добавляем метод транзакции данных
        new Thread(() -> {
            try {
                byte[] trd = stringToHex("9F0206000000000100");
                transaction(trd);
            } catch (Exception exception) {
                Log.println(Log.ERROR, "MtLog", Arrays.toString(exception.getStackTrace()));
            }
        }).start();

//        Toast.makeText(this, "Hello",
//               Toast.LENGTH_SHORT).show();
//        byte[] key = stringToHex("0123456789ABCDEF0123456789ABCDE0");
//        byte[] enc = encrypt(key, stringToHex("000000000000000102"));
//        byte[] dec = decrypt(key, enc);
//        String s = new String(Hex.encodeHex(dec)).toUpperCase();
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
    public native boolean transaction(byte[] trd);

    // Переопределяем метод, который написан в интерфейсе
    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();

        Intent intent = new Intent(MainActivity.this, PinpadActivity.class);
        intent.putExtra("ptc", ptc);
        intent.putExtra("amount", amount);

        synchronized (MainActivity.this) {
            activityResultLauncher.launch(intent);
            try {
                MainActivity.this.wait();
            } catch (Exception exception) {
                Log.println(Log.ERROR, "MtLog", exception.getMessage());
            }
        }

        return pin;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }
}
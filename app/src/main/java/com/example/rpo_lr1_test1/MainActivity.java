package com.example.rpo_lr1_test1;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rpo_lr1_test1.databinding.ActivityMainBinding;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;





public class MainActivity extends AppCompatActivity implements TransactionEvents {

    ActivityResultLauncher activityResultLauncher;
    // Used to load the 'rpo_lr1_test1' library on application startup.
    static {
        System.loadLibrary("rpo_lr1_test1");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    private String pin;

    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }
    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int res = initRng();

        ////////////////////////////////////////////////////////////////////////
        byte[] v = randomBytes(10);

        ///////////////////////////////////////////////////////////////////////
        // Example of a call to a native method
        TextView tv = binding.sampleText;
        String NewStr = stringFromJNI();
        String a= Arrays.toString(v);
        String ar[]=a.substring(1,a.length()-1).split(", ");
        NewStr=NewStr+" ||rand: "+Arrays.toString(ar)+" \n ";

        byte[] keys = "qwertyuiopasdfgh".getBytes();
        String b= Arrays.toString(keys);
        String bar[]=b.substring(1,b.length()-1).split(", ");
        NewStr=NewStr+" ||key: "+Arrays.toString(bar)+" \n ";

        byte[] shifrArr = encrypt(keys, v);
        b= Arrays.toString(shifrArr);
        bar=b.substring(1,b.length()-1).split(", ");
        NewStr=NewStr+" ||encrypt: "+Arrays.toString(bar)+" \n ";

        byte[] deshArr = decrypt(keys, shifrArr);
        b= Arrays.toString(deshArr);
        bar=b.substring(1,b.length()-1).split(", ");
        NewStr=NewStr+" ||decr: "+Arrays.toString(bar)+" \n ";
//        tv.setText(stringFromJNI());
//        tv.setText(NewStr);
        Toast.makeText(this, NewStr, Toast.LENGTH_LONG).show();

        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            String pin = data.getStringExtra("pin");
                            Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        activityResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            //String pin = data.getStringExtra("pin");
                            //Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                });

    }

    public void onButtonClick2(View v)
    {

        new Thread(()-> {
            try {




                HttpURLConnection uc = (HttpURLConnection)
                        (new URL("http://192.168.43.111:8081/api/v1/title").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream);
                String title = getPageTitle(html);



                runOnUiThread(()-> {
//                    Toast.makeText(MainActivity.this, ok ? "ok" : "failed", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, title, Toast.LENGTH_LONG).show();
                });


            } catch (Exception ex) {
                // todo: log error
                Log.e("fapptag","HTTP CLIENT FAILS", ex);
            }
        }).start();
        //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }



    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (org.apache.commons.codec.DecoderException e) {
            throw new RuntimeException(e);
        }
        return hex;
    }
//

//    private String getPageTitle(String html) {
//        return "";
//    }
protected String getPageTitle(String html)
{
    int pos = html.indexOf("<title");
    String p="not found";
    if (pos >= 0)
    {
        int pos2 = html.indexOf("<", pos + 1);
        if (pos >= 0)
            p = html.substring(pos + 7, pos2);
    }
    return p;
}

    /**
     * A native method that is implemented by the 'rpo_lr1_test1' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String CONSOS(String s);
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public native boolean transaction(byte[] trd);

    public static native byte[] decrypt(byte[] key, byte[] data);
}
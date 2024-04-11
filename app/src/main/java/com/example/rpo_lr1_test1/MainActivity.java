package com.example.rpo_lr1_test1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.rpo_lr1_test1.databinding.ActivityMainBinding;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    // Used to load the 'rpo_lr1_test1' library on application startup.
    static {
        System.loadLibrary("rpo_lr1_test1");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int res = initRng();
        byte[] v = randomBytes(10);
        // Example of a call to a native method
        TextView tv = binding.sampleText;
        String NewStr = stringFromJNI();
        //CONSOS(); //ради хайпа
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
        tv.setText(NewStr);

    }

    /**
     * A native method that is implemented by the 'rpo_lr1_test1' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String CONSOS();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);

    public static native byte[] decrypt(byte[] key, byte[] data);
}
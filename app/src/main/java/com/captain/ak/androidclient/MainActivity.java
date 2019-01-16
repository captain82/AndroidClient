package com.captain.ak.androidclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText et;

    private static Socket s;
    private static ServerSocket ss;
    private static InputStreamReader isr;
    private static BufferedReader br;
    private static PrintWriter printWriter;
    String str;

    String message = "";
    private static String ip = "169.254.101.43";

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;
    ImageView imageView;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = (EditText) findViewById(R.id.editText);



        Button upload_Button = (Button)findViewById(R.id.upload_button);

        upload_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
            }
        });

            detectionProgressDialog = new ProgressDialog(this);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                imageView = (ImageView)findViewById(R.id.imageView);

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendText(View view) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();

         message = Base64.encodeToString(byteArray,Base64.NO_WRAP);
         //String message2 = String.valueOf(Base64.decode(message,0));

        //Log.i("MESSAGE",message);

        //message = et.getText().toString();

        myTask mt = new myTask();

        mt.execute();

        Toast.makeText(getApplicationContext(), "Data sent", Toast.LENGTH_LONG).show();

    }

    class myTask extends AsyncTask<Void, Void, String> implements Runnable{

        @Override
        protected String doInBackground(Void... voids) {
            String message3 = null;
            try {

                //Connect to the socket at port 5000
                s = new Socket("192.168.43.215",8083);
                InputStream is = s.getInputStream();
                printWriter = new PrintWriter(s.getOutputStream()); //Set the output stream

                printWriter.write(message); //Send the message through the stream
                printWriter.flush();
                Log.i("Working" , "Fine");
                byte[] data = new byte[42];
                int count = is.read(data,0,42);
                 str = new String(data);
                //br = new BufferedReader(new InputStreamReader(is));
                Log.i("Working" , "Fine");

                //message3 = br.readLine();
                Log.i("Working" , "Fine");


                runOnUiThread(new Runnable() {

                    public void run() {

                        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();

                    }
                });

                Log.i("MESSAGE" , str);

                printWriter.close();
                s.close();





            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }


        @Override
        public void run() {


        }
    }
}

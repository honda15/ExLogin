package com.ehappy.exlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private EditText edtID, edtPW;
    private Button btnOK, btnReset;
    private String[] login;
    private File filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtID = (EditText)findViewById(R.id.edtID);
        edtPW = (EditText)findViewById(R.id.edtPW);
        btnOK = (Button)findViewById(R.id.btnOK);
        btnReset = (Button)findViewById(R.id.btnReset);
        btnOK.setOnClickListener(listener);
        btnReset.setOnClickListener(listener);
        requestStoragePermission();  //檢查驗證
    }

    //檢查驗證
    private void requestStoragePermission() {
        if(Build.VERSION.SDK_INT >= 23) {  //Androis 6.0 以上
            //判斷是否已取得驗證
            int hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if(hasPermission != PackageManager.PERMISSION_GRANTED) {  //未取得驗證
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
        readFile();  //已取得驗證
    }

    //requestPermissions 觸發的事件
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //按 允許 鈕
                readFile();
            } else {
                Toast.makeText(this, "未取得權限！", Toast.LENGTH_SHORT).show();
                finish();  //結束應用程式
            }
        } else  {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //讀取檔案
    private void readFile() {
        filename = new File("sdcard/login.txt");  //SD卡根目錄密碼檔
        try {
            FileInputStream fin = new FileInputStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            String line = "", wholedata = "";
            int i = 0;
            while ((line = reader.readLine()) != null) {
                wholedata = wholedata + line + "\n";
                i++;
            }
            login = wholedata.split("\n");
            reader.close();
            fin.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_LONG) .show();
            e.printStackTrace();
        }
    }

    private Button.OnClickListener listener=new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnOK:  //登入
                    //檢查帳號及密碼是否都有輸入
                    if(edtID.getText().toString().equals("") || edtPW.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "帳號及密碼都必須輸入！", Toast.LENGTH_LONG) .show();
                        break;
                    }
                    Boolean flag=false;
                    for(int i=0;i<login.length;i+=2){
                        if(edtID.getText().toString().equals(login[i])){ //帳號存在
                            flag=true;
                            if(edtPW.getText().toString().equals(login[i+1])){ //密碼正確
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("登入")
                                        .setMessage("登入成功！\n歡迎使用本應用程式！")
                                        .setPositiveButton("確定", new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                //轉換到應用程式啟始頁面程式碼置於此處
                                                finish();
                                            }
                                        })
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(), "密碼不正確！", Toast.LENGTH_LONG) .show();
                                edtPW.setText("");
                                break;
                            }
                        }
                    }
                    if(!flag) {
                        Toast.makeText(getApplicationContext(), "帳號不正確！", Toast.LENGTH_LONG) .show();
                        edtID.setText("");
                        edtPW.setText("");
                    }
                    break;
                case R.id.btnReset:  //重新輸入
                    edtID.setText("");
                    edtPW.setText("");
                    break;
            }
        }
    };
}

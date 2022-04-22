package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendTextActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private MessageReceiver messageReceiver;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;
    private ImageView picture;
    private EditText sendtext;
    List<String> messageslist= new ArrayList<>();
    String phone;
    ListView messagesView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        Intent intent = getIntent();
        String[] data = intent.getStringExtra("data").split("\n");
        phone=data[1].replaceAll(" ","");

        TextView contact=(TextView)findViewById(R.id.textView);
        sendtext = (EditText) findViewById(R.id.edit_text);

        Button send = (Button) findViewById(R.id.send);
        Button takephoto = (Button) findViewById(R.id.takephoto);
        Button chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        picture = (ImageView) findViewById(R.id.picture);

        //信息展示
        contact.setText(data[0]+":    "+data[1]);
        messagesView=(ListView)findViewById(R.id.messages);
        adapter =new ArrayAdapter<String>(this,android.R.layout. simple_list_item_1,messageslist);
        messagesView.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_SMS }, 1);
        } else {
            getSmsInPhone();
        }
        //接收短信广播
        getSms();

        //发送短信功能
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SendTextActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendTextActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
                } else {
                    sendMessage();
                }
            }
        });

        //拍照功能
        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(SendTextActivity.this,
                            "com.example.multimedia.fileprovider", outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        //从相册中选择照片
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SendTextActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendTextActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 3);
                } else {
                    openAlbum();
                }
            }
        });
    }

    //注册接收短信广播
    private void getSms() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        //设置较高的优先级
        intentFilter.setPriority(100);
        registerReceiver(messageReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁广播接收器
        unregisterReceiver(messageReceiver);
    }

    //短信广播接收器
    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            //提取短信消息
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            //获取发送方号码
            String address = messages[0].getOriginatingAddress();

            String fullMessage = "";
            for (SmsMessage message : messages) {
                //获取短信内容
                fullMessage += message.getMessageBody();
            }
            //截断广播,阻止其继续被Android自带的短信程序接收到
//            abortBroadcast();
            messageslist.add(fullMessage);
            adapter =new ArrayAdapter<String>(context,android.R.layout. simple_list_item_1,messageslist);
            messagesView.setAdapter(adapter);
        }
    }

    //发送短信
    private void sendMessage(){
        try {
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,sendtext.getText().toString(),null,null);
            messageslist.add(sendtext.getText().toString());
            adapter =new ArrayAdapter<String>(SendTextActivity.this,android.R.layout. simple_list_item_1,messageslist);
            messagesView.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), "短信发送成功！",
                    Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),
                    "发送失败，请重试！",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //获取短信的历史信息
    @SuppressLint("LongLogTag")
    public void getSmsInPhone() {
        String SMS_URI_ALL = "content://sms/";

        Cursor cur = null;

        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
             cur = getContentResolver().query(uri, projection, "address=?", new String[]{phone}, "date asc");// 获取手机内部短信

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = new Date(longDate);
                    String strDate = dateFormat.format(date);

                    String strType = "";
                    if (intType == 1) {
                        strType = "已接收";
                    } else if (intType == 2) {
                        strType = "已发送";
                    } else {
                        strType = "null";
                    }
                    messageslist.add("[电话号码："+strAddress+" 时间："+strDate+"]\n"
                            +"短信内容："+strbody+"\n"+strType+"\n");
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
            } else {
                messageslist.add("（没有信息记录）");
            } // end if
//            messageslist.add("已执行getSmsInPhone！");
        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO://返回拍摄相片
                if (resultCode == RESULT_OK) {

                    try {
//                        displayImage(getContentResolver().openInputStream(imageUri));
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    //打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    //获取图片路径
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);//
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    //获取图片路径
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //展示图片
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图像失败！", Toast.LENGTH_SHORT).show();
        }
    }

    //运行时授权
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSmsInPhone();
                }
                else {
                    Toast.makeText(this, "你拒绝了短信历史信息访问权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                } else {
                    Toast.makeText(this, "你拒绝了发送短信访问权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了打开相册访问权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
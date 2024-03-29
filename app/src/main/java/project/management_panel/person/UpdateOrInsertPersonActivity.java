package project.management_panel.person;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amaloffice.catalog.R;
import com.glide.slider.library.svg.GlideApp;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.master.permissionhelper.PermissionHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.classes.MimeTypes;
import project.person.Person;
import project.utils.Utils;

public class UpdateOrInsertPersonActivity extends AppCompatActivity {
  private static final int OPEN_DOCUMENT_CODE = 2;
  //widgets
  @BindView(R.id.imgLeft)
  ImageView imgLeft;
  @BindView(R.id.txtTitle)
  TextView txtTitle;
  @BindView(R.id.app_loading)
  LinearLayout app_loading;
  @BindView(R.id.app_no_internet)
  LinearLayout app_no_internet;
  @BindView(R.id.btnNONet)
  Button btnNONet;
  @BindView(R.id.txtNONetTitle)
  TextView txtNONetTitle;
  @BindView(R.id.img)
  ImageView img;
  @BindView(R.id.edtName)
  EditText edtName;
  @BindView(R.id.edtDesc)
  EditText edtDesc;
  @BindView(R.id.cbShowUser)
  CheckBox cbShowUser;


  //filds
  String realPath_1 = "";
  String file_1 = "";
  boolean isKitKat = false;
  private boolean wating = false;
  private List<Person> personList;
  private int WRITE_SDCARD_STORAGE_REQ = 1;
  private int personId = 0;
  private PermissionHelper permissionHelper;
  private boolean isShow;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_update_or_insert_person);
    ButterKnife.bind(this);

    initFilds();
    initWidgets();

  }


  //------------------------ GET DATA FROM NET ---------------------------

  //------------------------ INITIALS ------------------------
  private void initFilds() {
    personList = new ArrayList<>();
    permissionHelper = new PermissionHelper(this, new String[]{
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    }, WRITE_SDCARD_STORAGE_REQ);
  }

  private void initWidgets() {
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
    txtTitle.setText("Update Person");


    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      personId = bundle.getInt("id");
      edtName.setText(" " + bundle.getString("name"));
      edtDesc.setText(" " + bundle.getString("desc"));
      isShow = bundle.getInt("isShow") == 1?true:false;

      GlideApp.with(this)
        .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PERSON + bundle.getString("img")))
        .placeholder(this.getResources().getDrawable(R.drawable.logo))
        .into(img);
    } else {
      edtName.setText("");
      edtDesc.setText("");
    }

    if (isShow) {
      cbShowUser.setChecked(true);
      cbShowUser.setText("don't show user");
    }else {
      cbShowUser.setChecked(false);
      cbShowUser.setText("show user");
    }

    cbShowUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isChecked())
          cbShowUser.setText("don't show user");
        else
          cbShowUser.setText("show user");
      }
    });
  }

  //------------------------ EVENTS ---------------------------
  @OnClick(R.id.imgLeft)
  void imgLeftClicked(View v) {
    this.onBackPressed();
  }

  @OnClick(R.id.btnSubmit)
  void btnSubmitClicked(View v) {

    app_loading.setVisibility(View.VISIBLE);

    if (wating)
      return;

    wating = true;

    HashMap<String, List<String>> params = new HashMap<>();
    params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
    params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));
    params.put("personId", Collections.singletonList(String.valueOf(personId)));
    params.put("name", Collections.singletonList(edtName.getText().toString().trim() + " "));
    params.put("desc", Collections.singletonList(edtDesc.getText().toString().trim() + " "));
    params.put("isShow", Collections.singletonList(String.valueOf(cbShowUser.isChecked() ? 1 : 0)));


    if (realPath_1.equals("") || realPath_1 == null) {
      Ion.with(this)
        .load(Utils.checkVersionAndBuildUrl("CatalogApp_Api/v1/panel/insert_update_person.php"))
        .setTimeout(Consts.DEFUALT_TIME_OUT)
        .setBodyParameters(params)
        .asString().setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          app_loading.setVisibility(View.GONE);
          wating = false;

          if (e != null) {
            Log.i("UPDATE_PERSON", "error: " + e.toString());
            return;
          }

          Log.i("UPDATE_PERSON", "result: " + result);
          try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("error")) {
              Toast.makeText(UpdateOrInsertPersonActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              return;
            }

            Toast.makeText(UpdateOrInsertPersonActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


          } catch (JSONException e1) {
            e1.printStackTrace();
          }
        }
      });
      return;
    }

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl("CatalogApp_Api/v1/panel/insert_update_person.php"))
      .setTimeout(Consts.DEFUALT_TIME_OUT)
      .uploadProgressHandler(new ProgressCallback() {
        @Override
        public void onProgress(long uploaded, long total) {
          int progress = (int) ((uploaded * 1000) / total);
//                        setProgressOfDialog(progress);
          app_loading.setVisibility(View.VISIBLE);
        }
      })
      .setMultipartFile("file1", MimeTypes.getMimeType("jpg"),
        new File(realPath_1))
      .setMultipartParameters(params)
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String strResult) {
//                        sendCompleted(e, strResult, message);
          app_loading.setVisibility(View.GONE);
          wating = false;

          if (e != null) {
            Log.i("UPDATE_PERSON", "error: " + e.toString());
            return;
          }

          Log.i("UPDATE_PERSON", "result: " + strResult);
          try {
            JSONObject jsonObject = new JSONObject(strResult);
            if (jsonObject.getBoolean("error")) {
              Toast.makeText(UpdateOrInsertPersonActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              return;
            }
            Toast.makeText(UpdateOrInsertPersonActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

          } catch (JSONException e1) {
            e1.printStackTrace();
          }
        }
      });
  }

  @OnClick(R.id.cbShowUser)
  void rbShowUserClicked(View v) {

//    if (!isChecked) {
//      radioButton.setChecked(true);
//    } else {
//      radioButton.setChecked(false);
//    }
  }

  @OnClick(R.id.rLayImg)
  void rLayImgClicked(View v) {
    if (ContextCompat.checkSelfPermission(UpdateOrInsertPersonActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
      == PackageManager.PERMISSION_GRANTED) {
      showFileChooser();
    }
    writeExternalStoragePersmission();
  }

  private void showFileChooser() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("image/*");
      isKitKat = true;
      startActivityForResult(Intent.createChooser(intent, "Select file"), OPEN_DOCUMENT_CODE);
    } else {
      isKitKat = false;
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select file"), OPEN_DOCUMENT_CODE);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == OPEN_DOCUMENT_CODE) {

      if (data != null && data.getData() != null && resultCode == this.RESULT_OK) {

        boolean isImageFromGoogleDrive = false;

        Uri uri = data.getData();

        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
          if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
              realPath_1 = Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {
              Pattern DIR_SEPORATOR = Pattern.compile("/");
              Set<String> rv = new HashSet<>();
              String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
              String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
              String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
              if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                if (TextUtils.isEmpty(rawExternalStorage)) {
                  rv.add("/storage/sdcard0");
                } else {
                  rv.add(rawExternalStorage);
                }
              } else {
                String rawUserId;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                  rawUserId = "";
                } else {
                  String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                  String[] folders = DIR_SEPORATOR.split(path);
                  String lastFolder = folders[folders.length - 1];
                  boolean isDigit = false;
                  try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                  } catch (NumberFormatException ignored) {
                  }
                  rawUserId = isDigit ? lastFolder : "";
                }
                if (TextUtils.isEmpty(rawUserId)) {
                  rv.add(rawEmulatedStorageTarget);
                } else {
                  rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                }
              }
              if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
              }
              String[] temp = rv.toArray(new String[rv.size()]);
              for (int i = 0; i < temp.length; i++) {
                File tempf = new File(temp[i] + "/" + split[1]);
                if (tempf.exists()) {
                  realPath_1 = temp[i] + "/" + split[1];
                  GlideApp.with(this)
                    .load(realPath_1)
                    .placeholder(this.getResources().getDrawable(R.drawable.logo))
                    .into(img);
                }
              }
            }
          } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
            String id = DocumentsContract.getDocumentId(uri);
            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            Cursor cursor = null;
            String column = "_data";
            String[] projection = {column};
            try {
              cursor = this.getContentResolver().query(contentUri, projection, null, null,
                null);
              if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                realPath_1 = cursor.getString(column_index);
                GlideApp.with(this)
                  .load(realPath_1)
                  .placeholder(this.getResources().getDrawable(R.drawable.logo))
                  .into(img);
              }
            } finally {
              if (cursor != null)
                cursor.close();
            }
          } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
              contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
              contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
              contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            String selection = "_id=?";
            String[] selectionArgs = new String[]{split[1]};

            Cursor cursor = null;
            String column = "_data";
            String[] projection = {column};

            try {
              cursor = this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
              if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                realPath_1 = cursor.getString(column_index);
                GlideApp.with(this)
                  .load(realPath_1)
                  .placeholder(this.getResources().getDrawable(R.drawable.logo))
                  .into(img);
              }
            } finally {
              if (cursor != null)
                cursor.close();
            }
          } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
            isImageFromGoogleDrive = true;
          }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
          Cursor cursor = null;
          String column = "_data";
          String[] projection = {column};

          try {
            cursor = this.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
              int column_index = cursor.getColumnIndexOrThrow(column);
              realPath_1 = cursor.getString(column_index);
              GlideApp.with(this)
                .load(realPath_1)
                .placeholder(this.getResources().getDrawable(R.drawable.logo))
                .into(img);
            }
          } finally {
            if (cursor != null)
              cursor.close();
          }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
          realPath_1 = uri.getPath();
          GlideApp.with(this)
            .load(realPath_1)
            .placeholder(this.getResources().getDrawable(R.drawable.logo))
            .into(img);
        }

        try {
          Log.d("IMAGE", "Real Path 1 : " + realPath_1);
          file_1 = realPath_1.substring(realPath_1.lastIndexOf('/') + 1, realPath_1.length());

          GlideApp.with(this)
            .load(realPath_1)
            .placeholder(this.getResources().getDrawable(R.drawable.logo))
            .into(img);

          Log.i("File Name 1 ", file_1);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void writeExternalStoragePersmission() {

    permissionHelper.request(new PermissionHelper.PermissionCallback() {
      @Override
      public void onPermissionGranted() {
      }

      @Override
      public void onIndividualPermissionGranted(@NotNull String[] strings) {
      }

      @Override
      public void onPermissionDenied() {

        final AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateOrInsertPersonActivity.this);
        @SuppressLint("InflateParams")
        View permissioDialog = LayoutInflater.from(UpdateOrInsertPersonActivity.this).inflate(R.layout.dialog_permission, null, false);
        Button btnPermissionOk, btnPermissionCancel;


        btnPermissionOk = permissioDialog.findViewById(R.id.btnPermissionOk);
        btnPermissionCancel = permissioDialog.findViewById(R.id.btnPermissionCancel);

        builder.setView(permissioDialog);
        builder.setCancelable(false);
        alertDialog = builder.create();

        btnPermissionOk.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            writeExternalStoragePersmission();
            alertDialog.dismiss();
          }
        });
        btnPermissionCancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            alertDialog.dismiss();
          }
        });
        alertDialog.show();
      }

      @Override
      public void onPermissionDeniedBySystem() {

      }
    });
  }//end  writeExternalStoragePersmission()

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //my permissionHelper
    permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }


}

package project.management_panel.product;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.glide.slider.library.svg.GlideApp;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.master.permissionhelper.PermissionHelper;
import com.politecoder.catalog.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import project.category.Category;
import project.classes.App;
import project.classes.Consts;
import project.classes.MimeTypes;
import project.management_panel.category.UpdateOrInsertCategoryActivity;
import project.utils.Utils;

public class UpdateOrInsertProductActivity extends AppCompatActivity {
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
    @BindView(R.id.spCategory)
    Spinner spCategory;
    @BindView(R.id.edtTitle)
    EditText edtTitle;
    @BindView(R.id.edtDesc)
    EditText edtDesc;
    @BindView(R.id.edtPrice)
    EditText edtPrice;


    //filds
    private boolean wating = false;
    private List<Category> categoryList;
    private int seen = 0;
    private static final int OPEN_DOCUMENT_CODE = 2;
    String realPath_1 = "";
    String file_1 = "";
    private int WRITE_SDCARD_STORAGE_REQ = 1;
    private int productId = 0;
    private int categoryId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();

        App.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCategoryListFromNet();
            }
        }, 100);
    }


    //------------------------ INITIALS ------------------------
    private void initFilds() {
        categoryList = new ArrayList<>();
        permissionHelper = new PermissionHelper(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, WRITE_SDCARD_STORAGE_REQ);
    }

    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Update Product");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getInt("id");
            categoryId = bundle.getInt("categoryId");
            seen = bundle.getInt("seen");
            edtTitle.setText(" " + bundle.getString("title"));
            edtDesc.setText(" " + bundle.getString("desc"));
            edtPrice.setText(" " + bundle.getString("price").replace("$", ""));

            GlideApp.with(this)
                    .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PRODUCT + bundle.getString("img")))
                    .placeholder(this.getResources().getDrawable(R.drawable.logo))
                    .into(img);
        } else {
            edtTitle.setText("");
            edtDesc.setText("");
            edtPrice.setText("");
        }

    }


    private void initSpinner() {
        ArrayAdapter<Category> aa = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoryList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCategory.setAdapter(aa);
        Category c =(Category) spCategory.getSelectedItem();
        categoryId = c.getId();

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Category category = (Category) adapterView.getItemAtPosition(position);
                categoryId = category.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    //------------------------ GET DATA FROM NET ---------------------------
    private void getCategoryListFromNet() {
        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.GONE);

        if (wating) {
            return;
        }
        wating = true;

        HashMap<String, List<String>> params = new HashMap<>();
        params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
        params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));
        params.put("personId", Collections.singletonList(String.valueOf(App.preferences.getInt(Consts.PERSON_ID, 0))));

        Ion.with(UpdateOrInsertProductActivity.this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_PANEL_CATEGORIES))
                .setBodyParameters(params)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        wating = false;
                        app_loading.setVisibility(View.GONE);
                        if (e != null) {
                            app_no_internet.setVisibility(View.VISIBLE);
                            btnNONet.setText("Retry");
                            txtNONetTitle.setText("Error in internet Connection!");
                            return;
                        }

                        Log.i("CATEGORY", "result: " + result);
                        try {
                            JSONObject allCategoryJsonObject = new JSONObject(result);

                            if (allCategoryJsonObject.getBoolean("error")) {
                                Toast.makeText(UpdateOrInsertProductActivity.this, allCategoryJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONArray categoriesJa = allCategoryJsonObject.getJSONArray("categories");
                            categoryList.clear();
                            for (int i = 0; i < categoriesJa.length(); i++) {
                                JSONObject cJo = categoriesJa.getJSONObject(i);
                                Category category = new Category();
                                category.setId(cJo.getInt("id"));
                                category.setTitle(cJo.getString("title"));
                                category.setImg(cJo.getString("img"));
                                category.setParentId(cJo.getInt("parentId"));

                                categoryList.add(category);
                            }
                        } catch (JSONException e2) {
                        }

                        initSpinner();
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
        params.put("personId", Collections.singletonList(String.valueOf(App.preferences.getInt(Consts.PERSON_ID, 0))));
        params.put("productId", Collections.singletonList(String.valueOf(productId)));
        params.put("categoryId", Collections.singletonList(String.valueOf(categoryId)));
        params.put("title", Collections.singletonList(edtTitle.getText().toString().trim() + " "));
        params.put("desc", Collections.singletonList(edtDesc.getText().toString().trim() + " "));
        params.put("price", Collections.singletonList(edtPrice.getText().toString().trim() + " "));


        if (realPath_1.equals("") || realPath_1 == null) {
            Ion.with(this)
                    .load(Utils.checkVersionAndBuildUrl("CatalogApp_Api/v1/panel/insert_update_product.php"))
                    .setTimeout(Consts.DEFUALT_TIME_OUT)
                    .setBodyParameters(params)
                    .asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    app_loading.setVisibility(View.GONE);
                    wating = false;

                    if (e != null) {
                        Log.i("UPDATE_PRODUCT", "error: " + e.toString());
                        return;
                    }

                    Log.i("UPDATE_PRODUCT", "result: " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getBoolean("error")) {
                            Toast.makeText(UpdateOrInsertProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(UpdateOrInsertProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            return;
        }

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl("CatalogApp_Api/v1/panel/insert_update_product.php"))
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
                            Log.i("UPDATE_PRODUCT", "error: " + e.toString());
                            return;
                        }

                        Log.i("UPDATE_PRODUCT", "result: " + strResult);
                        try {
                            JSONObject jsonObject = new JSONObject(strResult);
                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(UpdateOrInsertProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(UpdateOrInsertProductActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    boolean isKitKat = false;

    @OnClick(R.id.rLayImg)
    void rLayImgClicked(View v) {
        if (ContextCompat.checkSelfPermission(UpdateOrInsertProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    private PermissionHelper permissionHelper;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateOrInsertProductActivity.this);
                @SuppressLint("InflateParams")
                View permissioDialog = LayoutInflater.from(UpdateOrInsertProductActivity.this).inflate(R.layout.dialog_permission, null, false);
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

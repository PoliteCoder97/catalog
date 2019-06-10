package project.register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.politecoder.catalog.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.management_panel.MainPanelActivity;
import project.utils.Utils;

public class PasswordActivity extends AppCompatActivity {

    //widgets
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.txtPhoneNumber)
    TextView txtPhoneNumber;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.app_loading)
    LinearLayout app_loading;
    @BindView(R.id.app_no_internet)
    LinearLayout app_no_internet;

    //filds
    private boolean wating = false;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();


    }

    //----------------------- INITIALS ------------------------------------------
    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText(" ");
        txtPhoneNumber.setText(" " + phoneNumber);
    }

    private void initFilds() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNumber = bundle.getString("phoneNumber");
        }
    }

    //----------------------- GET DATA FROM NET ------------------------------------------
    private void getDataFromNet(String password, View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(getResources().getDrawable(R.drawable.btn_sign_in));
        }

        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.INVISIBLE);

        if (wating)
            return;

        wating = true;

        HashMap<String, List<String>> params = new HashMap<>();
        params.put("action", Collections.singletonList("check_password"));
        params.put("phoneNumber", Collections.singletonList(phoneNumber));
        params.put("password", Collections.singletonList(password));
        params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
        params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.USER_REGISTER))
                .setTimeout(Consts.DEFUALT_TIME_OUT)
                .setBodyParameters(params)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        wating = false;
                        app_loading.setVisibility(View.INVISIBLE);

                        if (e != null) {
                            e.printStackTrace();
                            app_no_internet.setVisibility(View.VISIBLE);
                            app_no_internet.findViewById(R.id.btnNONet).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getDataFromNet(phoneNumber, view);
                                }
                            });

                            TextView textView = app_no_internet.findViewById(R.id.txtNONetTitle);
                            textView.setText(getResources().getString(R.string.no_net_error));

                            return;
                        }//end if

                        Log.i("PASSWORD", "result: " + result);

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(PasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (jsonObject.getBoolean("isChecked")) {
                                App.preferences.edit().putBoolean(Consts.IS_SIGN_UP, true).apply();
                                App.preferences.edit().putInt(Consts.PERSON_ID, jsonObject.getInt("personId")).apply();
                                App.preferences.edit().putString(Consts.TOKEN, jsonObject.getString("token")).apply();
                                App.preferences.edit().putString(Consts.REFRESH_TOKEN, jsonObject.getString("refresh_token")).apply();

                                Intent intent = new Intent(PasswordActivity.this, MainPanelActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                PasswordActivity.this.startActivity(intent);
                                PasswordActivity.this.finish();

                                return;
                            }

                            Toast.makeText(PasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    //----------------------- EVENTS ------------------------------------------
    @OnClick(R.id.btnSubmit)
    void btnSubmitClicked(View view) {
        String password = edtPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(password) || !password.equals("")) {
            App.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDataFromNet(password, view);
                }
            }, 100);
        } else {
            edtPassword.requestFocus();
            Toast.makeText(this, "pls fill the phone ", Toast.LENGTH_SHORT).show();
            view.setBackgroundColor(Color.parseColor("#FF0000"));
        }
    }

    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

}

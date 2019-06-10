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
import project.utils.Utils;

public class SignInActivity extends AppCompatActivity {

    //widgets
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.edtPhone)
    EditText edtPhone;
    @BindView(R.id.app_loading)
    LinearLayout app_loading;
    @BindView(R.id.app_no_internet)
    LinearLayout app_no_internet;

    //filds
    private boolean wating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();


    }

    //----------------------- INITIALS ------------------------------------------
    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Sing In");

    }

    private void initFilds() {

    }

    //----------------------- GET DATA FROM NET ------------------------------------------
    private void getDataFromNet(String phoneNumber, View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(getResources().getDrawable(R.drawable.btn_sign_in));
        }

        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.INVISIBLE);

        if (wating)
            return;

        wating = true;

        HashMap<String, List<String>> params = new HashMap<>();
        params.put("action", Collections.singletonList("sign_in"));
        params.put("phoneNumber", Collections.singletonList(phoneNumber));
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

                        Log.i("SIGNIN", "result: " + result);

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(SignInActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (jsonObject.getInt("isRegisterd") == 1) {
                                Toast.makeText(SignInActivity.this, "user is register", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, PasswordActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                SignInActivity.this.startActivity(intent);
                                SignInActivity.this.finish();

                            } else {
                                Toast.makeText(SignInActivity.this, "user isn't register sign up", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                SignInActivity.this.startActivity(intent);
                                SignInActivity.this.finish();
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });

    }

    //----------------------- EVENTS ------------------------------------------

    @OnClick(R.id.btnSubmit)
    void btnSubmitClicked(View view) {
        String phoneNumber = edtPhone.getText().toString().trim();

        if (!TextUtils.isEmpty(phoneNumber) || !phoneNumber.equals("")) {
            App.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDataFromNet(phoneNumber, view);
                }
            }, 100);
        } else {
            edtPhone.requestFocus();
            Toast.makeText(this, "pls fill the phone ", Toast.LENGTH_SHORT).show();
            view.setBackgroundColor(Color.parseColor("#FF0000"));
        }
    }


    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

}

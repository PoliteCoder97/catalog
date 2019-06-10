package project.certificate;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.politecoder.catalog.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.utils.Utils;
import project.view.BaseActivity;

public class CertificateActivity extends BaseActivity {

    //widgets
    @BindView(R.id.lLayHolder)
    LinearLayout lLayHolder;
    @BindView(R.id.app_loading)
    LinearLayout app_loading;
    @BindView(R.id.app_no_internet)
    LinearLayout app_no_internet;
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;


    //filds
    private boolean wating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();

        App.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataFromNet();
            }
        }, 100);
    }

    //-------------------------- INITIALS -------------------
    private void initFilds() {

    }

    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Certificates");
    }

    //--------------------------- GET DATA FROM NET ---------
    private void getDataFromNet() {
        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.GONE);
        if (wating)
            return;

        wating = true;

        HashMap<String, List<String>> params = new HashMap<>();
        params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
        params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_CERTIFICATES))
                .setBodyParameters(params)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onCompleted(Exception e, String result) {
                        wating = false;
                        app_loading.setVisibility(View.GONE);

                        if (e != null) {
                            app_no_internet.setVisibility(View.VISIBLE);
                            Button btnRetry = app_no_internet.findViewById(R.id.btnNONet);
                            TextView txtNONetTitle = app_no_internet.findViewById(R.id.txtNONetTitle);

                            btnRetry.setText("Retry");
                            txtNONetTitle.setText(getResources().getString(R.string.no_net_error));

                            btnRetry.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getDataFromNet();
                                }
                            });

                            return;
                        }

                        Log.i("CERTIFICATE", "result: " + result);

                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            if (jsonObject.getBoolean("error")) {
                                jsonObject.getString("message");
                                return;
                            }

                            String certificates = jsonObject.getString("certificates");
                            JSONArray jsonArray = new JSONArray(certificates);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);

                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                layoutParams.setMargins((int) getResources().getDimension(R.dimen.view_small),
                                        (int) getResources().getDimension(R.dimen.view_small),
                                        (int) getResources().getDimension(R.dimen.view_small),
                                        (int) getResources().getDimension(R.dimen.view_small));

                                Button button = new Button(CertificateActivity.this);
                                button.setLayoutParams(layoutParams);
                                button.setBackground(getResources().getDrawable(R.drawable.btn_certificate_background));
                                button.setText(" " + jo.getString("title"));
                                button.setGravity(Gravity.CENTER);

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(CertificateActivity.this, ShowCertificateActivity.class);
                                        try {

                                            intent.putExtra("title", jo.getString("title"));
                                            intent.putExtra("img", jo.getString("img"));

                                            CertificateActivity.this.startActivity(intent);
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                });

                                lLayHolder.addView(button);

                            }


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }

    //--------------------- EVENTS ---------------------------------

    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }
}

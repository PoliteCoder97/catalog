package project.management_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MainPanelActivity extends AppCompatActivity {

    //widgets
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
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
        setContentView(R.layout.activity_main_panel);
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

    //------------------------ INITIALS ------------------------
    private void initFilds() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Panel");
    }

    private void initWidgets() {

    }

    //------------------------ GET DATA FROM NET ----------------
    private void getDataFromNet() {

        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.INVISIBLE);

        if (wating)
            return;

        wating = true;

        HashMap<String, List<String>> params = new HashMap<>();
        params.put("action", Collections.singletonList("check_password"));

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
                                    getDataFromNet();
                                }
                            });

                            TextView textView = app_no_internet.findViewById(R.id.txtNONetTitle);
                            textView.setText(getResources().getString(R.string.no_net_error));

                            return;
                        }//end if

                        Log.i("MainPanelActivity", "result: " + result);

                        try {
                            JSONObject jsonObject = new JSONObject(result);


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    //------------------------ EVENTS ---------------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }
}

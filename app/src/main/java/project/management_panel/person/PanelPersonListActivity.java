package project.management_panel.person;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.politecoder.catalog.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.category.Category;
import project.classes.App;
import project.classes.Consts;
import project.management_panel.category.PanelCategoryListActivity;
import project.management_panel.category.PanelCategoryListAdapter;
import project.management_panel.category.PanelCategoryListEventListener;
import project.management_panel.category.UpdateOrInsertCategoryActivity;
import project.person.Person;
import project.utils.Utils;

public class PanelPersonListActivity extends AppCompatActivity {
    //widgets
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.app_loading)
    LinearLayout app_loading;
    @BindView(R.id.app_no_internet)
    LinearLayout app_no_internet;
    @BindView(R.id.txtNONetTitle)
    TextView txtNONetTitle;
    @BindView(R.id.btnNONet)
    Button btnNONet;
    @BindView(R.id.rclv)
    XRecyclerView rclv;

    //filds
    PanelPersonListAdapter panelPersonListAdapter;
    private boolean wating = false;
    List<Person> personList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_category_list);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();

        App.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPersonListFromNet();
            }
        }, 100);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //------------------------- INITIALS -----------------------
    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Persons");
    }

    private void initFilds() {
        personList = new ArrayList<>();
    }

    private void initRclv() {
        if (personList == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rclv.setLayoutManager(linearLayoutManager);
        rclv.setHasFixedSize(true);
        rclv.setPullRefreshEnabled(false);
        rclv.setLoadingMoreEnabled(false);

        panelPersonListAdapter = new PanelPersonListAdapter(this, personList);
        rclv.setAdapter(panelPersonListAdapter);
        rclv.refresh();
    }


    //------------------------- GET DATA FROM NET -----------------------

    private void getPersonListFromNet() {
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

        Ion.with(PanelPersonListActivity.this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_PANEL_PERSONS))
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

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Log.i("PANEL_PERSON", "result: " + jsonObject.toString(4));
                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(PanelPersonListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONArray jsonArray = jsonObject.getJSONArray("persons");
                            personList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);
                                Person person = new Person();
                                person.setId(jo.getInt("id"));
                                person.setName(jo.getString("name"));
                                person.setDesc(jo.getString("desc"));
                                person.setImg(jo.getString("img"));

                                personList.add(person);
                            }
                        } catch (JSONException e2) {
                        }

                        initRclv();
                    }
                });
    }

    //------------------------- EVENTS -----------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

    @OnClick(R.id.btnAddCategory)
    void btnAddCategoryClicked(View v) {
        Intent intent = new Intent(PanelPersonListActivity.this, UpdateOrInsertPersonActivity.class);
        this.startActivity(intent);

    }

    @OnClick(R.id.btnNONet)
    void btnNONetClicked(View v) {
        getPersonListFromNet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPanelPersonListEventListener(PanelPersonListEventListener event) {
        Intent intent = new Intent(PanelPersonListActivity.this, UpdateOrInsertPersonActivity.class);
        intent.putExtra("id", event.getPerson().getId());
        intent.putExtra("name", event.getPerson().getName());
        intent.putExtra("desc", event.getPerson().getDesc());
        intent.putExtra("img", event.getPerson().getImg());

        this.startActivity(intent);

    }
}
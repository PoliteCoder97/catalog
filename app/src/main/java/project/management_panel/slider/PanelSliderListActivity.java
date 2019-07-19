package project.management_panel.slider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amaloffice.catalog.R;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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
import project.classes.App;
import project.classes.Consts;
import project.classes.Slide;
import project.utils.Utils;

public class PanelSliderListActivity extends AppCompatActivity {
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
  PanelSliderListAdapter panelSlideListAdapter;
  List<Slide> slideList;
  private boolean wating = false;

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
        getSlideListFromNet();
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
    txtTitle.setText("Slides");
  }

  private void initFilds() {
    slideList = new ArrayList<>();
  }

  private void initRclv() {
    if (slideList == null) {
      return;
    }
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    rclv.setLayoutManager(linearLayoutManager);
    rclv.setHasFixedSize(true);
    rclv.setPullRefreshEnabled(false);
    rclv.setLoadingMoreEnabled(false);

    panelSlideListAdapter = new PanelSliderListAdapter(this, slideList);
    rclv.setAdapter(panelSlideListAdapter);
    rclv.refresh();
  }


  //------------------------- GET DATA FROM NET -----------------------

  private void getSlideListFromNet() {
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

    Ion.with(PanelSliderListActivity.this)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_PANEL_SLIDES))
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
          Log.i("PANEL_Slide", "result: " + result);
          try {

            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.getBoolean("error")) {
              Toast.makeText(PanelSliderListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              return;
            }
            JSONArray jsonArray = jsonObject.getJSONArray("slides");
            slideList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jo = jsonArray.getJSONObject(i);
              Slide Slide = new Slide();
              Slide.setId(jo.getInt("id"));
              Slide.setImageName(jo.getString("image"));

              slideList.add(Slide);
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
    Intent intent = new Intent(PanelSliderListActivity.this, UpdateOrInsertSliderActivity.class);
    this.startActivity(intent);

  }

  @OnClick(R.id.btnNONet)
  void btnNONetClicked(View v) {
    getSlideListFromNet();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onPanelSlideListLongEventListener(PanelSliderListLongEventListener event) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("delete user");
    builder.setMessage("Do you sure to delete this user?");

    builder.setPositiveButton("Yes", (dialogInterface, i) -> {

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
      params.put("slideId", Collections.singletonList(String.valueOf(event.getSlide().getId())));

      Ion.with(PanelSliderListActivity.this)
        .load(Utils.checkVersionAndBuildUrl(Consts.DELETE_SLIDER))
        .setBodyParameters(params)
        .asString()
        .setCallback(new FutureCallback<String>() {
          @Override
          public void onCompleted(Exception e, String result) {
            dialogInterface.dismiss();
            wating = false;
            app_loading.setVisibility(View.GONE);
            if (e != null) {
              app_no_internet.setVisibility(View.VISIBLE);
              btnNONet.setText("Retry");
              txtNONetTitle.setText("Error in internet Connection!");
              return;
            }
            Log.i("DELETESlide", "result: " + result);
            try {
              JSONObject jsonObject = new JSONObject(result);
              if (jsonObject.getBoolean("error")) {
                Toast.makeText(PanelSliderListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                return;
              }

              slideList.remove(event.getSlide());
              panelSlideListAdapter = new PanelSliderListAdapter(PanelSliderListActivity.this, slideList);
              rclv.setAdapter(panelSlideListAdapter);

            } catch (JSONException e2) {
            }

          }
        });


    });

    builder.setNegativeButton("No", (dialogInterface, i) -> {
      dialogInterface.dismiss();
    });

    builder.create().show();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onPanelSlideListEventListener(PanelSliderListEventListener event) {
    Intent intent = new Intent(PanelSliderListActivity.this, UpdateOrInsertSliderActivity.class);
    intent.putExtra("id", event.getSlide().getId());
    intent.putExtra("image", event.getSlide().getImageName());

    this.startActivity(intent);

  }
}
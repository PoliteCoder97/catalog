package project.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.utils.Utils;
import project.view.BaseActivity;

public class PersonListActivity extends BaseActivity {
  //widgets
  @BindView(R.id.app_no_internet)
  LinearLayout app_no_internet;
  @BindView(R.id.app_loading)
  LinearLayout app_loading;
  @BindView(R.id.rclv)
  XRecyclerView rclv;
  @BindView(R.id.imgLeft)
  ImageView imgLeft;
  @BindView(R.id.txtTitle)
  TextView txtTitle;
  @BindView(R.id.txtNONetTitle)
  TextView txtNONetTitle;
  @BindView(R.id.btnNONet)
  Button btnNONet;

  //filds
  private boolean wating = false;
  private int heightScrooled = 0;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_person_list);
    ButterKnife.bind(this);

    initFilds();
    initWidgets();

    App.gethandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getDataFromNet();
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

  //---------------------------------- INITIALS -----------------------------------
  private void initWidgets() {
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
    txtTitle.setText("Contact Us");
  }

  private void initFilds() {

  }

  private void initXRecyclerView(List<Person> personList) {

    rclv.setLayoutManager(new LinearLayoutManager(this));
    rclv.setPullRefreshEnabled(false);
    rclv.setLoadingMoreEnabled(false);

    if (personList == null) {
      personList = App.database.getPersonDao().getAllPeople();
    }

    PersonListAdapter adapter = new PersonListAdapter(this, personList);
    rclv.setAdapter(adapter);
    rclv.refresh();

    rclv.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        heightScrooled += dy;
        if (heightScrooled>60){
          app_no_internet.animate().translationY(app_no_internet.getHeight()+10);
        }else {
          app_no_internet.animate().translationY(0);
        }
        super.onScrolled(recyclerView, dx, dy);
      }
    });
  }


  //---------------------------------- GET DATA FROM NET -----------------------------------

  private void getDataFromNet() {
    app_loading.setVisibility(View.VISIBLE);
    app_no_internet.setVisibility(View.GONE);
    if (wating)
      return;

    wating = true;

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_PERSONS))
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          wating = false;
          app_loading.setVisibility(View.GONE);
          if (e != null) {
            Log.e("ERROR", "error: " + e.toString());
            initXRecyclerView(null);
            app_no_internet.setVisibility(View.VISIBLE);
            btnNONet.setText("Retry");
            txtNONetTitle.setText("" + PersonListActivity.this.getResources()
              .getString(R.string.no_net_error));
            return;
          }

          Log.i("RESULT", "result: " + result);
          try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("error")) {
              Toast.makeText(PersonListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              initXRecyclerView(null);
              return;
            }

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            App.database.getPersonDao().delete();
            List<Person> personList = new ArrayList<>();
            personList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject object = jsonArray.getJSONObject(i);
              Person person = new Person();

              person.setId(object.getInt("id"));
              person.setName(object.getString("name"));
              person.setImg(object.getString("img"));
              person.setDesc(object.getString("desc"));
              person.setIsMentor(object.getInt("isMentor"));
              personList.add(person);
              App.database.getPersonDao().insert(person);
            }

            initXRecyclerView(personList);

          } catch (JSONException e1) {
            e1.printStackTrace();
          }
        }
      });

  }

  //---------------------------------- EVENTS -----------------------------------
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onCategoryEventListener(PersonEventListener event) {/* Do something */

    Intent intent = new Intent(PersonListActivity.this, PersonContactActivity.class);
    intent.putExtra("id", event.getPerson().getId());
    intent.putExtra("name", event.getPerson().getName());
    intent.putExtra("desc", event.getPerson().getDesc());
    intent.putExtra("img", event.getPerson().getImg());
    intent.putExtra("isMentor", event.getPerson().getIsMentor());

    this.startActivity(intent);
  }

  @OnClick(R.id.btnNONet)
  void btnNoNetClicked(View v) {
    getDataFromNet();
  }

  @OnClick(R.id.imgLeft)
  void imgLeftClicked(View v) {
    this.onBackPressed();
  }

}

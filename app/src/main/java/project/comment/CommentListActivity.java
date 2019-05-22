package project.comment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.Consts;
import project.product.ProductEventListener;
import project.utils.Utils;

public class CommentListActivity extends AppCompatActivity {


  //widgets
  @BindView(R.id.fabNewComment)
  FloatingActionButton fabNewComment;
  @BindView(R.id.rclv)
  XRecyclerView rclv;
  @BindView(R.id.imgLeft)
  ImageView imgLeft;
  @BindView(R.id.txtTitle)
  TextView txtTitle;
  @BindView(R.id.app_no_internet)
  LinearLayout app_no_internet;
  @BindView(R.id.app_loading)
  LinearLayout app_loading;
  @BindView(R.id.txtNONetTitle)
  TextView txtNONetTitle;
  @BindView(R.id.btnNONet)
  Button btnNONet;

  //filds
  private boolean wating = false;
  private int productId = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment_list);
    ButterKnife.bind(this);

    initFilds();
    initWidgets();

    getDataFromNet();

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

  //---------------------------------------- INITIALS -------------------------------------
  private void initWidgets() {
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
    txtTitle.setText(" " + getIntent().getStringExtra("title"));
  }

  private void initFilds() {
    productId = getIntent().getIntExtra("productId", 0);
  }

  //---------------------------------------- GET DATA FROM NET -------------------------------------
  private void getDataFromNet() {

    app_loading.setVisibility(View.VISIBLE);
    app_no_internet.setVisibility(View.GONE);

    if (wating) {
      return;
    }
    wating = true;

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_COMMENTS))//TODO set uri for get data
      .setBodyParameter("productId", String.valueOf(productId))
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          app_loading.setVisibility(View.GONE);
          wating = false;
          if (e != null) {
            e.printStackTrace();
            app_no_internet.setVisibility(View.VISIBLE);
            btnNONet.setText("Retry");
            txtNONetTitle.setText("Error in internet Connection!");
            return;
          }

          Log.i("RESULT", "result: " + result);
        }
      });

  }


  //---------------------------------------- EVENTS -------------------------------------

  @OnClick(R.id.imgLeft)
  void imgLeftClicked(View v) {
    this.onBackPressed();
  }

  @OnClick(R.id.fabNewComment)
  void fabNewCommentClicked(View v) {
    Toast.makeText(this, "fab clicked!", Toast.LENGTH_SHORT).show();
  }

  @OnClick(R.id.btnNONet)
  void btnNONetClicked(View v) {

    getDataFromNet();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onCategoryEventListener(ProductEventListener event) {/* Do something */


  }

}

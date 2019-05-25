package project.comment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
  private CommentListAdapter adapter = null;

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

  private void initXRecyclerView(List<Comment> commentList) {
    rclv.setLayoutManager(new LinearLayoutManager(this));
    rclv.setLoadingMoreEnabled(false);
    rclv.setPullRefreshEnabled(false);

    if (commentList == null) {
      commentList = App.database.getCommentdao().getCommentList(productId);
    }
      adapter = new CommentListAdapter(this, commentList);

    rclv.setAdapter(adapter);
    rclv.refresh();
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
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_COMMENTS))
      .setBodyParameter("productId", String.valueOf(productId))
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          app_loading.setVisibility(View.GONE);
          wating = false;
          if (e != null) {
            e.printStackTrace();
            initXRecyclerView(null);
            initNoNetView();
            return;
          }//end if
          try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("error")) {
              Toast.makeText(CommentListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              return;
            }

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            List<Comment> commentList = new ArrayList<>();
            commentList.clear();
            App.database.getCommentdao().delete(productId);
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject object = jsonArray.getJSONObject(i);
              Comment comment = new Comment();
              comment.setId(object.getInt("id"));
              comment.setProductId(object.getInt("productId"));
              comment.setComment(object.getString("comment"));

              commentList.add(comment);
              App.database.getCommentdao().insert(comment);
            }

            initXRecyclerView(commentList);

          } catch (JSONException e1) {
            e1.printStackTrace();
          }
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
    //write record new note hear
    View view = LayoutInflater.from(this)
      .inflate(R.layout.dialog_record_comment, null, false);

    AlertDialog.Builder builder = new AlertDialog.Builder(CommentListActivity.this);
    builder.setView(view);

    final EditText edtComment = view.findViewById(R.id.edtComment);
    Button btnSave = view.findViewById(R.id.btnSave);
    Button btnCancel = view.findViewById(R.id.btnCancel);

    final AlertDialog dialog = builder.create();
    btnSave.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();

        final String comment = edtComment.getText().toString().trim();
        if (TextUtils.isEmpty(comment) || comment.length() == 0) {
          Toast.makeText(CommentListActivity.this, "You Most To Write Somthing...", Toast.LENGTH_SHORT).show();
          edtComment.requestFocus();
          return;
        }

        //send a Req to Server for Record
        app_loading.setVisibility(View.VISIBLE);
        if (wating)
          return;

        wating = true;

        Ion.with(CommentListActivity.this)
          .load(Utils.checkVersionAndBuildUrl(Consts.RECORD_COMMENTS))
          .setBodyParameter("productId", String.valueOf(productId))
          .setBodyParameter("comment", comment)
          .asString()
          .setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
              wating = false;
              app_loading.setVisibility(View.GONE);
              if (e != null) {
                e.printStackTrace();
                return;
              }

              Log.i("RESULT", "result: " + result);
              Comment cm = new Comment();
              cm.setComment(comment);
              cm.setProductId(productId);
              App.database.getCommentdao().insert(cm);

              if (adapter != null) {
                initXRecyclerView(null);
              }

            }
          });


      }
    });

    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        edtComment.setText("");
        dialog.dismiss();
      }
    });

//    dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
    dialog.show();

  }

  private void initNoNetView() {
    app_no_internet.setVisibility(View.VISIBLE);
    btnNONet.setText("Retry");
    txtNONetTitle.setText("Error in internet Connection!");
  }

  @OnClick(R.id.btnNONet)
  void btnNONetClicked(View v) {
    getDataFromNet();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onCategoryEventListener(ProductEventListener event) {/* Do something */
  }

}

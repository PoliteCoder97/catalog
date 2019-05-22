package project.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.utils.Utils;

public class ProductListActivity extends AppCompatActivity {

  //widgets
  @BindView(R.id.rclv)
  XRecyclerView rclv;
  @BindView(R.id.app_loading)
  LinearLayout app_loading;
  @BindView(R.id.app_no_internet)
  LinearLayout app_no_internet;
  @BindView(R.id.txtNONetTitle)
  TextView txtNONetTitle;
  @BindView(R.id.btnNONet)
  Button btnNONet;
  @BindView(R.id.txtTitle)
  TextView txtTitle;
  @BindView(R.id.imgLeft)
  ImageView imgLeft;


  private boolean wating = false;
  private int categoryId = 0;
  private String title;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product_list);
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
  //--------------------------------- INITIALS ---------------------------------------------

  private void initFilds() {
    categoryId = getIntent().getExtras().getInt("categoryId");
    title = getIntent().getExtras().getString("title");
  }

  private void initWidgets() {
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
    txtTitle.setText(" " + title);
  }

  private void initialRclv(List<Product> productList) {
//    llayNewestGoods.setVisibility(View.VISIBLE);

    LinearLayoutManager manager = new LinearLayoutManager(this);
//    rclvNewestGoods.setHasFixedSize(true);
    rclv.setLayoutManager(manager);
    rclv.setPullRefreshEnabled(false);
    rclv.setLoadingMoreEnabled(false);

    ProductListAdapter productListAdapter = null;

    if (productList == null || productList.size() == 0) {
      productList = App.database.getProductDao().getAllProducts(categoryId);
      productListAdapter = new ProductListAdapter(this, productList,false);
    }

    productListAdapter = new ProductListAdapter(this, productList,false);
    rclv.setAdapter(productListAdapter);
    rclv.refresh();
  }

  //--------------------------------- GET DATA FROM NET ---------------------------------------------
  private void getDataFromNet() {
    app_loading.setVisibility(View.VISIBLE);
    app_no_internet.setVisibility(View.GONE);

    if (wating) {
      return;
    }
    wating = true;

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_PRODUCT))
      .setBodyParameter("categoryId", String.valueOf(categoryId))
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          wating = false;
          app_loading.setVisibility(View.GONE);
          if (e != null) {
            e.printStackTrace();
            app_no_internet.setVisibility(View.VISIBLE);
            initialRclv(null);
            btnNONet.setText("Retry");
            txtNONetTitle.setText("Error in internet Connection!");
            return;
          }
          Log.i("RESULT", "result: " + result);

          List<Product> productList = new ArrayList<>();
          try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("error")) {

              Toast.makeText(ProductListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              initialRclv(null);
              return;
            }

            App.database.getProductDao().delete(categoryId);
            productList.clear();
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jo = jsonArray.getJSONObject(i);
              Product product = new Product();
              product.setId(jo.getInt("id"));
              product.setCategoryId(jo.getInt("categoryId"));
              product.setTitle(jo.getString("title"));
              product.setDesc(jo.getString("desc"));
              product.setImg(jo.getString("img"));
              product.setPrice(jo.getString("price"));
              product.setSeen(jo.getInt("seen"));

              SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
              try {
                Date date = format.parse(jo.getString("postedDate"));
                product.setPosted_date(date);
              } catch (ParseException ext) {
                e.printStackTrace();
              }
              productList.add(product);
              App.database.getProductDao().insert(product);
            }
            initialRclv(productList);
          } catch (JSONException e1) {
            e1.printStackTrace();
          }
        }
      });
  }

  //--------------------------------- EVENT LISTENERS ---------------------------------------------
  @OnClick(R.id.btnNONet)
  void btnNONetClicked(View v) {
    getDataFromNet();
  }

  @OnClick(R.id.imgLeft)
  void imgLeftClicked(View v) {
    this.onBackPressed();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onCategoryEventListener(ProductEventListener event) {/* Do something */

    Intent intent = new Intent(ProductListActivity.this, ProductActivity.class);
    intent.putExtra("id", event.getProduct().getId());
    intent.putExtra("title", event.getProduct().getTitle());
    intent.putExtra("categoryId", event.getProduct().getCategoryId());
    intent.putExtra("desc", event.getProduct().getDesc());
    intent.putExtra("img", event.getProduct().getImg());
    intent.putExtra("price", event.getProduct().getPrice());
    intent.putExtra("posted_date", event.getProduct().getPosted_date());

    ProductListActivity.this.startActivity(intent);
  }

}

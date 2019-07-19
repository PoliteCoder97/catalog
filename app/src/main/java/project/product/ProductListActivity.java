package project.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.utils.Utils;
import project.other.BaseActivity;

public class ProductListActivity extends BaseActivity {

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
    private int heightScrooled = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();

        App.getHandler().postDelayed(() -> getDataFromNet(), 100);

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

        if (productList == null) {
            productList = App.database.getProductDao().getAllProducts(categoryId);
        }

        productListAdapter = new ProductListAdapter(this, productList, false, false);
        rclv.setAdapter(productListAdapter);
        rclv.refresh();

        rclv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                heightScrooled += dy;
                if (heightScrooled > 60) {
                    app_no_internet.animate().translationY(app_no_internet.getHeight() + 10);
                } else {
                    app_no_internet.animate().translationY(0);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    //--------------------------------- GET DATA FROM NET ---------------------------------------------
    private void getDataFromNet() {
        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.GONE);

        if (wating) {
            return;
        }
        wating = true;
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
        params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));
        params.put("categoryId", Collections.singletonList(String.valueOf(categoryId)));

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_PRODUCT))
                .setBodyParameters(params)
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

                            App.database.getProductDao().delete();
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

package project.management_panel.product;

import android.content.Intent;
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
import project.product.Product;
import project.utils.Utils;
import project.view.BaseActivity;

public class PanelProductsListActivity extends BaseActivity {
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
    PanelProductListAdapter panelProductListAdapter;
    private boolean wating = false;
    List<Product> productList;
    List<Category> categoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_products_list);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();

        App.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getProductListFromNet();
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
        txtTitle.setText("Products");
    }

    private void initFilds() {
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
    }

    private void initRclv() {
        if (productList == null && categoryList == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rclv.setLayoutManager(linearLayoutManager);
        rclv.setHasFixedSize(true);
        rclv.setPullRefreshEnabled(false);
        rclv.setLoadingMoreEnabled(false);

        panelProductListAdapter = new PanelProductListAdapter(this, productList);
        rclv.setAdapter(panelProductListAdapter);
        rclv.refresh();
    }


    //------------------------- GET DATA FROM NET -----------------------
    private void getProductListFromNet() {
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

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_PANEL_PRODUCT))
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
                            btnNONet.setText("Retry");
                            txtNONetTitle.setText("Error in internet Connection!");
                            return;
                        }
                        Log.i("PANEL_PRODUCT", "result: " + result);
                        try {
                            JSONObject allProductJsonObject = new JSONObject(result);
                            if (allProductJsonObject.getBoolean("error")) {
                                Toast.makeText(PanelProductsListActivity.this, allProductJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONArray productsJa = allProductJsonObject.getJSONArray("products");
                            productList.clear();
                            for (int i = 0; i < productsJa.length(); i++) {
                                JSONObject pJo = productsJa.getJSONObject(i);
                                Product product = new Product();
                                product.setId(pJo.getInt("id"));
                                product.setTitle(pJo.getString("title"));
                                product.setDesc(pJo.getString("desc"));
                                product.setImg(pJo.getString("img"));
                                product.setPrice(pJo.getString("price"));
                                product.setCategoryId(pJo.getInt("categoryId"));
                                product.setSeen(pJo.getInt("seen"));

                                productList.add(product);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        initRclv();
                    }
                });
    }

    private void getCategoryListFromNet() {
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

        Ion.with(PanelProductsListActivity.this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_PANEL_CATEGORIES))
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

                        Log.i("PANEL_CATEGORY", "result: " + result);
                        try {
                            JSONObject allCategoryJsonObject = new JSONObject(result);

                            if (allCategoryJsonObject.getBoolean("error")) {
                                Toast.makeText(PanelProductsListActivity.this, allCategoryJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONArray categoriesJa = allCategoryJsonObject.getJSONArray("categories");
                            categoryList.clear();
                            for (int i = 0; i < categoriesJa.length(); i++) {
                                JSONObject cJo = categoriesJa.getJSONObject(i);
                                Category category = new Category();
                                category.setId(cJo.getInt("id"));
                                category.setTitle(cJo.getString("title"));
                                category.setImg(cJo.getString("img"));
                                category.setParentId(cJo.getInt("parentId"));

                                categoryList.add(category);
                            }
                        } catch (JSONException e2) {
                        }
                    }
                });
    }

    //------------------------- EVENTS -----------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

    @OnClick(R.id.btnAddProuduct)
    void btnAddProuductClicked(View v) {
        Intent intent = new Intent(PanelProductsListActivity.this, UpdateOrInsertProductActivity.class);
        this.startActivity(intent);

    }

    @OnClick(R.id.btnNONet)
    void btnNONetClicked(View v) {
        getProductListFromNet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPanelProductListEventListener(PanelProductListEventListener event) {
        Intent intent = new Intent(PanelProductsListActivity.this, UpdateOrInsertProductActivity.class);
        intent.putExtra("id", event.product.getId());
        intent.putExtra("title", event.product.getTitle());
        intent.putExtra("desc", event.product.getDesc());
        intent.putExtra("img", event.product.getImg());
        intent.putExtra("price", event.product.getPrice());
        intent.putExtra("categoryId", event.product.getCategoryId());
        intent.putExtra("seen", event.product.getSeen());

        this.startActivity(intent);

    }
}

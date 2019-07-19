package project.management_panel.category;

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
import project.category.Category;
import project.classes.App;
import project.classes.Consts;
import project.other.SearchActivity;
import project.utils.Utils;

public class PanelCategoryListActivity extends AppCompatActivity {

    //widgets
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.imgRight)
    ImageView imgRight;
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
    PanelCategoryListAdapter panelCategoryListAdapter;
    private boolean wating = false;
    List<Category> categoryList;


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
                getCategoryListFromNet();
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
        imgRight.setImageDrawable(getResources().getDrawable(R.drawable.search));
        txtTitle.setText("Categories");
    }

    private void initFilds() {
        categoryList = new ArrayList<>();
    }

    private void initRclv() {
        if (categoryList == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rclv.setLayoutManager(linearLayoutManager);
        rclv.setHasFixedSize(true);
        rclv.setPullRefreshEnabled(false);
        rclv.setLoadingMoreEnabled(false);

        panelCategoryListAdapter = new PanelCategoryListAdapter(this, categoryList);
        rclv.setAdapter(panelCategoryListAdapter);
        rclv.refresh();
    }


    //------------------------- GET DATA FROM NET -----------------------
    /*
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
*/
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

        Ion.with(PanelCategoryListActivity.this)
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
                                Toast.makeText(PanelCategoryListActivity.this, allCategoryJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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

                        initRclv();
                    }
                });
    }

    //------------------------- EVENTS -----------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }
    @OnClick(R.id.imgRight)
    void imgRightClicked(View v) {
        Intent intent=new Intent(this, SearchActivity.class);
        intent.putExtra("type","Category");
        this.startActivity(intent);
    }

    @OnClick(R.id.btnAddCategory)
    void btnAddCategoryClicked(View v) {
        Intent intent = new Intent(PanelCategoryListActivity.this, UpdateOrInsertCategoryActivity.class);
        this.startActivity(intent);

    }

    @OnClick(R.id.btnNONet)
    void btnNONetClicked(View v) {
        getCategoryListFromNet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPanelCategoryListEventListener(PanelCategoryListEventListener event) {
        Intent intent = new Intent(PanelCategoryListActivity.this, UpdateOrInsertCategoryActivity.class);
        intent.putExtra("id", event.getCategory().getId());
        intent.putExtra("parentId", event.getCategory().getParentId());
        intent.putExtra("title", event.getCategory().getTitle());
        intent.putExtra("img", event.getCategory().getImg());

        this.startActivity(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPanelCategoryListOnLongEventListener(PanelCategoryListOnLongEventListener event) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("delete user");
        builder.setMessage("Do you sure to delete this category?");

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
            params.put("categoryId", Collections.singletonList(String.valueOf(event.getCategory().getId())));

            Ion.with(PanelCategoryListActivity.this)
              .load(Utils.checkVersionAndBuildUrl(Consts.DELETE_CATEGORY))
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
                      Log.i("DELETECATEGORY", "result: " + result);
                      try {
                          JSONObject jsonObject = new JSONObject(result);
                          if (jsonObject.getBoolean("error")) {
                              Toast.makeText(PanelCategoryListActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                              return;
                          }

                          categoryList.remove(event.getCategory());
                          App.database.getCategorydao().delete(event.getCategory());
                          panelCategoryListAdapter.notifyDataSetChanged();

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
}

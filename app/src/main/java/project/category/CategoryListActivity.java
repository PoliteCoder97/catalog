package project.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.product.ProductListActivity;
import project.utils.Utils;

public class CategoryListActivity extends AppCompatActivity {

    //widjets
    @BindView(R.id.rclv)
    XRecyclerView rclv;
    @BindView(R.id.app_no_internet)
    LinearLayout app_no_internet;
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.txtNONetTitle)
    TextView txtNONetTitle;
    @BindView(R.id.btnNONet)
    Button btnNONet;
    @BindView(R.id.app_loading)
    LinearLayout app_loading;
    @BindView(R.id.app_empty_list)
    LinearLayout app_empty_list;

    //filds
    private boolean wating = false;
    private int parentId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        initFilds();
        initWidjets();

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    //----------------------------------- INITIALS ----------------------------------------
    private void initFilds() {
        parentId = getIntent().getIntExtra("parentId", -1);

    }

    private void initWidjets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Category");
    }

    private void initXRecyclerView() {

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rclv.setLayoutManager(manager);

        rclv.setPullRefreshEnabled(false);
        rclv.setLoadingMoreEnabled(false);

        CategoryListAdapter adapter = null;
        try {
            for (Category category : App.database.getCategorydao().getCategoryList(parentId)) {
                Log.i("CATEGORYLIST", "category id: " + category.getId());
            }

            adapter = new CategoryListAdapter(this, App.database.getCategorydao().getCategoryList(parentId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (adapter != null) {
            rclv.setAdapter(adapter);
            rclv.refresh();
        } else {
            Toast.makeText(this, "ایتمی وجود ندارد", Toast.LENGTH_SHORT).show();
        }

        //get list item count for showing empty list
        if (adapter.setOnListItemCountListener() == 0) {
            app_empty_list.setVisibility(View.VISIBLE);
            rclv.setVisibility(View.GONE);
        } else {
            app_empty_list.setVisibility(View.GONE);
            rclv.setVisibility(View.VISIBLE);
        }

    }

    //-------------------------------- GET DATA FROM NET --------------------------------
    private void getDataFromNet() {
        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.GONE);

        if (wating) {
            return;
        }
        wating = true;

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_CATEGORY))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        app_loading.setVisibility(View.GONE);
                        wating = false;
                        if (e != null) {
                            e.printStackTrace();
                            app_no_internet.setVisibility(View.VISIBLE);
                            txtNONetTitle.setText("Error in internet Connection!");
                            btnNONet.setText("Retry");

                            initXRecyclerView();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(CategoryListActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            App.database.getCategorydao().delete();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);
                                Category category = new Category();
                                category.setId(jo.getInt("id"));
                                category.setParentId(jo.getInt("parentId"));
                                category.setTitle(jo.getString("title"));
                                category.setImg(jo.getString("img"));

                                try {
                                    App.database.getCategorydao().insert(category);
                                } catch (Exception exp) {
                                    exp.printStackTrace();
                                }
                            }//end for
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        initXRecyclerView();
                    }
                });
    }

    //-------------------------------- EVENTS --------------------------------
    @OnClick(R.id.btnNONet)
    void btnNoNetClicked(View v) {
        getDataFromNet();
    }

    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCategoryEventListener(CategoryEventListener event) {/* Do something */
        if (App.database.getCategorydao().getCategoryList(event.getCategory().getId()).size() > 0) {
            Intent intent = new Intent(CategoryListActivity.this, CategoryListActivity.class);
            intent.putExtra("parentId", event.getCategory().getId());
            intent.putExtra("title", event.getCategory().getTitle());
            CategoryListActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
            intent.putExtra("categoryId", event.getCategory().getId());
            intent.putExtra("title", event.getCategory().getTitle());
            CategoryListActivity.this.startActivity(intent);
        }

    }


}

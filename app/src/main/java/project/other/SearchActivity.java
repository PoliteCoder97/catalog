package project.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amaloffice.catalog.R;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.category.Category;
import project.category.CategoryEventListener;
import project.category.CategoryListAdapter;
import project.classes.App;
import project.management_panel.category.UpdateOrInsertCategoryActivity;
import project.management_panel.product.PanelProductListEventListener;
import project.management_panel.product.UpdateOrInsertProductActivity;
import project.product.Product;
import project.product.ProductListAdapter;

public class SearchActivity extends BaseActivity {


  //--------------------- WIDGETS ----------------------------------------------
  @BindView(R.id.imgLeft)
  ImageView imgLeft;
  @BindView(R.id.txtTitle)
  TextView txtTitle;
  @BindView(R.id.imgRight)
  ImageView imgRight;
  @BindView(R.id.rclv)
  XRecyclerView rclv;
  @BindView(R.id.edtSearch)
  EditText edtSearch;

  //--------------------- FIELDS -----------------------------------------------
  private RecyclerView.Adapter adapter;
  private List list;
  private String type;
  //--------------------- MAIN -------------------------------------------------

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    ButterKnife.bind(this);

    getDataFromIntent();
    initFields();
    initWidgets();
    initRclv();

  }

  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  //--------------------- INITIALS -------------------------------------------------
  private void initFields() {
    switch (type) {
      case "Product":
        edtSearch.setHint("Shock wave");
        list = new ArrayList<Product>();
        creatAdapterDependOnType();
        break;
      case "Category":
        edtSearch.setHint("electrotherapy");
        list = new ArrayList<Category>();
        creatAdapterDependOnType();
        break;
    }
  }

  private void creatAdapterDependOnType() {
    switch (type) {
      case "Product":
        adapter = new ProductListAdapter(this, list, false, false);
        break;
      case "Category":
        adapter = new CategoryListAdapter(this, list);
        break;
    }

  }

  private void initWidgets() {
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
    txtTitle.setText("Search");
    imgRight.setImageDrawable(getResources().getDrawable(R.drawable.logo));
  }

  private void initRclv() {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    rclv.setLayoutManager(linearLayoutManager);
    rclv.setPullRefreshEnabled(false);
    rclv.setLoadingMoreEnabled(false);

    rclv.setAdapter(adapter);
    rclv.refresh();
  }

  private void fillRclv() {
    creatAdapterDependOnType();
    rclv.setAdapter(adapter);
  }

  //--------------------- GET DATA FROM INTENT -------------------------------------
  private void getDataFromIntent() {
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      type = bundle.getString("type");
    }

  }

  //--------------------- EVENTS ---------------------------------------------------

  @OnClick(R.id.imgLeft)
  void setImgLeftClicked(View v) {
    onBackPressed();
  }

  @OnClick(R.id.btnSearch)
  void btnSearchClicked(View v) {
    String key = edtSearch.getText().toString().trim();
    if (key == null || key.equals("")) {
      Toast.makeText(this, "fill the field pls", Toast.LENGTH_SHORT).show();
      return;
    }
    list.clear();

    switch (type) {
      case "Product":
        list = App.database.getProductDao().getSearchedProducts(key);
        break;
      case "Category":
        list = App.database.getCategorydao().getSearchedCategoryList(key);
        break;
    }
    fillRclv();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onPanelProductListEventListener(PanelProductListEventListener event) {
    Intent intent = new Intent(SearchActivity.this, UpdateOrInsertProductActivity.class);
    intent.putExtra("id", event.getProduct().getId());
    intent.putExtra("title", event.getProduct().getTitle());
    intent.putExtra("desc", event.getProduct().getDesc());
    intent.putExtra("img", event.getProduct().getImg());
    intent.putExtra("price", event.getProduct().getPrice());
    intent.putExtra("categoryId", event.getProduct().getCategoryId());
    intent.putExtra("seen", event.getProduct().getSeen());

    this.startActivity(intent);

  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onPanelCategoryListEventListener(CategoryEventListener event) {
    Intent intent = new Intent(SearchActivity.this, UpdateOrInsertCategoryActivity.class);
    intent.putExtra("id", event.getCategory().getId());
    intent.putExtra("parentId", event.getCategory().getParentId());
    intent.putExtra("title", event.getCategory().getTitle());
    intent.putExtra("img", event.getCategory().getImg());

    this.startActivity(intent);

  }
}

package project.management_panel.product;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.politecoder.catalog.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.category.Category;
import project.classes.App;
import project.product.Product;
import project.product.ProductListAdapter;
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

    //filds
    PanelProductListAdapter panelProductListAdapter;
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

    //------------------------- INITIALS -----------------------
    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Products");

        initProductRclv();
    }

    private void initFilds() {
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
    }

    private void initProductRclv() {
        if (productList == null && categoryList == null) {
            return;
        }


    }


    //------------------------- GET DATA FROM NET -----------------------
    private void getProductListFromNet() {

    }

    private void getCategoryListFromNet() {

    }

    //------------------------- EVENTS -----------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

    @OnClick(R.id.btnAddProuduct)
    void btnAddProuductClicked(View v) {
    }

    @OnClick(R.id.btnNONet)
    void btnNONetClicked(View v) {
        getProductListFromNet();
    }

}

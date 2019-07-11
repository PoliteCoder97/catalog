package project.management_panel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.amaloffice.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.management_panel.category.PanelCategoryListActivity;
import project.management_panel.person.PanelPersonListActivity;
import project.management_panel.product.PanelProductsListActivity;
import project.view.BaseActivity;

public class MainPanelActivity extends BaseActivity {

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
    private boolean wating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();

    }

    //------------------------ INITIALS ------------------------
    private void initFilds() {

    }

    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText("Panel");
    }

    //------------------------ EVENTS ---------------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

    @OnClick(R.id.btnProduct)
    void btnProductClicked(View v) {
        Intent intent = new Intent(MainPanelActivity.this, PanelProductsListActivity.class);
        this.startActivity(intent);
    }

    @OnClick(R.id.btnCategory)
    void btnCategoryClicked(View v) {
        Intent intent = new Intent(MainPanelActivity.this, PanelCategoryListActivity.class);
        this.startActivity(intent);
    }

    @OnClick(R.id.btnPerson)
    void btnPersonClicked(View v) {
        Intent intent = new Intent(MainPanelActivity.this, PanelPersonListActivity.class);
        this.startActivity(intent);
    }
}

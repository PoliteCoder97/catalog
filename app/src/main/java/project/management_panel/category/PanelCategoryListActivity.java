package project.management_panel.category;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.politecoder.catalog.R;

import butterknife.ButterKnife;

public class PanelCategoryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_category_list);
        ButterKnife.bind(this);



    }
}

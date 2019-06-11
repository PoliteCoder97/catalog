package project.management_panel.product;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabWidget;
import android.widget.Toast;

import com.glide.slider.library.svg.GlideApp;
import com.politecoder.catalog.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.category.Category;
import project.classes.Consts;
import project.product.Product;
import project.utils.Utils;

public class PanelProductListAdapter extends RecyclerView.Adapter<PanelProductListAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final List<Category> categoryList;


    public PanelProductListAdapter(Context context, List<Product> productList, List<Category> categoryList) {
        this.context = context;
        this.productList = productList;
        this.categoryList = categoryList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_panel_product, viewGroup, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Product product = productList.get(i);
//        Category category = categoryList.get(i);

        GlideApp.with(context)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PRODUCT + product.getImg()))
                .placeholder(context.getResources().getDrawable(R.drawable.logo))
                .into(viewHolder.img);

        viewHolder.rLayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo pick the image from gallery and show that to this imgView
            }
        });

        ArrayAdapter<Category> aa = new ArrayAdapter<Category>(context, android.R.layout.simple_spinner_item, categoryList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        viewHolder.spCategory.setAdapter(aa);

        viewHolder.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Category category = (Category) adapterView.getItemAtPosition(position);
                Toast.makeText(context, "category id: " + category.getId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Category category = (Category) adapterView.getItemAtPosition(0);
                Toast.makeText(context, "category id: " + category.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo update product
//                Toast.makeText(context, "category id: " + categoryId, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rLayImg)
        RelativeLayout rLayImg;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.spCategory)
        Spinner spCategory;
        @BindView(R.id.edtTitle)
        EditText edtTitle;
        @BindView(R.id.edtDesc)
        EditText edtDesc;
        @BindView(R.id.edtPrice)
        EditText edtPrice;
        @BindView(R.id.btnSubmit)
        Button btnSubmit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

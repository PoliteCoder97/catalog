package project.other;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amaloffice.catalog.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.category.Category;
import project.category.CategoryListActivity;
import project.classes.App;
import project.product.ProductListActivity;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder> {

  private final Context context;
  private final List<Category> categoryList;

  public MainCategoryAdapter(Context context, List<Category> categoryList) {
    this.context = context;
    this.categoryList = categoryList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_category,viewGroup,false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    Category category = categoryList.get(i);
    viewHolder.txtTitle.setText(""+category.getTitle());

    viewHolder.crd.setOnClickListener((View v)->{
      if (App.database.getCategorydao().getCategoryList(category.getId()).size() > 0) {
        Intent intent = new Intent(context, CategoryListActivity.class);
        intent.putExtra("parentId", category.getId());
        intent.putExtra("title", category.getTitle());
        context.startActivity(intent);
      } else {
        Intent intent = new Intent(context, ProductListActivity.class);
        intent.putExtra("categoryId", category.getId());
        intent.putExtra("title", category.getTitle());
        context.startActivity(intent);
      }
    });

  }

  @Override
  public int getItemCount() {
    return categoryList == null ? 0 : categoryList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.crd)
    CardView crd;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

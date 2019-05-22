package project.product;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.glide.slider.library.svg.GlideApp;
import com.politecoder.catalog.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.classes.Consts;
import project.utils.Utils;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

  private final List<Product> productList;
  private final Context context;

  public ProductListAdapter(Context context, List<Product> productList) {
    this.context = context;
    this.productList = productList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_rclv_product, viewGroup, false);
    WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    double width = windowManager.getDefaultDisplay().getWidth()/2.5;
    view.setLayoutParams(new RecyclerView.LayoutParams((int) width, RecyclerView.LayoutParams.WRAP_CONTENT));
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
    final Product product = productList.get(i);
    viewHolder.txtTitle.setText(" " + product.getTitle());

    GlideApp.with(context)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PRODUCT + product.getImg()))
      .placeholder(context.getResources().getDrawable(R.drawable.ic_store_white_24dp))
      .into(viewHolder.imgAdapter);
    viewHolder.lLayProduct.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new ProductEventListener(product));
      }
    });
  }

  @Override
  public int getItemCount() {
    if (productList == null)
      return 0;

    return productList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.lLayProduct)
    CardView lLayProduct;
    //    @BindView(R.id.lLayProduct)
//    LinearLayout lLayProduct;
    @BindView(R.id.imgAdapter)
    ImageView imgAdapter;
    @BindView(R.id.txtTitle)
    TextView txtTitle;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

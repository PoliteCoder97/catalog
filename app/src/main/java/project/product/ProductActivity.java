package project.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glide.slider.library.svg.GlideApp;
import com.politecoder.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.Consts;
import project.comment.CommentListActivity;
import project.utils.Utils;

public class ProductActivity extends AppCompatActivity {

  //widgets
  @BindView(R.id.txtTitle)
  TextView txtTitle;
  @BindView(R.id.imgLeft)
  ImageView imgLeft;
  @BindView(R.id.imgProduct)
  ImageView imgProduct;
  @BindView(R.id.txtPrice)
  TextView txtPrice;
  @BindView(R.id.txtDesc)
  TextView txtDesc;
  @BindView(R.id.btnCall)
  Button btnCall;

  //filds
  private int productId = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product);
    ButterKnife.bind(this);

    initFilds();
    initWidgets();


  }

  //------------------------------ INITIALS -------------------------------------
  private void initWidgets() {
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
    txtTitle.setText(" " + getIntent().getExtras().getString("title"));

    GlideApp.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PRODUCT + getIntent().getStringExtra("img")))
      .into(imgProduct);

    txtPrice.setText(" " + getIntent().getStringExtra("price"));
    txtDesc.setText(" " + getIntent().getStringExtra("desc"));

    productId = getIntent().getIntExtra("id", 0);

  }

  private void initFilds() {

  }


  //------------------------------ EVENTS -------------------------------------

  @OnClick(R.id.btnCall)
  void btnCallClicked(View v) {
    Utils.call(this,Consts.MARKETING_MANAGER);
  }

  @OnClick(R.id.imgLeft)
  void imgLeftClicked(View v) {
    this.onBackPressed();
  }

  @OnClick(R.id.llayComment)
  void llayCommentClicked(View v) {
    Intent intent = new Intent(ProductActivity.this, CommentListActivity.class);
    intent.putExtra("productId", productId);
    intent.putExtra("title", txtTitle.getText().toString().trim());
    this.startActivity(intent);
  }


}

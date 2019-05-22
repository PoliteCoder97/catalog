package project.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.politecoder.catalog.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.category.CategoryListActivity;
import project.classes.App;
import project.classes.Consts;
import project.classes.Slide;
import project.product.Product;
import project.product.ProductListAdapter;
import project.utils.Utils;

public class MainActivity extends AppCompatActivity {

  //widgets
  //toolbar
  @BindView(R.id.imgRight)
  ImageView imgRight;
  @BindView(R.id.txtTitle)
  TextView txtTitle;

  //main
  @BindView(R.id.llRowHolder)
  LinearLayout llRowHolder;
  @BindView(R.id.btnCategory)
  LinearLayout btnCategory;
  @BindView(R.id.btnContactUs)
  LinearLayout btnContactUs;
  @BindView(R.id.btnCertificate)
  LinearLayout btnCertificate;
  @BindView(R.id.btnLogIn)
  LinearLayout btnLogIn;
  @BindView(R.id.llayNewestGoods)
  LinearLayout llayNewestGoods;
  @BindView(R.id.llayMostVisits)
  LinearLayout llayMostVisits;
  @BindView(R.id.rclvMostVisited)
  RecyclerView rclvMostVisited;
  @BindView(R.id.rclvNewestGoods)
  RecyclerView rclvNewestGoods;
  //  @BindView(R.id.rclvMostVisited)
//  XRecyclerView rclvMostVisited;
//  @BindView(R.id.rclvNewestGoods)
//  XRecyclerView rclvNewestGoods;
  @BindView(R.id.app_loading)
  LinearLayout app_loading;


  //filds
  int displayWidth = 0;
  private boolean wating = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    initFilds();
    initWidgets();

    getDataFromNet();
  }


  //----------------------------------------- INITIALS --------------------------------------------
  private void initWidgets() {
    imgRight.setImageDrawable(getResources().getDrawable(R.drawable.menu));
    txtTitle.setText("Amal Office");

    setupSlider(llRowHolder);
  }

  private void initFilds() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    displayWidth = displayMetrics.widthPixels;
  }

  private void setupSlider(LinearLayout container) {
    SliderLayout sliderLayout = new SliderLayout(MainActivity.this);
//                    int height = (int) (joRow.getInt("height") * scale + 0.5f);
    sliderLayout.getPagerIndicator().setDefaultIndicatorColor(getResources().getColor(R.color.slider_indivator_current), getResources().getColor(R.color.slider_indivator_second));
    addView(sliderLayout, container, 0, displayWidth / 2);
    addView(new LinearLayout(MainActivity.this), container, 12, 5); //add for extra margin on button of slider
    fillSlider(sliderLayout, Utils.extractSlides("[]"));//TODO get slider data and fill it
  }

  private void fillSlider(SliderLayout sliderLayout, List<Slide> slides) {
    for (int i = 0; i < slides.size(); i++) {
      DefaultSliderView defaultSliderView = new DefaultSliderView(this);
      defaultSliderView
        .image("image url" + slides.get(i).getImageName())//TODO set image slider
        .setOnSliderClickListener(null);
      //add your extra information
      defaultSliderView.bundle(new Bundle());
      defaultSliderView.getBundle()
        .putString("action", slides.get(i).getAction());
      defaultSliderView.getBundle()
        .putString("actionData", slides.get(i).getActionData());
      sliderLayout.addSlider(defaultSliderView);
    }
  }

  private void addView(View view, ViewGroup rootView, int topMargin, int height) {
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
      height == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : height);

    params.topMargin = topMargin;

    //            inflatedView.setTag(i);
    //            inflatedView.setOnClickListener(oclSendType);
    //            TextView txtSubTitle = ((TextView) inflatedView.findViewById(R.id.tvSubTitle));
    //            txtSubTitle.setText(sendTypeList.get(i).subTitle);
    //            txtSubTitle.setTypeface(G.tf_fa_b);
    view.setLayoutParams(params);
    rootView.addView(view);
  }

  private void initialNewstGoodsRclv(List<Product> productList) {
    llayNewestGoods.setVisibility(View.VISIBLE);

    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rclvNewestGoods.setHasFixedSize(true);
    rclvNewestGoods.setLayoutManager(manager);
//    rclvNewestGoods.setPullRefreshEnabled(false);
//    rclvNewestGoods.setLoadingMoreEnabled(false);

    ProductListAdapter productListAdapter = null;

    if (productList == null || productList.size() == 0) {
      Date now = Calendar.getInstance().getTime();
//      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//      String formattedDate = df.format(d);
//      System.out.println("Current date => " + formattedDate);
      Calendar c = Calendar.getInstance();
      c.setTime(now);
      c.add(Calendar.DATE, -7);
      Date previousDate = c.getTime();
//      String formattedDateBefor = df.format(previousDate);
//      System.out.println("Current date => " + formattedDateBefor);
      productList = App.database.getProductDao().getNewestProducts(now, previousDate);//TODO check it
      Log.i("NewstTest", "NewstTest2: " + productList.size());

      productListAdapter = new ProductListAdapter(this, productList , true);
    }

    if (productList.size() == 0 || productList == null) {
      llayNewestGoods.setVisibility(View.GONE);
    }

    productListAdapter = new ProductListAdapter(this, productList,true);
    rclvNewestGoods.setAdapter(productListAdapter);
//    rclvNewestGoods.refresh();
  }

  private void initialMostSeenRclv(List<Product> productList) {
    llayMostVisits.setVisibility(View.VISIBLE);

    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rclvMostVisited.setHasFixedSize(true);
    rclvMostVisited.setLayoutManager(manager);
//    rclvMostVisited.setPullRefreshEnabled(false);
//    rclvMostVisited.setLoadingMoreEnabled(false);
    ProductListAdapter productListAdapter = null;
    if (productList == null || productList.size() == 0) {
      productList = App.database.getProductDao().getMostVisite();
      productListAdapter = new ProductListAdapter(this, productList , true);
    }
    if (productList.size() == 0 || productList == null) {
      llayMostVisits.setVisibility(View.GONE);
    }
    productListAdapter = new ProductListAdapter(this, productList,true);
    rclvMostVisited.setAdapter(productListAdapter);
//    rclvMostVisited.refresh();
  }

  //------------------------------------------- GET FROM NET -----------------------------------------------

  private void getDataFromNet() {
    app_loading.setVisibility(View.VISIBLE);

    if (wating) {
      return;
    }

    wating = true;

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_MAIN_INF))
      .setTimeout(Consts.DEFUALT_TIME_OUT)
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          app_loading.setVisibility(View.INVISIBLE);
          wating = false;

          if (e != null) {
            e.printStackTrace();
            initialNewstGoodsRclv(null);
            initialMostSeenRclv(null);
            return;
          }
          Log.i("MAIN", "result: " + result);
          try {
            JSONObject jsonObject = new JSONObject(result);

            JSONArray mostVisitedProductsJa = jsonObject.getJSONArray("mostVisitedProducts");
            JSONArray newestGoodsja = jsonObject.getJSONArray("newestGoods");

            List<Product> mostVisitedProducts = new ArrayList<>();
            List<Product> newestGoods = new ArrayList<>();


            for (int i = 0; i < newestGoodsja.length(); i++) {
              JSONObject jo = newestGoodsja.getJSONObject(i);
              Product product = new Product();

              product.setId(jo.getInt("id"));
              product.setCategoryId(jo.getInt("categoryId"));
              product.setTitle(jo.getString("title"));
              product.setDesc(jo.getString("desc"));
              product.setImg(jo.getString("img"));
              product.setPrice(jo.getString("price"));
              product.setSeen(jo.getInt("seen"));

              SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
              try {
                Date date = format.parse(jo.getString("postedDate"));
                product.setPosted_date(date);
              } catch (ParseException ext) {
                e.printStackTrace();
              }
              newestGoods.add(product);
            }

            for (int i = 0; i < mostVisitedProductsJa.length(); i++) {
              JSONObject jo = mostVisitedProductsJa.getJSONObject(i);
              Product product = new Product();

              product.setId(jo.getInt("id"));
              product.setCategoryId(jo.getInt("categoryId"));
              product.setTitle(jo.getString("title"));
              product.setDesc(jo.getString("desc"));
              product.setImg(jo.getString("img"));
              product.setPrice(jo.getString("price"));
              product.setSeen(jo.getInt("seen"));
              SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
              try {
                Date date = format.parse(jo.getString("postedDate"));
                product.setPosted_date(date);
              } catch (ParseException ext) {
                e.printStackTrace();
              }

              mostVisitedProducts.add(product);
            }
            initialNewstGoodsRclv(newestGoods);
            initialMostSeenRclv(mostVisitedProducts);
          } catch (JSONException e1) {
            e1.printStackTrace();
          }
        }
      });
  }

  //----------------------------------------- Event Listeners --------------------------------------------

  @OnClick(R.id.btnCategory)
  void btnCategoryClicked(View v) {
    Intent intent = new Intent(MainActivity.this, CategoryListActivity.class);
    this.startActivity(intent);
  }


}

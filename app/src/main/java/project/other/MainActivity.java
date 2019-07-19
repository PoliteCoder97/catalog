package project.other;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amaloffice.catalog.R;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.DefaultSliderView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.category.CategoryListActivity;
import project.certificate.CertificateActivity;
import project.classes.App;
import project.classes.Consts;
import project.classes.Slide;
import project.management_panel.MainPanelActivity;
import project.person.PersonListActivity;
import project.product.Product;
import project.product.ProductActivity;
import project.product.ProductEventListener;
import project.product.ProductListAdapter;
import project.register.SignInActivity;
import project.utils.Utils;

public class MainActivity extends BaseActivity {

  //widgets
  //toolbar
  @BindView(R.id.imgRight)
  ImageView imgRight;
  @BindView(R.id.imgLeft)
  ImageView imgLeft;
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
  @BindView(R.id.btnPanel)
  LinearLayout btnPanel;
  @BindView(R.id.lLayCategory)
  LinearLayout lLayCategory;
  @BindView(R.id.categoryRclv)
  RecyclerView categoryRclv;


  //filds
  int displayWidth = 0;
  private boolean wating = false;
  private boolean isShowShareButton = false;
  private boolean isShowPanelButton = false;
  private ArrayList<Uri> arrayListapkFilepath; // define global

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    initFilds();
    initWidgets();

    App.getHandler().postDelayed(new Runnable() {
      @Override
      public void run() {
        getDataFromNet();
      }
    }, 100);
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
    if (App.preferences.getBoolean(Consts.IS_SIGN_UP, false)) {
      btnLogIn.setVisibility(View.GONE);
      btnPanel.setVisibility(View.VISIBLE);
    }
    super.onResume();
  }

  //----------------------------------------- INITIALS --------------------------------------------
  private void initWidgets() {
    imgRight.setImageDrawable(getResources().getDrawable(R.drawable.menu));
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.logo));
    txtTitle.setText("Amal Office");

    if (App.preferences.getBoolean(Consts.IS_SIGN_UP, false)) {
      btnLogIn.setVisibility(View.VISIBLE);
      btnPanel.setVisibility(View.GONE);
    }
//        setupSlider(llRowHolder);

    categoryRclv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
    categoryRclv.hasFixedSize();
    MainCategoryAdapter mainCategoryAdapter = new MainCategoryAdapter(this, App.database.getCategorydao().getCategoryList(-1));
    categoryRclv.setAdapter(mainCategoryAdapter);
  }

  private void initFilds() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    displayWidth = displayMetrics.widthPixels;
  }

  private void initialNewstGoodsRclv(List<Product> productList) {
//        llayNewestGoods.setVisibility(View.VISIBLE);

    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rclvNewestGoods.setHasFixedSize(true);
    rclvNewestGoods.setLayoutManager(manager);


    if (productList == null) {
      Date now = Calendar.getInstance().getTime();
      Calendar c = Calendar.getInstance();
      c.setTime(now);
      c.add(Calendar.DATE, -7);
      Date previousDate = c.getTime();

      Log.i("DATE_TEST", "Now: " + now + "\npreviousDate: " + previousDate);
      productList = App.database.getProductDao().getNewestProducts(now, previousDate);//TODO check it

      llayNewestGoods.setVisibility(View.GONE);
      return;
    }

    ProductListAdapter productListAdapter = new ProductListAdapter(this, productList, true, true);
    rclvNewestGoods.setAdapter(productListAdapter);
  }

  private void initialMostSeenRclv(List<Product> productList) {
//        llayMostVisits.setVisibility(View.VISIBLE);

    LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rclvMostVisited.setHasFixedSize(true);
    rclvMostVisited.setLayoutManager(manager);
    if (productList == null) {
      productList = App.database.getProductDao().getMostVisite();
      llayMostVisits.setVisibility(View.GONE);
      return;
    }
    ProductListAdapter productListAdapter = new ProductListAdapter(this, productList, true, true);
    rclvMostVisited.setAdapter(productListAdapter);
  }

  //-------------- Slider ------------------------------------------------------------------------
  private void setupSlider(LinearLayout container, String data) {
    SliderLayout sliderLayout = new SliderLayout(MainActivity.this);
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    sliderLayout.setLayoutParams(layoutParams);

//                    int height = (int) (joRow.getInt("height") * scale + 0.5f);
    sliderLayout.getPagerIndicator().setDefaultIndicatorColor(getResources().getColor(R.color.slider_indivator_current), getResources().getColor(R.color.slider_indivator_second));
    addView(sliderLayout, container, 0, displayWidth / 2);
//        addView(new LinearLayout(MainActivity.this), container, 12, 5); //add for extra margin on button of slider
    fillSlider(sliderLayout, Utils.extractSlides(data));
//        fillSlider(sliderLayout, Utils.extractSlides(App.preferences.getString(Consts.SLIDER,"[]"));
  }

  private void fillSlider(SliderLayout sliderLayout, List<Slide> slides) {
    RequestOptions centerCrop = new RequestOptions().optionalCenterCrop();
    for (int i = 0; i < slides.size(); i++) {
      DefaultSliderView defaultSliderView = new DefaultSliderView(this);
      defaultSliderView
        .image(Utils.checkVersionAndBuildUrl(Consts.GET_SLIDER_IMAGE + slides.get(i).getImageName()))//TODO set image slider
        .setRequestOption(centerCrop)
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
  //------------------------------------------- GET FROM NET -------------------------------------

  private void getDataFromNet() {
    app_loading.setVisibility(View.VISIBLE);

    if (wating) {
      return;
    }

    wating = true;

    HashMap<String, List<String>> params = new HashMap<>();
    params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
    params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));
    params.put("personId", Collections.singletonList(String.valueOf(App.preferences.getInt(Consts.PERSON_ID, 0))));

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_MAIN_INF))
      .setTimeout(Consts.DEFUALT_TIME_OUT)
      .setBodyParameters(params)
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          app_loading.setVisibility(View.INVISIBLE);
          wating = false;

          if (e != null) {
            e.printStackTrace();
            setupSlider(llRowHolder, App.preferences.getString(Consts.SLIDER, "[]"));
            initialNewstGoodsRclv(null);
            initialMostSeenRclv(null);
            return;
          }
          Log.i("MAIN", "result: " + result);
          try {
            JSONObject jsonObject = new JSONObject(result);

            App.preferences.edit().putString("marketing_manager", jsonObject.getString("marketing_manager")).apply();
            App.preferences.edit().putString(Consts.REFRESH_TOKEN, jsonObject.getString("refresh_token")).apply();

            JSONArray mostVisitedProductsJa = jsonObject.getJSONArray("mostVisitedProducts");
            JSONArray newestGoodsja = jsonObject.getJSONArray("newestGoods");

            List<Product> mostVisitedProducts = new ArrayList<>();
            List<Product> newestGoods = new ArrayList<>();

            isShowShareButton = jsonObject.getBoolean("showShareButton");
            isShowPanelButton = jsonObject.getBoolean("showPanelButton");

            App.preferences.edit().putString(Consts.SLIDER, jsonObject.getJSONArray("slider").toString()).apply();
            setupSlider(llRowHolder, jsonObject.getJSONArray("slider").toString());

            if (!isShowPanelButton) {
              btnLogIn.setVisibility(View.GONE);
            } else {
              if (App.preferences.getBoolean(Consts.IS_SIGN_UP, false)) {
                btnPanel.setVisibility(View.VISIBLE);
                btnLogIn.setVisibility(View.GONE);
              } else {
                btnLogIn.setVisibility(View.VISIBLE);
                btnPanel.setVisibility(View.GONE);
              }
            }

            Log.i("MAINACTIVITY", "newestGoodsja.length(): " + newestGoodsja.length());

            if (newestGoodsja.length() != 0) {
              llayNewestGoods.setVisibility(View.VISIBLE);
            } else {
              llayNewestGoods.setVisibility(View.INVISIBLE);
            }
            if (mostVisitedProductsJa.length() != 0) {
              llayMostVisits.setVisibility(View.VISIBLE);
            } else {
              llayMostVisits.setVisibility(View.INVISIBLE);
            }


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

  private void logOut(LinearLayout llayLogout, AlertDialog dialog) {
    app_loading.setVisibility(View.VISIBLE);

    if (wating) {
      return;
    }

    wating = true;

    HashMap<String, List<String>> params = new HashMap<>();
    params.put("action", Collections.singletonList("log_out"));
    params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
    params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));
    params.put("personId", Collections.singletonList(String.valueOf(App.preferences.getInt(Consts.PERSON_ID, 0))));
    Log.i("LOG_OUT", "personId: " + App.preferences.getInt(Consts.PERSON_ID, 0));

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.USER_REGISTER))
      .setTimeout(Consts.DEFUALT_TIME_OUT)
      .setBodyParameters(params)
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          app_loading.setVisibility(View.INVISIBLE);
          wating = false;

          if (e != null) {
            e.printStackTrace();
            dialog.dismiss();
            return;
          }

          Log.i("LOG_OUT", "result: " + result);
          try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("error")) {
              Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              dialog.dismiss();
              return;
            }

            App.preferences.edit().clear().apply();
            btnLogIn.setVisibility(View.VISIBLE);
            btnPanel.setVisibility(View.GONE);
            llayLogout.setVisibility(View.GONE);
            dialog.dismiss();

          } catch (JSONException e1) {
            e1.printStackTrace();
          }


        }
      });
  }

  //----------------------------------------- Event Listeners ------------------------------------
  @OnClick(R.id.imgRight)
  void imgRightClicked(View v) {
    View menuDialog = LayoutInflater.from(this).inflate(R.layout.dialog_menu, null, false);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setView(menuDialog);
    AlertDialog dialog = builder.create();

    Button btnShareApplication = menuDialog.findViewById(R.id.btnShareApplication);
    LinearLayout llayShareApplication = menuDialog.findViewById(R.id.lLayoutShareApplication);
    Button btnAboutUs = menuDialog.findViewById(R.id.btnAboutUs);
    Button btnProgramer = menuDialog.findViewById(R.id.btnProgramer);
    Button btnLogOut = menuDialog.findViewById(R.id.btnLogOut);
    LinearLayout llayLogout = menuDialog.findViewById(R.id.llayLogout);

    if (isShowShareButton) {
      llayShareApplication.setVisibility(View.VISIBLE);
    } else {
      llayShareApplication.setVisibility(View.GONE);
    }

    if (App.preferences.getBoolean(Consts.IS_SIGN_UP, false)) {
      llayLogout.setVisibility(View.VISIBLE);
    } else {
      llayLogout.setVisibility(View.GONE);
    }
    btnShareApplication.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();

        try {
          //put this code when you wants to share apk
          arrayListapkFilepath = new ArrayList<>();
          shareAPK(getPackageName());
          // you can pass bundle id of installed app in your device instead of getPackageName()
          Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
          intent.setType("application/vnd.android.package-archive");
          intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
            arrayListapkFilepath);
          startActivity(Intent.createChooser(intent, "Share " +
            arrayListapkFilepath.size() + " Files Via"));
        } catch (Exception e) {
          Toast.makeText(MainActivity.this, "sorry! can't send installer file", Toast.LENGTH_SHORT).show();
        }

      }
    });
    btnAboutUs.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        Toast.makeText(MainActivity.this, "llayAboutUs", Toast.LENGTH_SHORT).show();
      }
    });
    btnProgramer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
        Toast.makeText(MainActivity.this, "llayProgramer", Toast.LENGTH_SHORT).show();
      }
    });

    btnLogOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        logOut(llayLogout, dialog);
      }
    });

    dialog.show();
    dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
  }

  public void shareAPK(String bundle_id) {
    File f1;
    File f2 = null;

    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
    for (Object object : pkgAppsList) {

      ResolveInfo info = (ResolveInfo) object;
      if (info.activityInfo.packageName.equals(bundle_id)) {

        f1 = new File(info.activityInfo.applicationInfo.publicSourceDir);

        Log.v("file--",
          " " + f1.getName() + "----" + info.loadLabel(getPackageManager()));
        try {

          String file_name = info.loadLabel(getPackageManager()).toString();
          Log.d("file_name--", " " + file_name);

          f2 = new File(Environment.getExternalStorageDirectory().toString() + "/Folder");
          f2.mkdirs();
          f2 = new File(f2.getPath() + "/" + file_name + ".apk");
          f2.createNewFile();

          InputStream in = new FileInputStream(f1);

          OutputStream out = new FileOutputStream(f2);

          // byte[] buf = new byte[1024];
          byte[] buf = new byte[4096];
          int len;
          while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
          }
          in.close();
          out.close();
          System.out.println("File copied.");
        } catch (FileNotFoundException ex) {
          System.out.println(ex.getMessage() + " in the specified directory.");
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }
    }

    arrayListapkFilepath.add(Uri.fromFile(new File(f2.getAbsolutePath())));

  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onCategoryEventListener(ProductEventListener event) {/* Do something */

    Intent intent = new Intent(MainActivity.this, ProductActivity.class);
    intent.putExtra("id", event.getProduct().getId());
    intent.putExtra("title", event.getProduct().getTitle());
    intent.putExtra("categoryId", event.getProduct().getCategoryId());
    intent.putExtra("desc", event.getProduct().getDesc());
    intent.putExtra("img", event.getProduct().getImg());
    intent.putExtra("price", event.getProduct().getPrice());
    intent.putExtra("posted_date", event.getProduct().getPosted_date());

    MainActivity.this.startActivity(intent);
  }

  @OnClick(R.id.btnCategory)
  void btnCategoryClicked(View v) {
    Intent intent = new Intent(MainActivity.this, CategoryListActivity.class);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      this.startActivity(intent,
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    } else {
      this.startActivity(intent);
    }
  }

  @OnClick(R.id.btnContactUs)
  void btnContactUsClicked(View v) {
    Intent intent = new Intent(MainActivity.this, PersonListActivity.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      this.startActivity(intent,
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    } else {
      this.startActivity(intent);
    }

  }

  @OnClick(R.id.btnLogIn)
  void btnLogInClicked(View v) {
    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      this.startActivity(intent,
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    } else {
      this.startActivity(intent);
    }

  }

  @OnClick(R.id.btnPanel)
  void btnPanelClicked(View v) {
    app_loading.setVisibility(View.VISIBLE);

    if (wating) {
      return;
    }

    wating = true;

    HashMap<String, List<String>> params = new HashMap<>();
    params.put("action", Collections.singletonList("check_user"));
    params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
    params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));
    params.put("personId", Collections.singletonList(String.valueOf(App.preferences.getInt(Consts.PERSON_ID, 0))));

    Ion.with(this)
      .load(Utils.checkVersionAndBuildUrl(Consts.USER_REGISTER))
      .setTimeout(Consts.DEFUALT_TIME_OUT)
      .setBodyParameters(params)
      .asString()
      .setCallback(new FutureCallback<String>() {
        @Override
        public void onCompleted(Exception e, String result) {
          app_loading.setVisibility(View.INVISIBLE);
          wating = false;

          if (e != null) {
            e.printStackTrace();
            return;
          }

          Log.i("PANEL", "result: " + result);

          try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getBoolean("error")) {
              Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
              App.preferences.edit().clear().apply();
              btnPanel.setVisibility(View.GONE);
              btnLogIn.setVisibility(View.VISIBLE);
              initWidgets();
              return;
            }

            if (jsonObject.getBoolean("isChecked")) {
              Intent intent = new Intent(MainActivity.this, MainPanelActivity.class);
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                MainActivity.this.startActivity(intent,
                  ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
              } else {
                MainActivity.this.startActivity(intent);
              }
            }

          } catch (JSONException e1) {
            e1.printStackTrace();
          }


        }
      });
  }

  @OnClick(R.id.btnCertificate)
  void btnCertificateClicked(View v) {
    Intent intent = new Intent(MainActivity.this, CertificateActivity.class);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
      this.startActivity(intent,
        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    } else {
      this.startActivity(intent);
    }

  }
}

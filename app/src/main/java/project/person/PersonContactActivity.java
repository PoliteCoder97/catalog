package project.person;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.glide.slider.library.svg.GlideApp;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.politecoder.catalog.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.App;
import project.classes.Consts;
import project.contact.Contact;
import project.utils.Utils;

public class PersonContactActivity extends AppCompatActivity {

    //widgets
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.app_loading)
    LinearLayout app_loading;
    @BindView(R.id.app_no_internet)
    LinearLayout app_no_internet;
    @BindView(R.id.btnNONet)
    Button btnNONet;
    @BindView(R.id.txtNONetTitle)
    TextView txtNONetTitle;
    @BindView(R.id.lLayPhone)
    LinearLayout lLayPhone;
    @BindView(R.id.lLayTelegram)
    LinearLayout lLayTelegram;
    @BindView(R.id.lLayWhatsApp)
    LinearLayout lLayWhatsApp;
    @BindView(R.id.lLayFacebook)
    LinearLayout lLayFacebook;
    @BindView(R.id.lLayEmail)
    LinearLayout lLayEmail;
    @BindView(R.id.lLayWeb)
    LinearLayout lLayWeb;
    @BindView(R.id.txtPhone)
    TextView txtPhone;
    @BindView(R.id.txtTelegram)
    TextView txtTelegram;
    @BindView(R.id.txtWhatsapp)
    TextView txtWhatsapp;
    @BindView(R.id.txtFacebook)
    TextView txtFacebook;
    @BindView(R.id.txtEmail)
    TextView txtEmail;
    @BindView(R.id.txtWeb)
    TextView txtWeb;
    @BindView(R.id.sv)
    ScrollView sv;
    @BindView(R.id.imgPerson)
    ImageView imgPerson;
    @BindView(R.id.txtDesc)
    TextView txtDesc;


    //filds
    private Person person = new Person();
    private PersonContact personContact;
    private boolean wating = false;
    private int heightScrooled = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_contact);
        ButterKnife.bind(this);

        initFilds();
        initWidgets(null);


        App.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataFromNet();
            }
        }, 100);

    }

    //------------------------------- INITIALS --------------------------
    private void initFilds() {
        getIntentExtras();
    }

    @SuppressLint("NewApi")
    private void initWidgets(Contact contact) {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText(" " + person.getName());
        txtDesc.setText(" " + person.getDesc());

        GlideApp.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PERSON + person.getImg()))
                .placeholder(this.getResources().getDrawable(R.drawable.logo))
                .into(imgPerson);

        if (contact == null) {
            contact = App.database.getContactDao().getContact(person.getId());
        }

        sv.setVisibility(View.INVISIBLE);
        if (contact != null) {
            sv.setVisibility(View.VISIBLE);
            if (!contact.getPhoneNumber().equals("-")) {
                lLayPhone.setVisibility(View.VISIBLE);
                Contact finalContact = contact;
                lLayPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.call(PersonContactActivity.this, finalContact.getPhoneNumber());
                    }
                });
                txtPhone.setText(" " + contact.getPhoneNumber());
            } else {
                lLayPhone.setVisibility(View.GONE);
            }
            if (!contact.getTelegram().equals("-")) {
                lLayTelegram.setVisibility(View.VISIBLE);
                Contact finalContact = contact;
                lLayTelegram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.openTelegram(PersonContactActivity.this, finalContact.getTelegram());
                    }
                });
                txtTelegram.setText(" " + contact.getTelegram());
            } else {
                lLayTelegram.setVisibility(View.GONE);
            }
            if (!contact.getWhatsApp().equals("-")) {
                lLayWhatsApp.setVisibility(View.VISIBLE);
                Contact finalContact = contact;
                lLayWhatsApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.openWhatsapp(PersonContactActivity.this, finalContact.getWhatsApp());
                    }
                });
                txtWhatsapp.setText(" " + contact.getWhatsApp());
            } else {
                lLayWhatsApp.setVisibility(View.GONE);
            }
            if (!contact.getFacebook().equals("-")) {
                lLayFacebook.setVisibility(View.VISIBLE);
                //TODO set email click listener
                txtFacebook.setText(" " + contact.getFacebook());
            } else {
                lLayFacebook.setVisibility(View.GONE);
            }
            if (!contact.getEmail().equals("-")) {
                lLayEmail.setVisibility(View.VISIBLE);
                Contact finalContact = contact;
                lLayEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.openEmail(PersonContactActivity.this, finalContact.getEmail());
                    }
                });
                txtEmail.setText(" " + contact.getEmail());
            } else {
                lLayEmail.setVisibility(View.GONE);
            }
            if (!contact.getWeb().equals("-")) {
                lLayWeb.setVisibility(View.VISIBLE);
                Contact finalContact = contact;
                lLayWeb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.openWeb(PersonContactActivity.this, finalContact.getWeb());
                    }
                });
                txtWeb.setText(" " + contact.getWeb());
            } else {
                lLayWeb.setVisibility(View.GONE);
            }


        }

        sv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                heightScrooled += scrollY;
                if (heightScrooled > 30) {
                    app_no_internet.animate().translationY(app_no_internet.getHeight() + 10);
                } else {
                    app_no_internet.animate().translationY(0);
                }
            }
        });
    }

    private void getIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            person.setId(getIntent().getIntExtra("id", 0));
            person.setName(getIntent().getStringExtra("name"));
            person.setImg(getIntent().getStringExtra("img"));
            person.setDesc(getIntent().getStringExtra("desc"));
            person.setIsMentor(getIntent().getIntExtra("isMentor", 0));
        }
        Log.i("PERSON", "personId: " + person.getId());
    }

    //------------------------------- GET DATA FROM Net --------------------------
    private void getDataFromNet() {
        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.GONE);
        if (wating) {
            return;
        }
        wating = true;

        HashMap<String, List<String>> params = new HashMap<>();
        params.put("token", Collections.singletonList(App.preferences.getString(Consts.TOKEN, "")));
        params.put("refresh_token", Collections.singletonList(App.preferences.getString(Consts.REFRESH_TOKEN, "")));
        params.put("personId", Collections.singletonList(String.valueOf(person.getId())));

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_CONTACT))
                .setBodyParameters(params)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        app_loading.setVisibility(View.GONE);

                        if (e != null) {
                            app_no_internet.setVisibility(View.VISIBLE);
                            txtTitle.setText(getResources().getString(R.string.no_net_error));
                            btnNONet.setText("Retry");
                            initWidgets(null);
                            return;
                        }


                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            Log.i("PERSON", "result: " + jsonObject.toString(4));
                            if (jsonObject.getBoolean("error")) {
                                Toast.makeText(PersonContactActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            App.database.getContactDao().delete();
                            JSONObject data = jsonObject.getJSONObject("data");

                            Contact contact = new Contact();
                            contact.setId(data.getInt("id"));
                            contact.setPersonId(data.getInt("personId"));
                            contact.setPhoneNumber(data.getString("phoneNumber"));
                            contact.setTelegram(data.getString("telegram"));
                            contact.setWhatsApp(data.getString("whatsApp"));
                            contact.setFacebook(data.getString("facebook"));
                            contact.setEmail(data.getString("email"));
                            contact.setWeb(data.getString("web"));

                            App.database.getContactDao().insert(contact);

                            initWidgets(contact);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    //------------------------------- EVENTS --------------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

    @OnClick(R.id.btnNONet)
    void btnNONetClicked(View v) {
        getDataFromNet();
    }
}

package project.person;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.politecoder.catalog.R;

import org.json.JSONException;
import org.json.JSONObject;

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


    //filds
    private Person person;
    private PersonContact personContact;
    private boolean wating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_contact);
        ButterKnife.bind(this);

        initFilds();
        initWidgets(null);
        getDataFromNet();
    }

    //------------------------------- INITIALS --------------------------
    private void initFilds() {
        getIntentExtras();
    }

    private void initWidgets(Contact contact) {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText(" " + person.getName());

        if (contact == null) {
            contact = App.database.getContactDao().getContact(person.getId());
        }

    }

    private void getIntentExtras() {
        person = new Person();
        person.setId(getIntent().getIntExtra("id", 0));
        person.setName(getIntent().getStringExtra("name"));
        person.setImg(getIntent().getStringExtra("img"));
        person.setDesc(getIntent().getStringExtra("desc"));
        person.setIsMentor(getIntent().getIntExtra("isMentor", 0));
    }

    //------------------------------- GET DATA FROM Net --------------------------
    private void getDataFromNet() {

//    List<PersonContact> personContactList = new ArrayList<>();
//
//    if (person != null)
//      personContactList = App.database.getPersonContactdao().getPersonContacts(person.getId());
//
//    if (personContactList != null)
//      personContact = personContactList.get(0);

        app_loading.setVisibility(View.VISIBLE);
        app_no_internet.setVisibility(View.GONE);
        if (wating) {
            return;
        }
        wating = true;

        int id = 0;
        if (person != null)
            id = person.getId();

        Ion.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_CONTACT))
                .setBodyParameter("personId", String.valueOf(id))
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

                        Log.i("RESULT", "result: " + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
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

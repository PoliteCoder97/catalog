package project.person;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.politecoder.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.Consts;
import project.utils.Utils;

public class PersonContactActivity extends AppCompatActivity {

  //widgets
  @BindView(R.id.imgLeft)
  ImageView imgLeft;
  @BindView(R.id.txtTitle)
  TextView txtTitle;


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
    initWidgets();
    getDataFromNet();
  }

  //------------------------------- INITIALS --------------------------
  private void initFilds() {
    getIntentExtras();
  }

  private void initWidgets() {
    imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
    txtTitle.setText(" " + person.getName());
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


    if (wating)
      return;

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
          if (e != null) {
            //TODO show error
            initWidgets();
            return;
          }

          Log.i("RESULT", "result: " + result);

        }
      });
  }

  //------------------------------- EVENTS --------------------------
  @OnClick(R.id.imgLeft)
  void imgLeftClicked(View v) {
    this.onBackPressed();
  }


}

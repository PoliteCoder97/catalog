package project.certificate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.glide.slider.library.svg.GlideApp;
import com.politecoder.catalog.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import project.classes.Consts;
import project.utils.Utils;
import project.view.BaseActivity;

public class ShowCertificateActivity extends BaseActivity {

    //widgets
    @BindView(R.id.imageView)
    PhotoView imageView;
    @BindView(R.id.imgLeft)
    ImageView imgLeft;
    @BindView(R.id.txtTitle)
    TextView txtTitle;


    //filds
    private String title = " ";
    private String img = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_certificate);
        ButterKnife.bind(this);

        initFilds();
        initWidgets();


    }

    //-------------------- INITIALS ---------------------------


    private void initFilds() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            img = bundle.getString("img");
        }
    }

    private void initWidgets() {
        imgLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back));
        txtTitle.setText(" " + title);

        GlideApp.with(this)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_CERTIFICATE) + img)
                .placeholder(getResources().getDrawable(R.drawable.logo))
                .into(imageView);
    }

    //-------------------- Events ---------------------------
    @OnClick(R.id.imgLeft)
    void imgLeftClicked(View v) {
        this.onBackPressed();
    }

}

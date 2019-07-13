package project.management_panel.person;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amaloffice.catalog.R;
import com.glide.slider.library.svg.GlideApp;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.classes.Consts;
import project.person.Person;
import project.utils.Utils;

public class PanelPersonListAdapter extends RecyclerView.Adapter<PanelPersonListAdapter.ViewHolder> {

  private final Context context;
  private final List<Person> personList;


  public PanelPersonListAdapter(Context context, List<Person> personList) {
    this.context = context;
    this.personList = personList;
  }

  @NonNull
  @Override
  public PanelPersonListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_panel, viewGroup, false);
    return new PanelPersonListAdapter.ViewHolder(view);
  }


  @Override
  public void onBindViewHolder(@NonNull PanelPersonListAdapter.ViewHolder viewHolder, int i) {
    Person person = personList.get(i);

    viewHolder.txtTitle.setText(" " + person.getName());
    GlideApp.with(context)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PERSON + person.getImg()))
      .placeholder(context.getResources().getDrawable(R.drawable.logo))
      .into(viewHolder.img);

    viewHolder.lLayPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new PanelPersonListEventListener(person));
      }
    });

    viewHolder.lLayPanel.setOnLongClickListener(view -> {
      EventBus.getDefault().post(new PanelPersonListLongEventListener(person));
      return false;
    });


  }

  @Override
  public int getItemCount() {
    return personList == null ? 0 : personList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.lLayPanel)
    LinearLayout lLayPanel;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.txtTitle)
    TextView txtTitle;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

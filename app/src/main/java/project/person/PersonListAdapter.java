package project.person;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder> {
  private final List<Person> personList;
  private final Context context;

  public PersonListAdapter(Context context, List<Person> personList) {
    this.context = context;
    this.personList = personList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_rclv_person, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
    Person person = personList.get(position);
    viewHolder.textView.setText(" " + person.getName());

    GlideApp.with(context)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_PERSON + person.getImg()))
      .placeholder(context.getResources().getDrawable(R.drawable.logo))
      .into(viewHolder.imageView);
    viewHolder.llayPerson.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new PersonEventListener(person));
      }
    });
  }

  @Override
  public int getItemCount() {
    return personList == null ? 0 : personList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.llayPerson)
    CardView llayPerson;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

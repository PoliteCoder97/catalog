package project.management_panel.slider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amaloffice.catalog.R;
import com.glide.slider.library.svg.GlideApp;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.classes.Consts;
import project.classes.Slide;
import project.utils.Utils;

public class PanelSliderListAdapter extends RecyclerView.Adapter<PanelSliderListAdapter.ViewHolder> {

  private final Context context;
  private final List<Slide> slideList;


  public PanelSliderListAdapter(Context context, List<Slide> slideList) {
    this.context = context;
    this.slideList = slideList;
  }

  @NonNull
  @Override
  public PanelSliderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_panel_slider, viewGroup, false);
    return new PanelSliderListAdapter.ViewHolder(view);
  }


  @Override
  public void onBindViewHolder(@NonNull PanelSliderListAdapter.ViewHolder viewHolder, int i) {
    Slide slide = slideList.get(i);

    GlideApp.with(context)
      .load(Utils.checkVersionAndBuildUrl(Consts.GET_SLIDER_IMAGE + slide.getImageName()))
      .placeholder(context.getResources().getDrawable(R.drawable.logo))
      .into(viewHolder.img);

    viewHolder.lLayPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        EventBus.getDefault().post(new PanelSliderListEventListener(slide));
      }
    });

    viewHolder.lLayPanel.setOnLongClickListener(view -> {
      EventBus.getDefault().post(new PanelSliderListLongEventListener(slide));
      return false;
    });


  }

  @Override
  public int getItemCount() {
    return slideList == null ? 0 : slideList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.lLayPanel)
    CardView lLayPanel;
    @BindView(R.id.img)
    ImageView img;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

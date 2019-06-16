package project.management_panel.category;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glide.slider.library.svg.GlideApp;
import com.politecoder.catalog.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import project.category.Category;
import project.classes.Consts;
import project.utils.Utils;

public class PanelCategoryListAdapter extends RecyclerView.Adapter<PanelCategoryListAdapter.ViewHolder> {

    private final Context context;
    private final List<Category> categoryList;


    public PanelCategoryListAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_panel, viewGroup, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Category category = categoryList.get(i);

        viewHolder.txtTitle.setText(" "+category.getTitle());
        GlideApp.with(context)
                .load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_CATEGORY + category.getImg()))
                .placeholder(context.getResources().getDrawable(R.drawable.logo))
                .into(viewHolder.img);

        viewHolder.lLayPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PanelCategoryListEventListener(category));
            }
        });



//        ArrayAdapter<Category> aa = new ArrayAdapter<Category>(context, android.R.layout.simple_spinner_item, categoryList);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//
//        viewHolder.spCategory.setAdapter(aa);
//
//        viewHolder.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//                Category category = (Category) adapterView.getItemAtPosition(position);
//                Toast.makeText(context, "category id: " + category.getId(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Category category = (Category) adapterView.getItemAtPosition(0);
//                Toast.makeText(context, "category id: " + category.getId(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        viewHolder.btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
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

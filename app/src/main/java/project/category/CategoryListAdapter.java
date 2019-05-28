package project.category;

import android.content.Context;
import android.support.annotation.NonNull;
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
import project.classes.EmptyListCallback;
import project.utils.Utils;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> implements EmptyListCallback {
    private final Context context;
    private final List<Category> categoryList;

    public CategoryListAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_list, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Category category = categoryList.get(i);

        viewHolder.txtTitle.setText(" " + category.getTitle());
        GlideApp.with(context).load(Utils.checkVersionAndBuildUrl(Consts.GET_IMAGE_CATEGORY + category.getImg())).into(viewHolder.imgAdapter);

        viewHolder.imgAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CategoryEventListener(category));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (categoryList == null)
            return 0;

        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgAdapter)
        ImageView imgAdapter;
        @BindView(R.id.txtTitle)
        TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public boolean setOnListEmptyListener() {
        return getItemCount() == 0 ;
    }
}

package project.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.politecoder.catalog.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {


  private List<Comment> commentList;
  private Context context;

  public CommentListAdapter(Context context, List<Comment> commentList) {
    this.context = context;
    this.commentList = commentList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_comment_list, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Comment comment = commentList.get(position);
    if (comment == null) {
      Toast.makeText(context, "We havent any Comment !", Toast.LENGTH_SHORT).show();
      return;
    }

    holder.txtId.setText(" "+comment.getId()+":");
    holder.txtComment.setText(""+comment.getComment());

  }

  @Override
  public int getItemCount() {
    return commentList == null ? 0 : commentList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.txtComment)
    TextView txtComment;
    @BindView(R.id.txtId)
    TextView txtId;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}

package com.appsinventiv.noorenikahadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.noorenikahadmin.Models.PostModel;
import com.appsinventiv.noorenikahadmin.R;
import com.appsinventiv.noorenikahadmin.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    Context context;
    List<PostModel> itemList;
    PostsAdapterCallbacks callbacks;

    public PostsAdapter(Context context, List<PostModel> itemList, PostsAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }

    public void setItemList(List<PostModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostModel postModel = itemList.get(position);

        if (postModel.getType().equalsIgnoreCase("text")) {
            holder.postImage.setVisibility(View.GONE);
        } else if (postModel.getType().equalsIgnoreCase("image")) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(postModel.getImageUrl()).into(holder.postImage);

        }
        Glide.with(context).load(postModel.getUserPicUrl()).into(holder.userImage);
        holder.name.setText(postModel.getUserName());
        holder.text.setText(postModel.getText());
        holder.commentCount.setText(postModel.getCommentCount() + " comments");
        holder.likesCount.setText(postModel.getLikeCount() + " likes");

        holder.time.setText(CommonUtils.getFormattedDate(postModel.getTime()));
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onRemove(postModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, text, commentCount, likesCount,time;
        ImageView postImage, remove, comment, share;
        CircleImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            name = itemView.findViewById(R.id.name);
            postImage = itemView.findViewById(R.id.postImage);

            remove = itemView.findViewById(R.id.remove);
            likesCount = itemView.findViewById(R.id.likesCount);
            time = itemView.findViewById(R.id.time);
            commentCount = itemView.findViewById(R.id.commentCount);
            userImage = itemView.findViewById(R.id.userImage);

        }
    }
    public interface PostsAdapterCallbacks{
        public void onRemove(PostModel model);

    }

}

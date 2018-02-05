package com.jack.buy4u;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Jack_Wang on 2018/1/14.
 */
//繼承 順序 2
public class ItemRecycleAdapter extends RecyclerView.Adapter<ItemRecycleAdapter.ViewHolder>{


    private final List<Item> items;

    public ItemRecycleAdapter(List<Item> items) {
        this.items = items;
    }

    //最後生成 三項方法 3
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Item item = items.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(item.getPhotoPath());
        holder.imageViewPhoto.setImageBitmap(bitmap);
        Glide.with(holder.itemView.getContext())
                .load(item.getPhotoPath())
                .override(300, 250)
                .into(holder.imageViewPhoto);
        holder.textViewName.setText(item.getName());
        holder.textViewPrice.setText(item.getPrice()+"");
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }else {
            return items.size();
        }
    }


    //寫的順序 1
    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewPhoto;
        TextView textViewName , textViewPrice;
        public ViewHolder(View itemView) {
            super(itemView);

            imageViewPhoto = itemView.findViewById(R.id.row_photo);
            textViewName = itemView.findViewById(R.id.row_name);
            textViewPrice = itemView.findViewById(R.id.row_price);

        }
    }
}

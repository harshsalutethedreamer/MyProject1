package org.example.android.numero;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by harshgupta on 10/09/16.
 */
public class NumeroRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
    protected TextView nname,ncount,ndescription,ndate,nname2;
    protected ImageView photogallery,star,choice;
    protected LinearLayout ncategorybox;
    private ClickListener clickListener;


    public interface Ondeletelistener{
        void onDeletelistener(int _id,String name);
    }

    public NumeroRecyclerViewHolder(View view){
        super(view);
        this.nname=(TextView) view.findViewById(R.id.first_alphabet_category);
        this.nname2=(TextView) view.findViewById(R.id.remaining_text_category);
        this.ndate=(TextView) view.findViewById(R.id.ncreatedate);
        this.ncount=(TextView) view.findViewById(R.id.display_count);
        this.star = (ImageView) view.findViewById(R.id.favourite_button);
        this.photogallery = (ImageView) view.findViewById(R.id.play_button);
        this.ncategorybox = (LinearLayout) view.findViewById(R.id.categorybox);
        this.choice = (ImageView) view.findViewById(R.id.more_options);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    public interface ClickListener {
        public void onClick(View v, int position, boolean isLongCLick);
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View view) {
        clickListener.onClick(view,getAdapterPosition(),true);
        return false;
    }
}

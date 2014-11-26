package com.example.jgill.myapplication;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.res.Resources;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by JGill on 21/11/14.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Country> countryArrayList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View cardView ;
        public TextView countryNameColumn1;
        public TextView countryNameColumn2;
        public TextView countryNameColumn3;
        public ImageView countryImageColumn1;
        public ImageView countryImageColumn2;
        public ImageView countryImageColumn3;
        public ViewHolder(View v) {
            super(v);
            cardView = v;
            this.countryNameColumn1 = (TextView) v.findViewById(R.id.countryNameColumn1);
            this.countryNameColumn2 = (TextView) v.findViewById(R.id.countryNameColumn2);
            this.countryNameColumn3 = (TextView) v.findViewById(R.id.countryNameColumn3);
            this.countryImageColumn1 = (ImageView) v.findViewById(R.id.countryImageColumn1);
            this.countryImageColumn2 = (ImageView) v.findViewById(R.id.countryImageColumn2);
            this.countryImageColumn3 = (ImageView) v.findViewById(R.id.countryImageColumn3);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Country> countryArrayList) {
        this.countryArrayList = countryArrayList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
       // ...

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void setCardVisibility(ViewHolder viewHolder, int cardViewId, Boolean selector) {
        if(selector) {
            viewHolder.cardView.findViewById(cardViewId).setVisibility(View.VISIBLE);
        } else {
            viewHolder.cardView.findViewById(cardViewId).setVisibility(View.GONE);

        }
    }

    private void setCard(ViewHolder viewHolder, int textId, int imageId, String countryName) {
        TextView text = (TextView) viewHolder.cardView.findViewById(textId);
        ImageView image = (ImageView) viewHolder.cardView.findViewById(imageId);
        text.setText(countryName);
        //used for using variable with R.drawable.()
        Context context = viewHolder.cardView.getContext();
        image.setImageResource(nameConverter(countryName, context));
    }

    private int nameConverter (String countryName, Context context) {
        int intId = 0;

        String editedCountryName = countryName.replaceAll(" ","_").toLowerCase();
        System.out.println(editedCountryName);
        intId = context.getResources().getIdentifier(editedCountryName, "drawable", context.getPackageName());

        if(intId != 0) {
            return intId;
        } else {
            return R.drawable.noflag;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ArrayList<Country> country = new ArrayList<Country>();
        int selectNumber;
        int countNumber = 0;
        for(int i = 0; i < 3; i++) {
            selectNumber = (position*3) + countNumber;
            if(selectNumber < countryArrayList.size()) {
                country.add(countryArrayList.get(selectNumber));
                countNumber++;

                if(i == 0) {
                    setCard(viewHolder, R.id.countryNameColumn1, R.id.countryImageColumn1, country.get(i).getName());
                } else if(i == 1) {
                    setCardVisibility(viewHolder, R.id.cardViewColumn2, true);
                    setCard(viewHolder, R.id.countryNameColumn2, R.id.countryImageColumn2, country.get(i).getName());
                } else {
                    setCardVisibility(viewHolder, R.id.cardViewColumn3, true);
                    setCard(viewHolder, R.id.countryNameColumn3, R.id.countryImageColumn3, country.get(i).getName());
                }
            } else {
                if(i == 1) {
                    setCardVisibility(viewHolder, R.id.cardViewColumn2, false);
                } else {
                    setCardVisibility(viewHolder, R.id.cardViewColumn3, false);
                }
            }
        }
      //  viewHolder.countryImage.setImageDrawable(mContext.getDrawable(country.getImageResourceId(mContext)));
      //  viewHolder.mTextView.setText(countryArrayList.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (int)Math.ceil(countryArrayList.size()/3.0);
    }
}
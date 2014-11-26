package com.example.jgill.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JGill on 25/11/14.
 */
public class GridViewAdapter extends BaseAdapter {
    private ArrayList<Country> countryArrayList = new ArrayList();
    private Context contextFragment;

    public GridViewAdapter(Context contextFragment, ArrayList<Country> countryArrayList) {
        this.contextFragment = contextFragment;
        this.countryArrayList = countryArrayList;

    }

    @Override
    public int getCount() {
        return countryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return countryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = convertView;
        ViewHolder1 viewHolder = null;
// check whether this view has been created already for example in switching portrait to Landscape
        if (cell == null) {
            LayoutInflater layoutInflater = (LayoutInflater) contextFragment.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = layoutInflater.inflate(R.layout.single_gridcell_card, parent, false);
            viewHolder = new ViewHolder1(cell);
            cell.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder1) cell.getTag();
        }
        Country country= countryArrayList.get(position);
        viewHolder.countryImage.setImageResource(country.getImageLocation());

        //  viewHolder.countryImage.setImageResource(R.drawable.canada);
        viewHolder.countryName.setText(country.getName());


        return cell;

    }

    private int nameConverter (String countryName, Context context) {
        int intId = 0;

        String editedCountryName = countryName.replaceAll(" ","_").toLowerCase();
        System.out.println(editedCountryName);
        intId = context.getResources().getIdentifier(editedCountryName, "drawable", context.getPackageName());

        if(intId != 0) {
            return intId;
        } else {
            return R.drawable.canada;
        }
    }

    /*/
      ViewHolder Class to create easy access of the each GridCell which holds
      the Image view and TextView inside of the CardView
       */
    private class ViewHolder1 {
        TextView countryName;
        ImageView countryImage;
        CardView cardView;

        public ViewHolder1(View cellView) {

            countryName=(TextView)cellView.findViewById(R.id.countryTextView);
            countryImage = (ImageView) cellView.findViewById(R.id.countryImageView);

        }

    }
}

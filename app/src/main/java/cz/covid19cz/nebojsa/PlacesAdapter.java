package cz.covid19cz.nebojsa;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;

public class PlacesAdapter extends ArrayAdapter<String> {
    private ArrayList<String> places;
    private ArrayList<String> address;
    private ArrayList<String> distances;
    private String category;
    private LayoutInflater inflater = null;
    private String time;
    private Context context;
    private String bestTime;
    private Boolean selected = false;
    private int hour;
    private String minute;
    private ArrayList<Integer> pos = new ArrayList<>();


    public PlacesAdapter(Context context, ArrayList<String> places, ArrayList<String> address, ArrayList<String> distances, String category, Boolean selected, String Best_time) {
        super(context, 0, places);
        this.context = context;
        this.places = places;
        this.address = address;
        this.distances = distances;
        this.category = category;
        this.selected = selected;
        this.bestTime = Best_time;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NotNull
    @Override
    public View getView(final int position, View convertView, @NotNull ViewGroup parent) {
        // Get the data item for this position
        View v;
        if (convertView == null) {
            {
                if (selected) {
                    final AppLanguageManager appLanguageManager = new AppLanguageManager(parent.getContext());

                    if (appLanguageManager.getAppLanguage().equals("cz")) {
                        v = inflater.inflate(R.layout.selected_place_2_cz, parent, false);
                    } else if (appLanguageManager.getAppLanguage().equals("ar")) {
                        v = inflater.inflate(R.layout.selected_place_2_ar, parent, false);
                    } else {
                        v = inflater.inflate(R.layout.selected_place_2, parent, false);
                    }
                } else
                    v = inflater.inflate(R.layout.item_place, parent, false);

            }
        } else
            v = convertView;

        final AppLanguageManager appLanguageManager = new AppLanguageManager(getContext());
        TextView addPlace = v.findViewById(R.id.addPlace);
        TextView place_name = v.findViewById(R.id.tv_placeName);
        TextView place_address = v.findViewById(R.id.tv_placeAddress);
        if (selected) {
            place_name.setSelected(true);
            place_address.setSelected(true);
            TextView best_time = v.findViewById(R.id.tv_bestTime);
            if (bestTime.equals("No data") || bestTime.equals("Not today")) {
                best_time.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_data, 0, 0, 0);
                if (bestTime.equals("No data")) {
                    best_time.setTextColor(Color.GRAY);
                    if (appLanguageManager.getAppLanguage().equals("cz")) {
                        bestTime = getContext().getString(R.string.favourite_places_status_1_cz);
                    } else if (appLanguageManager.getAppLanguage().equals("ar")) {
                        bestTime = getContext().getString(R.string.favourite_places_status_1_ar);
                    }
                } else {
                    best_time.setTextColor(Color.parseColor("#FF4133"));
                    if (appLanguageManager.getAppLanguage().equals("cz")) {
                        bestTime = getContext().getString(R.string.favourite_places_status_2_cz);
                    } else if (appLanguageManager.getAppLanguage().equals("ar")) {
                        bestTime = getContext().getString(R.string.favourite_places_status_2_ar);
                    }
                }
            } else {
                Date date;

                try {

                    hour = Integer.parseInt(bestTime.substring(11, 13));
                    minute = bestTime.substring(14, 16);
                    time = hour + ":" + minute + " - " + (hour + 1) + ":" + minute;
                    bestTime = time;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //bestTime = Time;
            }


            best_time.setText(bestTime);
            addPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MapActivity) {
                        pos.add(position);
                        ((MapActivity) context).add_place();
                    }
                }
            });

        } else {
            CardView nearby_places_card = v.findViewById(R.id.card1);
            TextView place_distances = v.findViewById(R.id.tv_distance);
            ImageView cat_icon = v.findViewById(R.id.iv_cat_icon);
            place_distances.setText(distances.get(position));
            if (category.equals("park"))
                cat_icon.setBackground(v.getResources().getDrawable(R.drawable.ic_icon_park));
            else if (category.equals("shop"))
                cat_icon.setBackground(v.getResources().getDrawable(R.drawable.ic_icon_shop));
            else
                cat_icon.setBackground(v.getResources().getDrawable(R.drawable.ic_icon_pharmacy));

            nearby_places_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof MapActivity) {
                        pos.add(position);
                        ((MapActivity) context).select_Place(position);
                    }
                }
            });
        }

        place_address.setText(address.get(position));
        place_name.setText(places.get(position));

//        simpleCheckedTextView.setText(places.get(position));
//        // perform on Click Event Listener on CheckedTextView
//        simpleCheckedTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (simpleCheckedTextView.isChecked()) {
//                    // set cheek mark drawable and set checked property to false
//                    pos.remove((Object) position);
//                    value = "un-Checked";
//                    simpleCheckedTextView.setChecked(false);
//                } else {
//                    // set cheek mark drawable and set checked property to true
//                    pos.add(position);
//                    value = "Checked";
//                    simpleCheckedTextView.setChecked(true);
//                }
//            }
//        });
        return v;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public ArrayList<Integer> getSelected() {
        return pos;
    }
}

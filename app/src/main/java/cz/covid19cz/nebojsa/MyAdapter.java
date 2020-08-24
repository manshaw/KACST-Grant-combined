package cz.covid19cz.nebojsa;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;

import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;

    private ArrayList<String> Status;
    private ArrayList<String> GraphVal;
    private ArrayList<String> Category;
    private ArrayList<String> TimeDay;
    private ArrayList<String> Address;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> myDataset, ArrayList<String> Status, ArrayList<String> GraphVal, ArrayList<String> Category, ArrayList<String> TimeDay, ArrayList<String> Address) {
        mDataset = myDataset;
        this.Status = Status;
        this.GraphVal = GraphVal;
        this.Category = Category;
        this.TimeDay = TimeDay;
        this.Address = Address;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.selected_place, parent, false);

        return new MyAdapter.MyViewHolder(itemView);
    }

    public void delete(MyViewHolder holder, int pos) {
        int temp = holder.sharedPreferences.getInt("FavPlacesNum", 0);
        int temp_curr = 0;
        for (int i = pos; i < temp - 1; i++) {
            temp_curr = i + 1;
            holder.sharedPreferences.edit().putString("FavPlaceLoc" + i, holder.sharedPreferences.getString("FavPlaceLoc" + temp_curr, null)).apply();
            holder.sharedPreferences.edit().putString("FavPlaceNam" + i, holder.sharedPreferences.getString("FavPlaceNam" + temp_curr, null)).apply();
            holder.sharedPreferences.edit().putString("FavCat" + i, holder.sharedPreferences.getString("FavCat" + temp_curr, null)).apply();
        }
        holder.sharedPreferences.edit().putInt("FavPlacesNum", temp - 1).apply();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AppLanguageManager appLanguageManager = new AppLanguageManager(holder.card.getContext());

        if (Category.get(position).equals("pharmacy"))
            holder.cat_icon.setImageResource(R.drawable.ic_cat_pharmacy);
        else if (Category.get(position).equals("park"))
            holder.cat_icon.setImageResource(R.drawable.ic_cat_park);
        else
            holder.cat_icon.setImageResource(R.drawable.ic_cat_grocery);
        holder.Pl_Names.setText(mDataset.get(position));
        holder.tvAddress.setText(Address.get(position));
        if (Status.get(position).equals("null")) {
//            if (new AppLanguageManager(holder.context).getAppLanguage().equals("cz")) {
//                holder.bestTime.setText(R.string.favourite_places_status_1_cz);
//            } else {
//                holder.bestTime.setText(R.string.favourite_places_status_1);
//            }
            if (appLanguageManager.getAppLanguage().equals("cz")) {
                holder.bestTime.setText(R.string.favourite_places_status_1_cz);
            } else if (appLanguageManager.getAppLanguage().equals("ar")) {
                holder.bestTime.setText(R.string.favourite_places_status_1_ar);
            } else {
                holder.bestTime.setText(R.string.favourite_places_status_1);
            }

            holder.tvday.setVisibility(View.GONE);
            if (!GraphVal.get(position).equals("[]")) {
//                if (new AppLanguageManager(holder.context).getAppLanguage().equals("cz")) {
//                    holder.bestTime.setText(R.string.favourite_places_status_2_cz);
//                } else {
//                    holder.bestTime.setText(R.string.favourite_places_status_2);
//                }
                if (appLanguageManager.getAppLanguage().equals("cz")) {
                    holder.bestTime.setText(R.string.favourite_places_status_2_cz);
                } else if (appLanguageManager.getAppLanguage().equals("ar")) {
                    holder.bestTime.setText(R.string.favourite_places_status_2_ar);
                } else {
                    holder.bestTime.setText(R.string.favourite_places_status_2);
                }

                holder.bestTime.setTextColor(Color.parseColor("#FF4133"));
            } else {
                holder.bestTime.setTextColor(Color.GRAY);
//                holder.bestTime.setSingleLine();

            }

        } else {
            if (TimeDay.get(position).equals("Today")) {
                if (appLanguageManager.getAppLanguage().equals("cz")) {
                    holder.tvday.setText(R.string.graph_day1_cz);
                } else if (appLanguageManager.getAppLanguage().equals("ar")) {
                    holder.tvday.setText(R.string.graph_day1_ar);
                } else {
                    holder.tvday.setText(TimeDay.get(position));
                }
            } else {
                if (appLanguageManager.getAppLanguage().equals("cz")) {
                    holder.tvday.setText(R.string.graph_day2_cz);
                } else if (appLanguageManager.getAppLanguage().equals("ar")) {
                    holder.tvday.setText(R.string.graph_day2_ar);
                } else {
                    holder.tvday.setText(TimeDay.get(position));
                }
            }
            holder.bestTime.setText(Status.get(position));
            holder.bestTime.setTextColor(Color.parseColor("#4CAF50"));
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.context = view.getContext();
                if (holder.bestTime.getText().toString().equals("No data") || holder.bestTime.getText().toString().equals("Málo dat") || holder.bestTime.getText().toString().equals(view.getContext().getString(R.string.favourite_places_status_1_ar))) {
                    Intent intent = new Intent(holder.context, GraphActivity.class);
                    intent.putExtra("graphVal", "");
                    intent.putExtra("placeID", position);
                    intent.putExtra("bestTime", holder.bestTime.getText().toString());
                    holder.context.startActivity(intent);
//                    ((Activity)holder.context).finish();
                    //Toast.makeText(view.getContext(),"Sorry no data available for this place. Please support us by downloading Data Gathering App. Download link given in Help Us Section",Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(holder.context, GraphActivity.class);
                    intent.putExtra("graphVal", GraphVal.get(position));
                    intent.putExtra("placeID", position);
                    intent.putExtra("bestTime", holder.bestTime.getText().toString());
                    holder.context.startActivity(intent);
//                    ((Activity)holder.context).finish();
                }
            }
        });
//        holder.delPlace.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                final AlertDialog alertDialog1 = new AlertDialog.Builder(view.getContext())
//                        .setIcon(R.drawable.ic_delete_black_24dp)
//                        .setTitle("Remove Place")
//                        .setMessage("Remove this place?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                delete(holder, position);
//                                Intent intent = new Intent(view.getContext(), RiskActivity.class);
//                                view.getContext().startActivity(intent);
//                                ((Activity) view.getContext()).finish();
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.cancel();
//                            }
//                        })
//                        .show();
//            }
//        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private static final String PACAKGE_NAME = "pk.com.pakzarzameen.c19places";
        // each data item is just a string in this case
        protected TextView Pl_Names, bestTime, tvday, tvAddress;
        Context context;
        //        private ImageButton delPlace;
        SharedPreferences sharedPreferences;
        private CardView card;
        private ImageView cat_icon;


        public MyViewHolder(View v) {
            super(v);
            Pl_Names = v.findViewById(R.id.sp);
            bestTime = v.findViewById(R.id.tv_placeBT);
            card = v.findViewById(R.id.card);
            cat_icon = v.findViewById(R.id.iv_cat_icon1);
//            delPlace = (ImageButton) v.findViewById(R.id.ibDelPlace);
            tvAddress = v.findViewById(R.id.tv_address);
            tvday = v.findViewById(R.id.tv_placeBTday);

            sharedPreferences = v.getContext().getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        }
    }
}

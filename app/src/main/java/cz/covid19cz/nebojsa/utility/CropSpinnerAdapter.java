package cz.covid19cz.nebojsa.utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cz.covid19cz.erouska.R;

public class CropSpinnerAdapter extends ArrayAdapter<String> {

    private final String TAG = "EME.CropSpinnerAda";
    private final String[] spinnerTitles;
    private final int[] spinnerImages;
    Context mContext;

    public CropSpinnerAdapter(Context context, String[] titles, int[] images) {
        super(context, R.layout.custom_spinner_row);
        this.spinnerTitles = titles;
        this.spinnerImages = images;
        this.mContext = context;
        Log.v(TAG, "spinnerTitles size: " + spinnerTitles.length);
        Log.v(TAG, "spinnerImages size: " + spinnerImages.length);
    }

    @Override
    public int getCount() {
        return spinnerTitles.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG, "Current Position: " + position);
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_spinner_row, parent, false);
            mViewHolder.mFlag = convertView.findViewById(R.id.ivImage);
            mViewHolder.mName = convertView.findViewById(R.id.tvName);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mName.setText("Select Crop");

        try {
            mViewHolder.mFlag.setImageResource(spinnerImages[position]);
            mViewHolder.mName.setText(spinnerTitles[position]);
        } catch (Exception e) {
            Log.e(TAG, "Error in Position: " + e.getMessage());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        ImageView mFlag;
        TextView mName;
    }
}

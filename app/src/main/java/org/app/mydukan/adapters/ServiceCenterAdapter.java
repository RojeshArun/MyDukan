package org.app.mydukan.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.ServiceCenterInfo;
import org.app.mydukan.server.ApiResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceCenterAdapter extends BaseAdapter {

    // Declare Variables
    private Context mContext;
    private LayoutInflater inflater;
    private List<ServiceCenterInfo> servicecenterlist = null;
    private ArrayList<ServiceCenterInfo> arraylist;

    public ServiceCenterAdapter(Context context, ArrayList<ServiceCenterInfo> servicecenterlist) {
       this. mContext = context;
        this.servicecenterlist = servicecenterlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<ServiceCenterInfo>();
        this.arraylist.addAll(servicecenterlist);
    }
    public class ViewHolder {
        TextView name;
        TextView brand;
        TextView address;
        TextView contact;
        TextView city;
        TextView pincode;
        TextView state;
    }

    @Override
    public int getCount() {
        return servicecenterlist.size();
    }

    @Override
    public ServiceCenterInfo getItem(int position) {
        return servicecenterlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_scenter, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.tv_centerName);
            holder.brand = (TextView) view.findViewById(R.id.tv_centerBrand);
            holder.address = (TextView) view.findViewById(R.id.tv_centerAddress);
            holder.contact = (TextView) view.findViewById(R.id.tv_centerContact);
            holder.city= (TextView) view.findViewById(R.id.tv_centerCity);
            holder.pincode = (TextView) view.findViewById(R.id.tv_centerPincode);
            holder.state = (TextView) view.findViewById(R.id.tv_centerState);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(servicecenterlist.get(position).getServicecenter_NAME());
        holder.brand.setText(servicecenterlist.get(position).getServicecenter_BRAND());
        holder.address.setText(servicecenterlist.get(position).getServicecenter_ADDRESS());
        holder.contact.setText(servicecenterlist.get(position).getServicecenter_CONTACTPERSION()+" "+servicecenterlist.get(position).getServicecenter_PHONENUMBER()+" \n"+servicecenterlist.get(position).getServicecenter_EMAILID());
        holder.city.setText(servicecenterlist.get(position).getServicecenter_CITY()+", "+servicecenterlist.get(position).getStateservicecenter_STATE()+" "+String.valueOf(servicecenterlist.get(position).getServicecenter_PINCODE()));
      //  holder.pincode.setText(String.valueOf(servicecenterlist.get(position).getServicecenter_PINCODE()));
       // holder.state.setText(servicecenterlist.get(position).getStateservicecenter_STATE());

     /*   // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Send single item click data to SingleItemView Class
                Intent intent = new Intent(mContext, SingleItemView.class);
                // Pass all data name
                intent.putExtra("name",(servicecenterlist.get(position).getName()));
                // Pass all data brand
                intent.putExtra("brand",(servicecenterlist.get(position).getBrand()));
                // Pass all data address
                intent.putExtra("address",(servicecenterlist.get(position).getAddress()));
                // Pass all data contact
                intent.putExtra("contact",(servicecenterlist.get(position).getContact()));
                // Pass all data city
                intent.putExtra("city",(servicecenterlist.get(position).getCity()));
                // Pass all data pincode
                intent.putExtra("pincode",(servicecenterlist.get(position).getPincode()));
                // Pass all data state
                intent.putExtra("state",(servicecenterlist.get(position).getState()));
                // Pass all data flag
                // Start SingleItemView Class
                mContext.startActivity(intent);
            }
        });
*/
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        servicecenterlist.clear();
        if (charText.length() == 0) {
            servicecenterlist.addAll(arraylist);
        }
        else
        {
            for (ServiceCenterInfo centerInfo : arraylist)
            {
                if (centerInfo.getServicecenter_BRAND().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    servicecenterlist.add(centerInfo);
                }
                else if (centerInfo.getServicecenter_CITY().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    servicecenterlist.add(centerInfo);
                }
                else if (centerInfo.getStateservicecenter_STATE().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    servicecenterlist.add(centerInfo);
                }
                else if (centerInfo.getServicecenter_ADDRESS().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    servicecenterlist.add(centerInfo);
                }  else if (centerInfo.getServicecenter_PINCODE().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    servicecenterlist.add(centerInfo);
                }
            }
        }
        notifyDataSetChanged();
    }

}

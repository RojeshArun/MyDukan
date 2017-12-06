package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arpithadudi on 10/7/16.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> mDataList;
    private Context mContext;
    private MyDukan mApp;
    private ComplaintsAdapterListener mListener;

    public CategoryAdapter(Context context, String supplierName, ComplaintsAdapterListener listener) {
        mContext = context;
        mDataList = new ArrayList<Category>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = listener;
    }

    public void addItems(ArrayList<Category> list) {
        mDataList.clear();
        mDataList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.category_listitem, parent, false);
        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Category category = mDataList.get(position);
        if (category.getName() != null) {
            String categoryname = category.getName();
            categoryname = categoryname.replace(".", " "); //-KX41ilBK4hjaSDIV419(pricedrop) ,   -KZkiZ785mct3R2ROHlf(New Launch) ,  -Ksr9awbFUvRMz-Uu1i3(UpComing Launches)

         /*
          if(category.getId().equalsIgnoreCase("-KX41ilBK4hjaSDIV419")){
               holder.mNameView.setTextColor(Color.parseColor("#E65100"));
              //  holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_samsung));
            }
            if(category.getId().equalsIgnoreCase("-KZkiZ785mct3R2ROHlf")){
               holder.mNameView.setTextColor(Color.parseColor("#E65100"));
               // holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_samsung));
            }
            if(category.getId().equalsIgnoreCase("-Ksr9awbFUvRMz-Uu1i3")){
                holder.mNameView.setTextColor(Color.parseColor("#E65100"));
               // holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_samsung));
            }
            holder.mNameView.setText(categoryname);
            */

            switch (category.getId()) {
                case "-KTTV8BvY0PpRUOEyKOo":
                    //SAMSUNG = -KTTV8BvY0PpRUOEyKOo
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_samsung));
                    break;
                case "-KZkiZ785mct3R2ROHlf":
                    //NEW LAUNCH= -KZkiZ785mct3R2ROHlf
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_newlaunch));

                    break;
                case "-KX41ilBK4hjaSDIV419":
                    //PRICE DROP = -KX41ilBK4hjaSDIV419
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_pricedrop));

                    break;
                case "-Ksr9awbFUvRMz-Uu1i3":
                    //     UPCOMING LAUNCH     = -Ksr9awbFUvRMz-Uu1i3
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_upcominglaunch));

                    break;
                case "-KuxmE7rc7w4WpFYKWE5":
                    // IPHONE = -KuxmE7rc7w4WpFYKWE5 ,-KuxmE7rc7w4WpFYKWE5
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_iphone));
                    break;

                case "-KTmmfHzpn5Nn-ZZ0N1A":
                    // GIONEE=  -KTmmfHzpn5Nn-ZZ0N1A
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_gionee));

                    break;
                case "-KTwazqP9cmccZNbt9hb":
                    // LAVA = -KTwazqP9cmccZNbt9hb
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lava));

                    break;
                case "-KrovlBYSgcM3NA8GxAg":
                    // COMIO = -KrovlBYSgcM3NA8GxAg
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_comio));

                    break;

                case "-KThq1qTj7upEbf5OAGV":
                    // ASUS = -KThq1qTj7upEbf5OAGV

                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_asus));
                    break;
                case "-KTwlJGRrjcnqumXCzGV":

                    // LENOVO =-KTwlJGRrjcnqumXCzGV
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lenovo));
                    break;
                case "-KU2hE65nFcTF0eYa0u_":
                    // VIVO= -KU2hE65nFcTF0eYa0u_
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_vivo));
                    break;
                case "-KUIMuoS_jziNt_deTkv":
                    //     ITEL    = -KUIMuoS_jziNt_deTkv
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_itel));
                    break;
                case "-KTx_nTRtIoZtqG_ErSD":
                    // ZIOX =-KTx_nTRtIoZtqG_ErSD
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_ziox));
                    break;

                case "-KU0J03fDBO4Zrsb3tTz":
                    // MICROMAX SMART PHONES=  -KU0J03fDBO4Zrsb3tTz
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_micromax));
                    break;
                case "-KU1r_NUU5DdFPJLS1C2":
                    //XIAOMI= -KU1r_NUU5DdFPJLS1C2
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_xiaomi));
                    break;

                case "-KU23bD-T28Bx3AF87K0":
                    // MOTOROLA = -KU23bD-T28Bx3AF87K0
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_motorola));
                    break;

                case "-KTxmDYOCC_MxyRtu83V":
                    //OPPO= -KTxmDYOCC_MxyRtu83V
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_oppo));
                    break;

                case "-KUSXXe-dC52lGIcD16J":
                    //ZEN
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_zen));
                    break;


                case "-KTlvtThlXhnokm3hux3":
                    //INTEX= -KTlvtThlXhnokm3hux3
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_intex));
                    break;

                case "-KTzALKLYilcvlcnM1Z0":
                    // CELKON
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_celkon));
                    break;

                case "-KUFxA23cmu4baJ3BL0P":
                    //NOKIA
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_nokia));
                    break;

                case "-KkvKrWFqJR7tpaAb7Sy":
                    //TECNO
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_tecno));
                    break;


                case "-KTmMpmpYcsv3XVjr3Nl":
                    //HTC
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_htc));
                    break;

                case "-Kjg5QfunwoNhVUAXz-z":
                    //Google Pixel
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_pixel));
                    break;
                case "-KpY9KtzUtnRFFoVLjXO":
                    //SPICE= -KpY9KtzUtnRFFoVLjXO
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_spice));
                    break;
                case "-KqmnwSZdtFqRtjVqeDh":
                    //COOLPAD
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_coolpad));
                    break;
                case "-Kum8bSbB3Qq5OSVbUM8":
                    //SMARTRON
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_smartron));

                    break;
                case "-KU1tkMWpVGdbiozbdMk":
                    //MICROMAX FEATURE PHONES
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_micromax_featurephones));
                    break;
                case "-KU2u7z3XVfFrHQiiXAg":
                    //PANASONIC
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_panasonic));
                    break;
                case "-KU1m3xWbMbRpybhTFyb":
                    //INFOCUS
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_infocus));
                    break;
                case "-KTzBkfTUVuHcjKL-_nG":
                    //HUAWEI
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_huawei));
                    break;
                case "-KU1oqGw6Vvs9E5R4AAk":
                    //LG
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lg));
                    break;
                case "-KTzBNFwVsjGDHaYfR9A":
                    //LEPHONE =-KTzBNFwVsjGDHaYfR9A
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lephone));
                    break;
                case "-KreSsMPOYr7fsSqyB8C":
                    //MAFE=-KreSsMPOYr7fsSqyB8C
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mafe));
                    break;
                case "-KepYPQdF-uJOEscF0lm":
                    //m-tech
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mtech));
                    break;
                case "-Ksc37Nj0Ye0ZrnbgK8D":
                    //JIVI
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_jivi));
                    break;
                case "-KU7f5qx3Oe-XxEjfbZk":
                    //SONY
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_sony));
                    break;
                case "-KUL97n-vNpLoNW2AK_q":
                    //-KUL97n-vNpLoNW2AK_q =KARBONN
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_karbonn));
                    break;
                case "-KqLd12Ir-CII-o28yST":
                    //LEMON
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lemon));
                    break;
                case "-KUQDt2SABFB5SMXXRk6":
                    //JIO LYF
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lyf));
                    break;
                case "-Kl2tmit89SZyWuyOaNo":
                    //SAMSUNG ACCESSORIES
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_samsung_accessories));
                    break;
                case "-KscjmvCSVlEY6WSloaS":
                    //ORAIMO Accessories
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_oraimo));
                    break;
                case "-KU6o-IaO1J_H0MDNNyf":
                    //IBALL
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_iball));
                    break;
                case "-KU1thpXX242Q7NSEzcL":
                    //YU
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_yu));
                    break;
                case "-KpJlnV76WbYmrcLHVxh":
                    //Mozomaxx
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mozomaxx));
                    break;
                case "-KVzHyaGhW1O7FdEjYuo":
                    //ACER
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_acer));
                    break;
                case "-KfQXgzUi2mh4qfHWlI7":
                    //EDGE MOBILE
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mobileedge));
                    break;
                case "-KULvmv5RjPypNDXYXvR":
                    //VIDEOCON
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_videocon));
                    break;
                case "-KYXXJ0Q3io-JtGMe8bb":
                    //ZOPO
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_zopo));
                    break;
                case "-KkBlkABWX7NtvkQgtcL":
                    //DETEL
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_detel));
                    break;
                case "-KkUFMpMIXXPBl-yFPGh":
                    //EXMART
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_exmart));
                    break;
                case "-KusMlizwhhlJ3Clxjtb":
                    //VOTO
                    holder.mNameView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_voto));
                    break;
                default:
                    holder.mNameView.setText(categoryname);
                    break;
            }

        }
        //    holder.mNameView.setText(category.getName());
        holder.mCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mCategoryLayout;
        private Button mNameView;

        public ViewHolder(final View itemView) {
            super(itemView);
            mCategoryLayout = (LinearLayout) itemView.findViewById(R.id.categoryLayout);
            mNameView = (Button) itemView.findViewById(R.id.name);
        }
    }

    public interface ComplaintsAdapterListener {
        void OnClick(int position);
    }
}

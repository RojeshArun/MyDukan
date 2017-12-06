package org.app.mydukan.activities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.app.mydukan.R;


/**
 * Created by vaibhavkumar on 09/10/17.
 */

public class chat_rec extends RecyclerView.ViewHolder  {



    TextView leftText,rightText,welcomeText;

    public chat_rec(View itemView){
        super(itemView);
        welcomeText = (TextView)itemView.findViewById(R.id.wellComeText);
        leftText = (TextView)itemView.findViewById(R.id.leftText);
        rightText = (TextView)itemView.findViewById(R.id.rightText);


    }
}

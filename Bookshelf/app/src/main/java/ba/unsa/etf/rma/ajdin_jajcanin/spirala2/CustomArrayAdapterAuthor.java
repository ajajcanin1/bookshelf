package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 06/04/2018.
 */

public class CustomArrayAdapterAuthor extends ArrayAdapter<Autor> {

    private int _reso;
    private List<Autor> mList = new ArrayList<>();
    private Context mCont;


    public CustomArrayAdapterAuthor(Context context, int resource, List<Autor> objects) {
        super(context, resource, objects);
        mList = objects;
        _reso = resource;
        mCont = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout newView;
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().
                    getSystemService(inflater);
            li.inflate(_reso, newView, true);
        } else {
            newView = (LinearLayout) convertView;
        }


        Autor p = getItem(position);
        //Log.d("POZICIJA U ADAPTERU: ", String.valueOf(position));
        //Log.d("POZICIJA U LISTI ORG: ", String.valueOf(b.getId()));

        TextView pName = (TextView) newView.findViewById((R.id.eImeAutora));
        TextView pNum = (TextView) newView.findViewById((R.id.eBrojKnjiga));

        pName.setText(p.getImeiPrezime());
        pNum.setText(getContext().getResources().getString(R.string.etNumBooks)+ Integer.toString(p.getKnjige().size()));

        return newView;
    }
}

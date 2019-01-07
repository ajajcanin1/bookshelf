package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 05/04/2018.
 */

public class CustomArrayAdapter extends ArrayAdapter<Knjiga> {

    private int _reso;
    private List<Knjiga> mList = new ArrayList<>();
    private Context mCont;
    private OnClick oc;

    public CustomArrayAdapter(Context context, int resource, List<Knjiga> objects) {
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
        oc = (OnClick) mCont;
        Knjiga b = mList.get(position);
        //Log.d("POZICIJA U ADAPTERU: ", String.valueOf(position));
        //Log.d("POZICIJA U LISTI ORG: ", String.valueOf(b.getId()));

        TextView bName = (TextView) newView.findViewById((R.id.eNaziv));
        TextView bAuthor = (TextView) newView.findViewById((R.id.eAutor));
        TextView bDatumObjavljivanja = (TextView) newView.findViewById(R.id.eDatumObjavljivanja);
        ImageView bImg = (ImageView) newView.findViewById((R.id.eNaslovna));
        TextView bOpis = (TextView) newView.findViewById(R.id.eOpis);
        TextView bBrojStranica = (TextView) newView.findViewById(R.id.eBrojStranica);
        Button btnPreporuci = (Button) newView.findViewById(R.id.dPreporuci);

        if(b.getDaLiJePlav()){
            newView.setBackgroundResource(R.color.colorSelect);
        }
        else newView.setBackgroundResource(R.color.colorWhite);
        bName.setText(b.getNaziv());
        bAuthor.setText(b.getImeAutora());
        bDatumObjavljivanja.setText(b.getDatumObjavljivanja());
        bOpis.setText(b.getOpis());
        if(b.getBrojStranica() != -1) bBrojStranica.setText(Integer.toString(b.getBrojStranica()));
        else bBrojStranica.setText("");

        /*FileOutputStream outputStream;
        Bitmap bitmap = null;
        try {
            outputStream = mCont.openFileOutput(b.getNaziv(), mCont.MODE_PRIVATE);
            while(bitmap == null)
            {
                bitmap = getBitmapFromUri(Uri.parse(b.getNaslovna()));
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream);
                //bitmap = MediaStore.Images.Media.getBitmap(mCont.getContentResolver(), Uri.parse(b.getNaslovna()));
            }
            outputStream.close();
            //bImg.setImageBitmap(BitmapFactory.decodeStream(mCont.openFileInput(b.getNaziv())));
            bImg.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        btnPreporuci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oc.onPreporuciClick(b.getId());
            }
        });


        try {

            if(!b.getNaslovna().isEmpty()) {
                File f = new File(b.getNaslovna(), b.getTitle());
                Bitmap bitImg = BitmapFactory.decodeStream(new FileInputStream(f));

                bImg.setImageBitmap(bitImg);
            }
            else if (b.getSlika() == null) bImg.setImageResource(R.drawable.ic_launcher_background);
            else {
                Picasso.with(mCont).load(b.getSlika().toString()).into(bImg);

            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return newView;
    }

    public interface OnClick{
        public void onPreporuciClick(String p);
    }
    /*private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = mCont.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }*/


}

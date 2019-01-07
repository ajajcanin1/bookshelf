package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class FragmentPreporuci extends Fragment {
    public FragmentPreporuci() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_preporuci, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String idKnjige = (String) getArguments().getString("idKnjige");
        BazaOpenHelper baza = new BazaOpenHelper(getContext());
        ArrayList<String> kontakti = new ArrayList<>();
        ArrayList<String> imena = new ArrayList<>();

        Spinner sKontakti = (Spinner) getView().findViewById(R.id.sKontakti);
        TextView bName = (TextView) getView().findViewById((R.id.eNaziv));
        TextView bAuthor = (TextView) getView().findViewById((R.id.eAutor));
        TextView bDatumObjavljivanja = (TextView) getView().findViewById(R.id.eDatumObjavljivanja);
        ImageView bImg = (ImageView) getView().findViewById((R.id.eNaslovna));
        TextView bOpis = (TextView) getView().findViewById(R.id.eOpis);
        TextView bBrojStranica = (TextView) getView().findViewById(R.id.eBrojStranica);
        Button btnSend = (Button) getView().findViewById(R.id.dPosalji);

        //Knjiga b = KnjigeHolder.getInstance().getKnjige().get(position);
        Knjiga b = baza.getKnjiga(idKnjige);
        bName.setText(b.getNaziv());
        bAuthor.setText(b.getImeAutora());
        bDatumObjavljivanja.setText(b.getDatumObjavljivanja());
        bOpis.setText(b.getOpis());
        if(b.getBrojStranica() != -1) bBrojStranica.setText(Integer.toString(b.getBrojStranica()));
        else bBrojStranica.setText("");


        try {
            if(!b.getNaslovna().isEmpty()) {
                File f = new File(b.getNaslovna(), b.getTitle());
                Bitmap bitImg = BitmapFactory.decodeStream(new FileInputStream(f));

                bImg.setImageBitmap(bitImg);
            }
            else if (b.getSlika() == null) bImg.setImageResource(R.drawable.ic_launcher_background);
            else
                Picasso.with(getContext()).load(b.getSlika().toString()).into(bImg);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //spinner population
        ContentResolver cr = getContext().getContentResolver();
        String[] projection = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
        String selection = ContactsContract.CommonDataKinds.Email.DATA + " <> \"\"";
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection,
              selection,null, null);
        while(c.moveToNext()) {
            kontakti.add(c.getString(3));
            imena.add(c.getString(1));
        }
        c.close();
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, kontakti);
        sKontakti.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[] {sKontakti.getSelectedItem().toString()});
                i.putExtra(Intent.EXTRA_SUBJECT, "Spirala4");
                i.putExtra(Intent.EXTRA_TEXT   , "Zdravo "+ imena.get(sKontakti.getSelectedItemPosition())+",\n" +
                        "Pročitaj knjigu " + b.getNaziv() + " od " + b.getAutori().get(0).getImeiPrezime()+"!");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

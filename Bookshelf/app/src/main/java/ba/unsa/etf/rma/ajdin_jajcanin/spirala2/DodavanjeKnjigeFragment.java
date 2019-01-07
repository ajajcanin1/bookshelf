package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class DodavanjeKnjigeFragment extends Fragment{

    ImageView iwNaslovna;
    ArrayList<Autor> autori;
    String uri = new String();
    String title = new String();
    private OnClick oc;
    public static final int PICK_IMAGE = 1;

    public DodavanjeKnjigeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dodavanje_knjige, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        BazaOpenHelper baza = new BazaOpenHelper(getContext());
        ArrayList<String> zanrovi = baza.getKategorije();

        autori = baza.getAutori();

        iwNaslovna = (ImageView)getView().findViewById(R.id.naslovnaStr);
        final Spinner sZanrovi = (Spinner)getView().findViewById(R.id.sKategorijaKnjige);
        final EditText etImeAutora = (EditText)getView().findViewById(R.id.imeAutora);
        final EditText etNazivKnjige = (EditText)getView().findViewById(R.id.nazivKnjige);
        final Button btnNadjiSliku = (Button)getView().findViewById(R.id.dNadjiSliku);

        Button btnDodaj = (Button)getView().findViewById(R.id.dUpisiKnjigu);
        Button btnPonisti = (Button)getView().findViewById(R.id.dPonisti);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, zanrovi);
        sZanrovi.setAdapter(adapter);       //dodati naslov (na -1 poziciju)

        oc = (OnClick)getActivity();

        btnDodaj.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (sZanrovi.getCount() == 0){
                    Toast.makeText(getActivity(), R.string.tErrorGenre,
                            Toast.LENGTH_LONG).show();
                }else if(etImeAutora.getText().toString().isEmpty() ||
                        etNazivKnjige.getText().toString().isEmpty() ||
                        iwNaslovna.getDrawable()==null){
                    Toast.makeText(getActivity(), R.string.tErrorBook,
                            Toast.LENGTH_LONG).show();
                }else{
                    Autor a = new Autor(etImeAutora.getText().toString());
                    /*Boolean nijePronadjen = true;
                    for(Autor p : autori) {
                        if(p.getImeiPrezime().toLowerCase().equals(etImeAutora.getText().toString().toLowerCase())) {
                            nijePronadjen=false;
                            p.dodajKnjigu(etNazivKnjige.getText().toString());
                           // a = p; <---------------------------------------------------------------------------------
                        }
                    }
                    if(nijePronadjen) {
                        a.dodajKnjigu(etNazivKnjige.getText().toString());
                        autori.add(a);
                    }
                    KnjigeHolder.getInstance().addKnjige(new Knjiga(a,
                            etNazivKnjige.getText().toString(), sZanrovi.getSelectedItem().toString(),
                            uri, false, title));
                    baza.dodajKnjigu(new Knjiga(a,
                            etNazivKnjige.getText().toString(), sZanrovi.getSelectedItem().toString(),
                            uri, false, title));*/
                    Knjiga k = new Knjiga(a,
                            etNazivKnjige.getText().toString(), sZanrovi.getSelectedItem().toString(),
                            uri, false, title);
                    k.setId("");
                    try {
                        k.setSlika(new URL(uri));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    baza.dodajKnjigu(k);
                    Toast.makeText(getActivity(), R.string.tAddedBook,
                            Toast.LENGTH_LONG).show();

                    iwNaslovna.setImageDrawable(null);
                    etImeAutora.setText("");
                    etNazivKnjige.setText("");
                    uri= "";
                }

            }
        });

        btnPonisti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                oc.onPonistiClick();
                //DodavanjeKnjigeAkt.this.finish();
                //Intent mIntent=new Intent(.., KategorijeAkt.class);
                //startActivity(mIntent);
            }
        });

        btnNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent sendIntent = new Intent();

                sendIntent.setAction(Intent.ACTION_GET_CONTENT);
                sendIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                sendIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                sendIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sendIntent.setType("image/*");

                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(sendIntent, "Izaberite sliku"), PICK_IMAGE);
                }*/

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                getIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
    }
    public interface OnClick{
        public void onPonistiClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            //if (requestCode == PICK_IMAGE ) {

            Uri imageUri = data.getData();
            String filename=imageUri.toString().substring(imageUri.toString().lastIndexOf("/")+1);
            //uri = imageUri.toString();
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final int flags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    ContentResolver conRes = this.getContentResolver();
                    conRes.takePersistableUriPermission(imageUri, flags);
                }*/
            final InputStream imageStream;
            try {
                //Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                uri = saveToInternalStorage(selectedImage, filename);
                iwNaslovna.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.tImageError, Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.tErrorNoImg, Toast.LENGTH_LONG).show();
        }
    }
    private String saveToInternalStorage(Bitmap bitmapImage, String filename){
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        Random rand = new Random();
        title = filename + (rand.nextInt(1000)+1);
        File mypath=new File(directory, title);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 30, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}




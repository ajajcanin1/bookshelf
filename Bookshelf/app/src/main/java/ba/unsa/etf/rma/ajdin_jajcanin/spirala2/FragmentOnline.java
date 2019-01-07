package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FragmentOnline extends Fragment implements DohvatiKnjige.IDohvatiKnjigeDone,
    DohvatiNajnovije.IDohvatiNajnovijeDone, MyResultReceiver.Receiver{

    Spinner sRez;
    Spinner sZanrovi;
    ArrayList<Knjiga> spinnerKnjige = new ArrayList<>();
    private OnClick oc;
    public FragmentOnline() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_online, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //ArrayList<String> zanrovi = (ArrayList<String>) getArguments().getStringArrayList("zanroviSpinner");
        //ArrayList<Autor> autori = (ArrayList<Autor>) getArguments().getSerializable("autori");
        BazaOpenHelper baza = new BazaOpenHelper(getContext());
        ArrayList<String> zanrovi = baza.getKategorije();

        sZanrovi = (Spinner) getView().findViewById(R.id.sKategorije);
        final EditText etTekstUpit = (EditText) getView().findViewById(R.id.tekstUpit);
        sRez = (Spinner) getView().findViewById(R.id.sRezultat);
        final Button btnRun = (Button) getView().findViewById(R.id.dRun);
        final Button btnAdd = (Button) getView().findViewById(R.id.dAdd);
        final Button btnBack = (Button) getView().findViewById(R.id.dPovratak);
        oc = (OnClick)getActivity();
        ArrayAdapter adapter1 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, zanrovi);

        sZanrovi.setAdapter(adapter1);       //dodati naslov (na -1 poziciju)
        //sRez.setAdapter(adapter2);       //dodati naslov (na -1 poziciju)

        btnRun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String query = etTekstUpit.getText().toString();
                if(!query.contains(";") &&
                        !query.contains(" ") && !query.contains(":")) //potrebna ova linija y/n?
                new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone)FragmentOnline.this).execute(query);
                else if(query.contains(";") && !query.contains(":")) {
                    new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone) FragmentOnline.this).
                            execute(query.split(";"));
                }
                else if(query.startsWith("autor:")) {
                    new DohvatiNajnovije((DohvatiNajnovije.IDohvatiNajnovijeDone)FragmentOnline.this).execute(query.substring(6));
                }
                else if(query.startsWith("korisnik:")) {
                    MyResultReceiver mReceiver = new MyResultReceiver(new Handler());
                    mReceiver.setReceiver(FragmentOnline.this);
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), KnjigePoznanika.class);
                    intent.putExtra("idKorisnika", query.substring(9));
                    intent.putExtra("resultReceiver", mReceiver);
                    getActivity().startService(intent);
                }

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int p = sRez.getSelectedItemPosition();
                if(p != -1){
                    Knjiga k = spinnerKnjige.get(p);
                    k.dodijeliRedniBr();
                    k.setZanr(sZanrovi.getSelectedItem().toString());
                    k.setNaslovna("");
                    baza.dodajKnjigu(k);
                    /*Knjiga k = spinnerKnjige.get(p);
                    k.dodijeliRedniBr();
                    KnjigeHolder.getInstance().addKnjige(k);
                    k.setZanr(sZanrovi.getSelectedItem().toString());
                    k.setNaslovna("");
                    for (int j = 0; j < k.getAutori().size(); j++) {
                        Autor a = new Autor(k.getAutori().get(j).getImeiPrezime());
                        Boolean nijePronadjen = true;
                        for (Autor x : autori) {
                            if (x.getImeiPrezime().toLowerCase().equals(a.getImeiPrezime().toLowerCase())) {
                                nijePronadjen = false;
                                x.dodajKnjigu(k.getId());
                                // a = p; <---------------------------------------------------------------------------------
                            }
                        }
                        if (nijePronadjen) {
                            a.dodajKnjigu(k.getId());
                            autori.add(a);
                        }
                    }*/

                    Toast.makeText(getActivity(), R.string.tAddedBook, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                oc.onPonistiClick();
            }
        });
    }

    @Override
    public void onDohvatiDone(ArrayList<Knjiga> rez) {
        spinnerKnjige = rez;
        ArrayList<String> idKnjiga = new ArrayList<>();
        for (int i=0; i < rez.size(); i++)
            idKnjiga.add(rez.get(i).getNaziv());
        ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, idKnjiga);
        sRez.setAdapter(adapter2);
    }
    public void onNajnovijeDone(ArrayList<Knjiga> rez) {
        spinnerKnjige = rez;
        ArrayList<String> naziviKnjiga = new ArrayList<>();
        for (int i=0; i < rez.size(); i++)
            naziviKnjiga.add(rez.get(i).getNaziv());
        ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, naziviKnjiga);
        sRez.setAdapter(adapter2);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

    }
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData){
        if(resultCode == 1) {
            spinnerKnjige = resultData.getParcelableArrayList("result");
            ArrayList<String> naziviKnjiga = new ArrayList<>();
            for (int i=0; i < spinnerKnjige.size(); i++)
                naziviKnjiga.add(spinnerKnjige.get(i).getNaziv());
            ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, naziviKnjiga);
            sRez.setAdapter(adapter2);

        }
        else if(resultCode == 2)
            Toast.makeText(getActivity(), R.string.tImageError, Toast.LENGTH_LONG).show();
    }
    public interface OnClick{
        public void onPonistiClick();
    }
}


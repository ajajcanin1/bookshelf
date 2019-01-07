package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListeFragment extends Fragment {

    ArrayAdapter<String> adapter;
    ArrayList<Autor> autori;
    ArrayList<String> input;
    CustomArrayAdapterAuthor adapterAuth;
    private BazaOpenHelper baza;
    private OnClick oc;

    public ListeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
         baza= new BazaOpenHelper(getActivity());
        Button btnSearch = (Button)getView().findViewById(R.id.dPretraga);
        Button btnAddBook = (Button)getView().findViewById(R.id.dDodajKnjigu);
        Button btnAuthors = (Button)getView().findViewById(R.id.dAutori);
        Button btnGenres = (Button)getView().findViewById(R.id.dKategorije);
        Button btnOnline = (Button)getView().findViewById(R.id.dDodajOnline);


        final LinearLayout llSearch = (LinearLayout)getView().findViewById(R.id.pretraziLayout);
        final Button btnAddGenre= (Button)getView().findViewById(R.id.dDodajKategoriju);
        final EditText etSearch = (EditText)getView().findViewById(R.id.tekstPretraga);
        final ListView lvGenre = (ListView)getView().findViewById(R.id.listaKategorija);
        //input = getArguments().getStringArrayList("zanrovi");
        input = baza.getKategorije();
        //autori = getArguments().getParcelableArrayList("autori");
        autori = baza.getAutori();
        //int x = autori.size();
        //adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, input);
        oc = (OnClick)getActivity();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, input);
        lvGenre.setAdapter(adapter);

        if(DaLiJeAutor.getInstance().isDaLiJeAutor()) {
            adapterAuth = new CustomArrayAdapterAuthor(getActivity(), R.layout.autori_liste, autori);
            lvGenre.setAdapter(adapterAuth);
            llSearch.setVisibility(View.GONE);
            btnAddGenre.setVisibility(View.GONE);
        } else {
            llSearch.setVisibility(View.VISIBLE);
            btnAddGenre.setVisibility(View.VISIBLE);
        }

        //adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, input);
        //lvGenre.setAdapter(adapter);


        etSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(btnAddGenre.isEnabled())
                    btnAddGenre.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                adapter.getFilter().filter(etSearch.getText().toString(), new Filter.FilterListener(){
                    @Override
                    public void onFilterComplete(int i){
                        if(i == 0 && !(etSearch.getText().toString().equals(""))) {
                            btnAddGenre.setEnabled(true);
                        }else {
                            etSearch.setText("");
                        }
                    }
                });
                //Log.d("test1", Integer.toString(adapter.getCount()));
            }
        });

        btnAddGenre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, input);
                lvGenre.setAdapter(adapter);
                input.add(0, etSearch.getText().toString());
                baza.dodajKategoriju(etSearch.getText().toString());
                adapter.notifyDataSetChanged();
                etSearch.setText("");
                btnAddGenre.setEnabled(false);
            }
        });


        btnAddBook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                oc.onAddClick();
                //DodavanjeKnjigeFragment dkf = new DodavanjeKnjigeFragment();
                //getFragmentManager().beginTransaction().replace(R.id.frameMain, dkf).commit();
                /*Intent intent = new Intent(KategorijeAkt.this, DodavanjeKnjigeAkt.class);
                intent.putExtra("Zanrovi", input);
                KategorijeAkt.this.startActivityForResult(intent, 1);*/

            }

        });

        lvGenre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(DaLiJeAutor.getInstance().isDaLiJeAutor())
                    oc.onItemClick(autori.get(i).getImeiPrezime(), DaLiJeAutor.getInstance().isDaLiJeAutor());
                else
                    oc.onItemClick(input.get(input.indexOf(adapterView.getItemAtPosition(i))), DaLiJeAutor.getInstance().isDaLiJeAutor());
                //ispravljena greska
                /*Intent intentLista = new Intent(KategorijeAkt.this, ListaKnjigaAkt.class);
                intentLista.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentLista.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intentLista.putExtra("Zanr", adapterView.getItemAtPosition(i).toString());
                KategorijeAkt.this.startActivity(intentLista);*/
            }
        });

        btnAuthors.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                adapterAuth = new CustomArrayAdapterAuthor(getActivity(), R.layout.autori_liste, autori);
                lvGenre.setAdapter(adapterAuth);
                DaLiJeAutor.getInstance().setDaLiJeAutor(true);
                llSearch.setVisibility(View.GONE);
                btnAddGenre.setVisibility(View.GONE);

            }
        });
        btnGenres.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                lvGenre.setAdapter(adapter);
                DaLiJeAutor.getInstance().setDaLiJeAutor(false);
                llSearch.setVisibility(View.VISIBLE);
                btnAddGenre.setVisibility(View.VISIBLE);
            }
        });
        btnOnline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                oc.onOnlineClick();
            }
        });
    }

    public interface OnClick{
        public void onAddClick();
        public void onOnlineClick();
        public void onItemClick(String p, boolean b);
    }

}

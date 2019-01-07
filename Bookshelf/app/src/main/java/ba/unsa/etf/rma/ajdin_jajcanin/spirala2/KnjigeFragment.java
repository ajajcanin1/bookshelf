package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class KnjigeFragment extends Fragment {
    CustomArrayAdapter adapter;
    ArrayList<Knjiga> knjige = new ArrayList<Knjiga>();
    ListView lista;
    Button btnPonisti;
    private OnClick oc;

    public KnjigeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_knjige, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BazaOpenHelper baza = new BazaOpenHelper(getContext());

        oc = (OnClick) getActivity();
        lista = (ListView) getView().findViewById(R.id.listaKnjiga);
        Button btnPonisti = (Button) getView().findViewById(R.id.dPovratak);

        if (getActivity().findViewById(R.id.frameSide) != null)
            btnPonisti.setVisibility(View.GONE);
        else
            btnPonisti.setVisibility(View.VISIBLE);

        if (getArguments() != null && getArguments().containsKey("zanr") && getArguments().containsKey("autor")) {
            String zanr = (String) getArguments().getString("zanr");
            String autor = (String) getArguments().getString("autor");

            if(zanr != "")
                knjige = baza.knjigeKategorije(baza.getIdZanr(zanr));
            else
                knjige = baza.knjigeAutora(baza.getIdAutora(autor));


           /* ArrayList<Knjiga> knjigeDelete = new ArrayList<Knjiga>();
            if(knjige.isEmpty()) {
                knjige.addAll(KnjigeHolder.getInstance().getKnjige());

                if (zanr != "")
                    for (Knjiga k : knjige) {
                        if (!k.getZanr().equals(zanr))
                            knjigeDelete.add(k);
                    }
                else
                    for (Knjiga k : knjige) {
                        if (!k.getAutori().stream().anyMatch(x -> x.getImeiPrezime().equalsIgnoreCase(autor)))
                            knjigeDelete.add(k);
                    }

                knjige.removeAll(knjigeDelete);
            }*/
            adapter = new CustomArrayAdapter(getActivity(),
                    R.layout.element_liste, knjige);
            lista.setAdapter(adapter);
        }


        btnPonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oc.onPonistiClick();
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //adapterView.getChildAt(i).setBackgroundResource(R.color.colorSelect);
                //KnjigeHolder.getInstance().getKnjige().get(adapter.index.get(i)).setDaLiJePlav(true);
                adapter.getItem(i).setDaLiJePlav(true);
                adapter.notifyDataSetChanged();
                Knjiga k = adapter.getItem(i);
                if(k.getId().isEmpty())
                    baza.setBackground(baza.findBook(k.getNaziv(), k.getImeAutora()));
                else
                    baza.setBackground(adapter.getItem(i));

            }
        });


    }
    public interface OnClick{
        public void onPonistiClick();
        public void onPreporuciClick(String p);
    }

}

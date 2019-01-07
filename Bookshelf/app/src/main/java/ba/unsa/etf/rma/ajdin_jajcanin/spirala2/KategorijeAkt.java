package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class KategorijeAkt extends AppCompatActivity  implements ListeFragment.OnClick,
    DodavanjeKnjigeFragment.OnClick, KnjigeFragment.OnClick, FragmentOnline.OnClick,
        CustomArrayAdapter.OnClick{

    ArrayList<String> input = new ArrayList<String>();
    ArrayList<Autor> autori = new ArrayList<>();
    Boolean siriL;
    KnjigeFragment kf = new KnjigeFragment();
    KnjigeFragment kfLandScape;
    ListeFragment lf;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_kategorije_akt);
        Bundle arg = new Bundle();
        BazaOpenHelper db = new BazaOpenHelper(this);
        if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 201);
        siriL=false;
        fm = getFragmentManager();
        FrameLayout ldetalji = (FrameLayout)findViewById(R.id.frameSide);
        if(ldetalji != null) {
            siriL = true;
            kf = (KnjigeFragment)fm.findFragmentById(R.id.frameSide);
            if (kf == null) {
                kf = new KnjigeFragment();
                    fm.beginTransaction().replace(R.id.frameSide, kf).commit();
            }
        }
        lf = (ListeFragment)fm.findFragmentByTag("Lista");
        if(lf == null)
        {
            lf=new ListeFragment();
            Bundle arguments = new Bundle();
            arguments.putStringArrayList("zanrovi", input);
            arguments.putParcelableArrayList("autori", autori);
            lf.setArguments(arguments);
            fm.beginTransaction().replace(R.id.frameMain, lf, "Lista").commit();
        } else {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("input", input);
        outState.putParcelableArrayList("autori", autori);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        input = savedInstanceState.getStringArrayList("input");
        autori = savedInstanceState.getParcelableArrayList("autori");
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public void onAddClick() {
        DodavanjeKnjigeFragment dkf = new DodavanjeKnjigeFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList("autori", autori);
        arguments.putStringArrayList("zanroviSpinner", input);
        dkf.setArguments(arguments);
        if(siriL){
            ((FrameLayout)findViewById(R.id.frameMain)).setVisibility(View.GONE);
            ((FrameLayout)findViewById(R.id.frameSide)).setVisibility(View.GONE);
            ((FrameLayout)findViewById(R.id.frameFullScreen)).setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction().replace(R.id.frameFullScreen, dkf)
                    .addToBackStack(null).commit();
        }else{
            getFragmentManager().beginTransaction().replace(R.id.frameMain, dkf)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void onOnlineClick() {
        FragmentOnline fo = new FragmentOnline();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList("autori", autori);
        arguments.putStringArrayList("zanroviSpinner", input);
        fo.setArguments(arguments);
        if(siriL){
            ((FrameLayout)findViewById(R.id.frameMain)).setVisibility(View.GONE);
            ((FrameLayout)findViewById(R.id.frameSide)).setVisibility(View.GONE);
            ((FrameLayout)findViewById(R.id.frameFullScreen)).setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction().replace(R.id.frameFullScreen, fo)
                    .addToBackStack(null).commit();
        }else{
            getFragmentManager().beginTransaction().replace(R.id.frameMain, fo)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void onPonistiClick() {
        if(siriL){
            getFragmentManager().beginTransaction().replace(R.id.frameSide, kf).commit();
            getFragmentManager().beginTransaction().replace(R.id.frameMain, lf).commit();
            ((FrameLayout)findViewById(R.id.frameMain)).setVisibility(View.VISIBLE);
            ((FrameLayout)findViewById(R.id.frameSide)).setVisibility(View.VISIBLE);
            ((FrameLayout)findViewById(R.id.frameFullScreen)).setVisibility(View.GONE);
        }
        fm.popBackStack();
        /*getFragmentManager().beginTransaction().replace(R.id.frameMain, fd)
                .addToBackStack(null).commit();*/
    }

    @Override
    public void onPreporuciClick(String p) {
        FragmentPreporuci fp = new FragmentPreporuci();
        Bundle arguments = new Bundle();
        arguments.putString("idKnjige", p);
        fp.setArguments(arguments);
        if(siriL){
            ((FrameLayout)findViewById(R.id.frameMain)).setVisibility(View.GONE);
            ((FrameLayout)findViewById(R.id.frameSide)).setVisibility(View.GONE);
            ((FrameLayout)findViewById(R.id.frameFullScreen)).setVisibility(View.VISIBLE);
            getFragmentManager().beginTransaction().replace(R.id.frameFullScreen, fp)
                    .addToBackStack(null).commit();
        }else{
            getFragmentManager().beginTransaction().replace(R.id.frameMain, fp)
                    .addToBackStack(null).commit();
        }
    }
    @Override
    public void onItemClick(String p, boolean daLiJeAutor) {
        kfLandScape=(KnjigeFragment) fm.findFragmentById(R.id.frameSide);
        Bundle argument = new Bundle();
        if (DaLiJeAutor.getInstance().isDaLiJeAutor()) {
            argument.putString("autor", p);
            argument.putString("zanr", "");
        } else {
            argument.putString("autor", "");
            argument.putString("zanr", p);
        }

        if(kfLandScape!= null)
            fm.beginTransaction().remove(kfLandScape).commit();

        if (siriL) {
            kfLandScape = new KnjigeFragment();
            kfLandScape.setArguments(argument);
            fm.beginTransaction().replace(R.id.frameSide, kfLandScape)
                    .addToBackStack(null).commit();
        } else {
            kfLandScape = new KnjigeFragment();
            kfLandScape.setArguments(argument);
            fm.beginTransaction().replace(R.id.frameMain, kfLandScape)
                    .addToBackStack(null).commit();
        }
    }
    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0)
            onPonistiClick();
        else finish();
    }
}

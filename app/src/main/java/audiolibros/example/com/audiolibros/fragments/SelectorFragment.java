package audiolibros.example.com.audiolibros.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import audiolibros.example.com.audiolibros.AdaptadorLibros;
import audiolibros.example.com.audiolibros.AdaptadorLibrosFiltro;
import audiolibros.example.com.audiolibros.Aplicacion;
import audiolibros.example.com.audiolibros.Libro;
import audiolibros.example.com.audiolibros.MainActivity;
import audiolibros.example.com.audiolibros.R;

/**
 * Created by William_ST on 05/02/19.
 */

public class SelectorFragment extends Fragment {

    private Activity actividad;
    private RecyclerView recyclerView;
    private AdaptadorLibrosFiltro adaptador;
    private Aplicacion app;

    @Override
    public void onAttach(Activity actividad) {
        super.onAttach(actividad);
        this.actividad = actividad;
        app = (Aplicacion) actividad.getApplication();
        adaptador = app.getAdaptador();
    }

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector, contenedor, false);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(actividad, 2));
        recyclerView.setAdapter(adaptador);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) actividad).mostrarDetalle(
                        // recyclerView.getChildAdapterPosition(v));
                        (int) adaptador.getItemId(recyclerView.getChildAdapterPosition(v)));
            }
        });

        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                if (app == null) return false;

                final int id = recyclerView.getChildAdapterPosition(v);
                AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
                CharSequence[] opciones = {"Compartir", "Borrar ", "Insertar"};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Compartir
                                Libro libro = app.getListaLibros().get(id);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                                i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                                startActivity(Intent.createChooser(i, "Compartir"));
                                break;
                            case 1: //Borrar
                                Snackbar.make(v, "¿Estás seguro?", Snackbar.LENGTH_LONG)
                                        .setAction("SI", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //app.getListaLibros().remove(id);
                                                adaptador.borrar(id);
                                                adaptador.notifyDataSetChanged();
                                            }
                                        })
                                        .show();
                                break;
                            case 2: //Insertar
                                //app.getListaLibros().add(app.getListaLibros().get(id));
                                int posicion = recyclerView.getChildLayoutPosition(v);
                                adaptador.insertar((Libro) adaptador.getItem(posicion));
                                adaptador.notifyDataSetChanged();
                                Snackbar.make(v, "Libro insertado", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });

        return vista;
    }
}
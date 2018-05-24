package com.example.bvarg.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Botones
    Button guardar;
    Button cargarimagen;
    ArrayList uris = new ArrayList();

    //Cuadros de Texto
    EditText nombre;
    EditText precio;
    EditText descripcion;

    //Mostrar Datos
    ListView listView;
    ArrayList listaproductos = new ArrayList();

    //Imagen
    StorageReference mStorage;
    Uri img;
    Uri uri;
    ProgressDialog mProgressDialog;
    public static int GALLERY_INTENT = 1;
    static boolean registrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!registrado){
            Intent intent = new Intent(getApplicationContext(), autenticacion.class);
            startActivity(intent);
        }

        mStorage = FirebaseStorage.getInstance().getReference();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference productos = database.getReference(FirebaseReferences.PRODUCTO_REFERENCE);

        nombre = findViewById(R.id.editText_nombre);
        precio = findViewById(R.id.editText_precio);
        descripcion = findViewById(R.id.editText_descripcion);
        listView = findViewById(R.id.list_view);
        mProgressDialog = new ProgressDialog(this);
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listaproductos);
        listView.setAdapter(adapter);

        cargarimagen = findViewById(R.id.button_cargarimagen);
        cargarimagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        guardar = findViewById(R.id.button_guardar);
        guardar.setEnabled(false);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nombre.getText().toString().equals("") && !precio.getText().toString().equals("") && !descripcion.getText().toString().equals("")){
                    mProgressDialog.setTitle("Cargando...");
                    mProgressDialog.setMessage("Cargando Contenido");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();

                    StorageReference filepath = mStorage.child("fotos").child(uri.getLastPathSegment());

                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Se subio exitosamente", Toast.LENGTH_SHORT).show();
                            img = taskSnapshot.getDownloadUrl();

                            Producto producto = new Producto();
                            producto.nombre = nombre.getText().toString();
                            producto.precio = Float.parseFloat(precio.getText().toString());
                            producto.descripcion = descripcion.getText().toString();
                            producto.imagen = String.valueOf(img);
                            productos.push().setValue(producto);
                            guardar.setEnabled(false);
                            nombre.setText("");
                            precio.setText("");
                            descripcion.setText("");
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "No debe haber ningun dato vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        productos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaproductos.clear();
                uris.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    String total = "Nombre: " + data.child("nombre").getValue().toString()+"   Descripcion: "+data.child("descripcion").getValue().toString()+"   Precio: "+data.child("precio").getValue().toString();
                    uris.add(data.child("imagen").getValue().toString());
                    listaproductos.add(total);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Main2Activity.uri = uris.get(position).toString();
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            uri = data.getData();
            guardar.setEnabled(true);
        }
    }
}

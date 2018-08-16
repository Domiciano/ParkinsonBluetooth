package i2t.icesi.parkinsonbluetooth;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GattCallbackLeft.ReadListener, GattCallbackRight.ReadListener, GattCallbackLeftHigher.ReadListener, GattCallbackRightHigher.ReadListener, EntryDialog.OnDialogDismiss {

    public static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter;

    Button btn_BTON;
    Button iniciar;
    ListView lista_dispositivos;
    ArrayAdapter<String> adaptador;
    ArrayList<String> lista;
    ArrayList<BluetoothDevice> devices;

    Estadisticas realTime;

    AnimationEngine engine;

    boolean firstDato = true;

    Crono h;

    ProgressDialog desconectar;

    boolean recibirDatos = true;

    private int frecuencia;

    //AMPLIAR A 4
    GattCallbackLeft leftCallbackGATT;
    GattCallbackRight rightCallbackGATT;
    GattCallbackLeftHigher leftHigherCallbackGATT;
    GattCallbackRightHigher rightHigherCallbackGATT;

    TextView info_manoizquieda, info_manoderecha;
    Button boton_izquierda, boton_derecha, boton_izquierda_higher, boton_derecha_higher;

    ArrayList<Vector> derecha, izquierda, derecha_higher, izquierda_higher;
    ;

    int tl_anterior, tlh_anterior;
    int tr_anterior, trh_anterior;

    private boolean leftIsConected = false;
    private boolean rightIsConected = false;
    private boolean rightHigherIsConected = false;
    private boolean leftHigherIsConected = false;

    int flhmin = 1000000, flhmax = 0;
    int flmin = 1000000, flmax = 0;
    int frhmin = 1000000, frhmax = 0;
    int frmin = 1000000, frmax = 0;
    int frh = 0, flh = 0;
    int fr = 0, fl = 0;

    int datos_fl = 0;
    int promediol = 0;

    int datos_fr = 0;
    int promedior = 0;

    int datos_flh = 0;
    int promediolh = 0;

    int datos_frh = 0;
    int promediorh = 0;


    Button cambio;

    boolean descarga_activa_left = true;
    boolean descarga_activa_right = true;
    boolean descarga_activa_left_higher = true;
    boolean descarga_activa_right_higher = true;

    FirebaseDatabase db;

    //SOLO GUARDA LA PRUEBA CUANDO ESTÁ LA MANILLA IZQUIERDA CONECTADA Y FUNCIONANDO


    public Drawable getDrawableByAngle(int angle){
        Bitmap rot = BitmapFactory.decodeResource(getResources(),
                R.drawable.change);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedBitmap = Bitmap
                .createBitmap(rot, 0, 0, rot.getWidth(),
                        rot.getHeight(), matrix, true);
        Drawable d = new BitmapDrawable(getResources(), rotatedBitmap);
        return d;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();

        desconectar = new ProgressDialog(this);
        desconectar.setCancelable(false);

        findViewById(R.id.boton_desconectar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desconectar();
            }
        });

        //TODO: MIRAR LA REPRESENTACION CON CUATRO
        cambio = (Button) findViewById(R.id.cambio);
        cambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (engine.manilla == AnimationEngine.IZQUIERDA) {
                    engine.manilla = AnimationEngine.IZQUIERDA_HIGHER;
                    Drawable d = getDrawableByAngle(-45);
                    cambio.setBackground(d);

                }else if (engine.manilla == AnimationEngine.IZQUIERDA_HIGHER) {
                    engine.manilla = AnimationEngine.DERECHA_HIGHER;
                    Drawable d = getDrawableByAngle(-135);
                    cambio.setBackground(d);

                } else if (engine.manilla == AnimationEngine.DERECHA_HIGHER) {
                    engine.manilla = AnimationEngine.DERECHA;
                    Drawable d = getDrawableByAngle(135);
                    cambio.setBackground(d);
                }else if (engine.manilla == AnimationEngine.DERECHA) {
                    engine.manilla = AnimationEngine.IZQUIERDA;
                    Drawable d = getDrawableByAngle(45);
                    cambio.setBackground(d);
                }
            }
        });

        //TODO: INCLUIR LOS OTROS DOS DATASET
        LinearLayout activity_main = (LinearLayout) findViewById(R.id.activity_main);
        engine = new AnimationEngine(this);
        LinearLayout.LayoutParams engine_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        engine.setLayoutParams(engine_params);
        activity_main.addView(engine);

        derecha = new ArrayList<>();
        izquierda = new ArrayList<>();
        izquierda_higher = new ArrayList<>();
        derecha_higher = new ArrayList<>();

        //TODO: INCLUIR DOS CLICK LISTENERS
        boton_izquierda = (Button) findViewById(R.id.boton_izquierda);
        boton_izquierda.setOnClickListener(this);
        boton_derecha = (Button) findViewById(R.id.boton_derecha);
        boton_derecha.setOnClickListener(this);
        boton_izquierda_higher = (Button) findViewById(R.id.boton_izquierda_higher);
        boton_izquierda_higher.setOnClickListener(this);
        boton_derecha_higher = (Button) findViewById(R.id.boton_derecha_higher);
        boton_derecha_higher.setOnClickListener(this);

        devices = new ArrayList<>();

        iniciar = (Button) findViewById(R.id.iniciar);
        iniciar.setOnClickListener(this);

        btn_BTON = (Button) findViewById(R.id.btn_BTON);
        btn_BTON.setOnClickListener(this);

        info_manoizquieda = (TextView) findViewById(R.id.info_manoizquieda);
        info_manoderecha = (TextView) findViewById(R.id.info_manoderecha);


        lista = new ArrayList<>();
        lista_dispositivos = (ListView) findViewById(R.id.lista_dispositivos);
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        lista_dispositivos.setAdapter(adaptador);

        lista_dispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = devices.get(position);
                if (device.getName().contains("LEFT")) {
                    device.connectGatt(MainActivity.this, false, leftCallbackGATT);
                } else if (device.getName().contains("RIGHT")) {
                    device.connectGatt(MainActivity.this, false, rightCallbackGATT);
                } else if (device.getName().contains("LEFT-HIGHER")) {
                    device.connectGatt(MainActivity.this, false, leftHigherCallbackGATT);
                } else if (device.getName().contains("RIGHT-HIGHER")) {
                    device.connectGatt(MainActivity.this, false, rightHigherCallbackGATT);
                } else {
                    Toast.makeText(MainActivity.this, "El dispositivo no es una manilla", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Su dispositivo NO es compatible con Bluetooth", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Su dispositivo NO es compatible con Smart Bluetooth", Toast.LENGTH_LONG).show();
            return;
        }

        engine.manilla = AnimationEngine.IZQUIERDA;
        Drawable d = getDrawableByAngle(45);
        cambio.setBackground(d);

        //PERMISOS DE ALMACENAMIENTO
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            File carpeta = new File(Environment.getExternalStorageDirectory() + "/" + "Parkinson/");
            if (!carpeta.exists()) carpeta.mkdirs();
        }
    }

    private void inicializarSistema() {
        File carpeta = new File(Environment.getExternalStorageDirectory() + "/" + "Parkinson/");
        if (!carpeta.exists()) carpeta.mkdirs();


        boton_izquierda.setBackgroundResource(R.drawable.conectar);
        boton_derecha.setBackgroundResource(R.drawable.conectar);
        boton_izquierda_higher.setBackgroundResource(R.drawable.conectar);
        boton_derecha_higher.setBackgroundResource(R.drawable.conectar);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        lista.clear();
        devices.clear();
        for (BluetoothDevice device : pairedDevices) {
            devices.add(device);
            lista.add(device.getName() + "\n" + device.getAddress());
        }
        adaptador.notifyDataSetChanged();

        //TODO: VERIFICAR LOS NOMBRES
        if (containsSomeStringStartsWith(lista, "LEFT-HAND")) {
            boton_izquierda.setEnabled(true);
        }
        if (containsSomeStringStartsWith(lista, "RIGHT-HAND")) {
            boton_derecha.setEnabled(true);
        }
        if (containsSomeStringStartsWith(lista, "LEFT-HIGHER")) {
            boton_izquierda_higher.setEnabled(true);
        }
        if (containsSomeStringStartsWith(lista, "RIGHT-HIGHER")) {
            boton_derecha_higher.setEnabled(true);
        }
    }

    //TODO: HACER PARA LAS DOS MANILLAS
    @Override
    public void onClick(View v) {
        if (v.equals(btn_BTON)) {
            requestEncenderBT();
        } else if (v.equals(iniciar)) {
            if (iniciar.getText().toString().equalsIgnoreCase("INICIAR")) {

                derecha.clear();
                izquierda.clear();
                derecha_higher.clear();
                izquierda_higher.clear();
                firstDato = true;
                h = new Crono();
                h.start();

                if (leftCallbackGATT != null) leftCallbackGATT.enviar("P");
                if (rightCallbackGATT != null) rightCallbackGATT.enviar("P");
                if (leftHigherCallbackGATT != null) leftHigherCallbackGATT.enviar("P");
                if (rightHigherCallbackGATT != null) rightHigherCallbackGATT.enviar("P");
                iniciar.setText("TERMINAR PRUEBA");
            } else if (iniciar.getText().toString().equalsIgnoreCase("TERMINAR PRUEBA")) {

                if (leftCallbackGATT != null) leftCallbackGATT.enviar("S");
                if (rightCallbackGATT != null) rightCallbackGATT.enviar("S");
                if (leftHigherCallbackGATT != null) leftHigherCallbackGATT.enviar("S");
                if (rightHigherCallbackGATT != null) rightHigherCallbackGATT.enviar("S");
                iniciar.setText("DESCARGAR PRUEBA");
                h.vivo = false;
                recibirDatos = false;
            } else if (iniciar.getText().toString().equalsIgnoreCase("DESCARGAR PRUEBA")) {
                descarga_activa_left = true;
                descarga_activa_right = true;
                descarga_activa_left_higher = true;
                descarga_activa_right_higher = true;
                if (leftCallbackGATT != null) leftCallbackGATT.enviar("B");
                if (rightCallbackGATT != null) rightCallbackGATT.enviar("B");
                if (leftHigherCallbackGATT != null) leftHigherCallbackGATT.enviar("B");
                if (rightHigherCallbackGATT != null) rightHigherCallbackGATT.enviar("B");
                iniciar.setEnabled(false);
                h.vivo = false;
            }
        } else if (v.equals(boton_derecha)) {
            connect("RIGHT-HAND");
        } else if (v.equals(boton_izquierda)) {
            connect("LEFT-HAND");
        } else if (v.equals(boton_derecha_higher)) {
            connect("RIGHT-HIGHER");
        } else if (v.equals(boton_izquierda_higher)) {
            connect("LEFT-HIGHER");
        } else if (v.equals(findViewById(R.id.modolive_btn))) {
            Intent i = new Intent(this, ModoLive.class);
            startActivity(i);
            finish();
        } else if (v.equals(findViewById(R.id.cedula))) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            EntryDialog dialog = EntryDialog.newInstance("Escribe la cedula del paciente",
                    PreferenceManager.getDefaultSharedPreferences(this).getString("cedula", "")
                    , "OK");
            dialog.setOnDialogDismiss(this);
            dialog.show(ft, "dialog_fiebre");
        }
    }

    //LISTO PARA LOS DOS DISPOSITIVIOS
    private void desconectar() {
        if (true) {
            //Toast.makeText(this, "Desconectando...", Toast.LENGTH_SHORT).show();
            desconectar.setMessage("Desconectando...");
            desconectar.show();
            Thread h1 = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 20; i++) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i % 2 == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (rightCallbackGATT != null) {
                                        rightCallbackGATT.close();
                                    }
                                }
                            });

                        } else if (i % 2 == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (leftCallbackGATT != null) {
                                        leftCallbackGATT.close();
                                    }
                                }
                            });
                        } else if (i % 2 == 2) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (leftHigherCallbackGATT != null) {
                                        leftHigherCallbackGATT.close();
                                    }
                                }
                            });
                        } else if (i % 2 == 3) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (rightHigherCallbackGATT != null) {
                                        rightHigherCallbackGATT.close();
                                    }
                                }
                            });
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inicializarSistema();
                            Toast.makeText(MainActivity.this, "Las manillas han sido desconectadas", Toast.LENGTH_SHORT).show();
                            leftIsConected = false;
                            rightIsConected = false;
                            leftHigherIsConected = false;
                            rightHigherIsConected = false;
                            desconectar.dismiss();
                        }
                    });

                }
            };
            h1.start();
        }
    }

    //LISTO PARA LOS DOS DISPOSITIVIOS
    private void connect(String name) {
        BluetoothDevice device = null;
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getName().equals(name)) {
                device = devices.get(i);
                break;
            }
        }
        if (device == null) {
            Toast.makeText(this, "Asegurese que el dispositivo está conectado", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (name.contains("LEFT-HAND")) {
                leftCallbackGATT = new GattCallbackLeft();
                leftCallbackGATT.setReadListener(this);
                device.connectGatt(MainActivity.this, false, leftCallbackGATT);
            } else if (name.contains("RIGHT-HAND")) {
                rightCallbackGATT = new GattCallbackRight();
                rightCallbackGATT.setReadListener(this);
                device.connectGatt(MainActivity.this, false, rightCallbackGATT);
            } else if (name.contains("LEFT-HIGHER")) {
                leftHigherCallbackGATT = new GattCallbackLeftHigher();
                leftHigherCallbackGATT.setReadListener(this);
                device.connectGatt(MainActivity.this, false, leftHigherCallbackGATT);
            } else if (name.contains("RIGHT-HIGHER")) {
                rightHigherCallbackGATT = new GattCallbackRightHigher();
                rightHigherCallbackGATT.setReadListener(this);
                device.connectGatt(MainActivity.this, false, rightHigherCallbackGATT);
            }
        }
    }

    //LISTO PARA LOS DOS DISPOSITIVIOS
    private void requestEncenderBT() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    //LISTO PARA LOS DOS DISPOSITIVIOS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth habilitado!", Toast.LENGTH_LONG).show();
                btn_BTON.setEnabled(false);
                inicializarSistema();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth NO fue habilitado!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void guardarPrueba() {
        try {
            Gson g = new Gson();
            ArrayList<Vector> out_derecha = new ArrayList<>();
            out_derecha.addAll(Arrays.asList(new Vector[derecha.size()]));
            Collections.copy(out_derecha, derecha);

            ArrayList<Vector> out_izquierda = new ArrayList<>();
            out_izquierda.addAll(Arrays.asList(new Vector[izquierda.size()]));
            Collections.copy(out_izquierda, izquierda);

            ArrayList<Vector> out_izquierda_higher = new ArrayList<>();
            out_izquierda_higher.addAll(Arrays.asList(new Vector[izquierda_higher.size()]));
            Collections.copy(out_izquierda_higher, izquierda_higher);

            ArrayList<Vector> out_derecha_higher = new ArrayList<>();
            out_derecha_higher.addAll(Arrays.asList(new Vector[derecha_higher.size()]));
            Collections.copy(out_derecha_higher, derecha_higher);

            String json_derecha = g.toJson(out_derecha);
            String json_izquierda = g.toJson(out_izquierda);
            String json_derecha_higher = g.toJson(out_derecha_higher);
            String json_izquierda_higher = g.toJson(out_izquierda_higher);

            String cedula = PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getString("cedula", "NO_CEDULA");



            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minuto = Calendar.getInstance().get(Calendar.MINUTE);
            int segundo = Calendar.getInstance().get(Calendar.SECOND);
            String nombre = PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getString("cedula", "NO_CEDULA") +
                    "" + year + "-" + month + "-" + day + "-" + hora + "-" + minuto + "-" + segundo;


            DatabaseReference ref = db.getReference()
                    .child(cedula)
                    .child("pruebas")
                    .child("" + year + "-" + month + "-" + day + "-" + hora + "-" + minuto + "-" + segundo);

            ref.child("derecha").setValue(out_derecha);
            ref.child("izquierda").setValue(out_izquierda);
            ref.child("derecha_higher").setValue(out_derecha_higher);
            ref.child("izquierda_higher").setValue(out_izquierda_higher);


            File carpeta = new File(Environment.getExternalStorageDirectory() + "/" + "Parkinson/Precision/" + nombre + "/");
            if (!carpeta.exists()) carpeta.mkdirs();

            File archivo_derecha = new File(carpeta.toString() + "/derecha-" + nombre + ".json");
            FileOutputStream fosder = new FileOutputStream(archivo_derecha);
            fosder.write(json_derecha.getBytes());
            fosder.close();

            File archivo_izquierda = new File(carpeta.toString() + "/izquierda-" + nombre + ".json");
            FileOutputStream fosizq = new FileOutputStream(archivo_izquierda);
            fosizq.write(json_izquierda.getBytes());
            fosizq.close();

            File archivo_derecha_superior = new File(carpeta.toString() + "/derecha_superior-" + nombre + ".json");
            FileOutputStream fosderh = new FileOutputStream(archivo_derecha_superior);
            fosderh.write(json_derecha_higher.getBytes());
            fosderh.close();

            File archivo_izquierda_superior = new File(carpeta.toString() + "/izquierda_superior-" + nombre + ".json");
            FileOutputStream fosizqh = new FileOutputStream(archivo_izquierda_superior);
            fosizqh.write(json_izquierda_higher.getBytes());
            fosizqh.close();
            //2. Inicializar Datos
            inicializarDatos();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inicializarDatos() {
        tl_anterior = 0;
        tr_anterior = 0;
        tlh_anterior = 0;
        trh_anterior = 0;

        flmin = 1000000;
        frmin = 1000000;
        flhmin = 1000000;
        frhmin = 1000000;

        flmax = 0;
        frmax = 0;
        flhmax = 0;
        frhmax = 0;

        firstDato = true;

        datos_fl = 0;
        promediol = 0;
        datos_fr = 0;
        promedior = 0;

        datos_flh = 0;
        promediolh = 0;
        datos_frh = 0;
        promediorh = 0;

        izquierda.clear();
        derecha.clear();
        izquierda_higher.clear();
        derecha_higher.clear();

        //Tumbar hilo Estadisticas
        if (realTime != null) realTime.live = false;
        //Mantener hilo de dibujado
    }

    @Override
    protected void onPause() {
        engine.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        engine.resume();
    }

    @Override
    protected void onStop() {
        if (realTime != null) realTime.live = false;
        if (h != null) h.vivo = false;
        super.onStop();
    }

    @Override
    public void finish(EntryDialog dialog, String sintoma) {
        dialog.dismiss();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString("cedula", sintoma).apply();
    }

    //TODO: INCLUIR PARA LOS 4
    public class Estadisticas extends Thread {
        boolean live = true;

        @Override
        public void run() {
            try {
                while (live) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String alfa = "", beta = "", gamma = "", delta = "";
                            if (izquierda_higher.size() > 4) {
                                alfa = "Tiempo superior: " + izquierda_higher.get(izquierda_higher.size() - 2).t + "s" +
                                        "\nFrecuencia superior: " + promediolh + "Hz\n";
                            }
                            if (izquierda.size() > 4) {
                                beta = "Tiempo inferior: " + izquierda.get(izquierda.size() - 2).t + "s" +
                                        "\nFrecuencia inferior: " + promediol  + "Hz";
                            }
                            info_manoizquieda.setText(alfa+beta);

                            if (derecha_higher.size() > 4) {
                                gamma = "Tiempo superior: " + derecha_higher.get(derecha_higher.size() - 2).t + "s" +
                                        "\nFrecuencia superior: " + promediorh + "Hz\n";
                            }
                            if (derecha.size() > 4) {
                                delta = "Tiempo inferior: " + derecha.get(derecha.size() - 2).t + "s" +
                                        "\nFrecuencia inferior: " + promedior  + "Hz";
                            }
                            info_manoderecha.setText(gamma+delta);
                        }
                    });
                    medirFrecuencia();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void medirFrecuencia() {
            if (izquierda.size() >= 8) {
                int delta_t = izquierda.get(izquierda.size() - 4).t - izquierda.get(izquierda.size() - 5).t;
                int frecuencia = (int) (1000 / (float) delta_t);
                flmin = Math.min(flmin, frecuencia);
                flmax = Math.max(flmax, frecuencia);
                fl = frecuencia;
                datos_fl++;
                promediol = (int) Math.round(frecuencia * (1 / (double) datos_fl) + promediol * ((datos_fl - 1) / (double) datos_fl));
                Log.e(">>>", "izquierda: " + delta_t);
            }
            if (derecha.size() >= 8) {
                int delta_t = derecha.get(derecha.size() - 4).t - derecha.get(derecha.size() - 5).t;
                int frecuencia = (int) (1000 / (float) delta_t);
                frmin = Math.min(frmin, frecuencia);
                frmax = Math.max(frmax, frecuencia);
                fr = frecuencia;
                datos_fr++;
                promedior = (int) Math.round(frecuencia * (1 / (double) datos_fr) + promedior * ((datos_fr - 1) / (double) datos_fr));
                Log.e(">>>", "derecha: " + delta_t);
            }
            if (izquierda_higher.size() >= 8) {
                int delta_t = izquierda_higher.get(izquierda_higher.size() - 4).t - izquierda_higher.get(izquierda_higher.size() - 5).t;
                int frecuencia = (int) (1000 / (float) delta_t);
                flhmin = Math.min(flhmin, frecuencia);
                flhmax = Math.max(flhmax, frecuencia);
                flh = frecuencia;
                datos_flh++;
                promediolh = (int) Math.round(frecuencia * (1 / (double) datos_flh) + promediolh * ((datos_flh - 1) / (double) datos_flh));
                Log.e(">>>", "izquierda superior: " + delta_t);
            }
            if (derecha_higher.size() >= 8) {
                int delta_t = derecha_higher.get(derecha_higher.size() - 4).t - derecha_higher.get(derecha_higher.size() - 5).t;
                int frecuencia = (int) (1000 / (float) delta_t);
                frhmin = Math.min(frhmin, frecuencia);
                frhmax = Math.max(frhmax, frecuencia);
                frh = frecuencia;
                datos_frh++;
                promediorh = (int) Math.round(frecuencia * (1 / (double) datos_frh) + promediorh * ((datos_frh - 1) / (double) datos_frh));
                Log.e(">>>", "derecha superior: " + delta_t);
            }
        }

    }

    //LISTO PARA AMBOS
    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Por favor encienda el Bluetooth", Toast.LENGTH_LONG).show();
            btn_BTON.setEnabled(true);
        } else {
            inicializarSistema();
            btn_BTON.setEnabled(false);
        }
    }

    //UTILS

    public boolean containsSomeStringStartsWith(ArrayList<String> lista, String busqueda) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).startsWith(busqueda)) return true;
        }
        return false;
    }


    private int diferencia(int a, int b) {
        if (a < b) {
            int c = (256 - b) + a;
            return c;
        } else {
            int c = a - b;
            return c;
        }
    }


    //------------>


    //------------->


    public class Crono extends Thread {
        boolean vivo = true;
        int segundos = 0;

        @Override
        public void run() {
            while (vivo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.info)).setText("Transcurrido: " + segundos + " segundos");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                segundos++;

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.info)).setText("No olvide vincular las manillas izquierda y derecha");
                }
            });
        }
    }


    //TODO: COPIAR METODO PARA LOS 4
    //LEFT
    @Override
    public void onDataReceivedLeft(GattCallbackLeft obj, byte[] data) {
        String recibido = new String(data).trim();
        if (recibido.contains("FINAL") && descarga_activa_left) {
            descarga_activa_left = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        realTime.live = false;
                        Thread.sleep(1000);
                        guardarPrueba();
                        Toast.makeText(MainActivity.this, "Descarga finalizada", Toast.LENGTH_SHORT).show();
                        iniciar.setText("INICIAR");
                        iniciar.setEnabled(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            return;
        } else if (recibido.contains("FINAL") && !descarga_activa_left) {
            return;
        }

        if (firstDato == true) {
            firstDato = false;
            realTime = new Estadisticas();
            realTime.start();
        }

        int tiempo0 = (data[4] & 0xFF);
        int medida0 = ((data[0] & 0x3F) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        int X0 = (medida0 & 0x3FF00000) >> 20;
        int Y0 = (medida0 & 0xFFC00) >> 10;
        int Z0 = (medida0 & 0x3FF);

        int tiempo1 = (data[9] & 0xFF);
        int medida1 = ((data[5] & 0x3F) << 24) | ((data[6] & 0xFF) << 16) | ((data[7] & 0xFF) << 8) | (data[8] & 0xFF);
        int X1 = (medida1 & 0x3FF00000) >> 20;
        int Y1 = (medida1 & 0xFFC00) >> 10;
        int Z1 = (medida1 & 0x3FF);

        int tiempo2 = (data[14] & 0xFF);
        int medida2 = ((data[10] & 0x3F) << 24) | ((data[11] & 0xFF) << 16) | ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);
        int X2 = (medida2 & 0x3FF00000) >> 20;
        int Y2 = (medida2 & 0xFFC00) >> 10;
        int Z2 = (medida2 & 0x3FF);

        int tiempo3 = (data[19] & 0xFF);
        int medida3 = ((data[15] & 0x3F) << 24) | ((data[16] & 0xFF) << 16) | ((data[17] & 0xFF) << 8) | (data[18] & 0xFF);
        int X3 = (medida3 & 0x3FF00000) >> 20;
        int Y3 = (medida3 & 0xFFC00) >> 10;
        int Z3 = (medida3 & 0x3FF);

        Vector a = new Vector(tiempo0, Z0, Y0, X0);
        Vector b = new Vector(tiempo1, Z1, Y1, X1);
        Vector c = new Vector(tiempo2, Z2, Y2, X2);
        Vector d = new Vector(tiempo3, Z3, Y3, X3);


        if (izquierda.size() == 0) {
            a.t = 0;
            tl_anterior = a.t;
            izquierda.add(a);
        } else {
            a.t = tl_anterior + diferencia(a.millis, izquierda.get(izquierda.size() - 1).millis);
            tl_anterior = a.t;
            izquierda.add(a);
        }

        b.t = tl_anterior + diferencia(b.millis, izquierda.get(izquierda.size() - 1).millis);
        tl_anterior = b.t;
        izquierda.add(b);

        c.t = tl_anterior + diferencia(c.millis, izquierda.get(izquierda.size() - 1).millis);
        tl_anterior = c.t;
        izquierda.add(c);

        d.t = tl_anterior + diferencia(d.millis, izquierda.get(izquierda.size() - 1).millis);
        tl_anterior = d.t;
        izquierda.add(d);

    }

    @Override
    public void onConnectedLeft(final GattCallbackLeft gatt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iniciar.setEnabled(true);
                leftIsConected = true;
                boton_izquierda.setEnabled(false);
                boton_izquierda.setBackgroundResource(R.drawable.conectado);
                Toast.makeText(MainActivity.this, "Manilla izquierda conectada", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDisconectedLeft(final GattCallbackLeft gatt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftIsConected = false;
                boton_izquierda.setBackgroundResource(R.drawable.conectar);
                if (!leftIsConected && !rightIsConected) {
                    iniciar.setEnabled(false);
                }
            }
        });

    }

    //RIGHT
    @Override
    public void onDataReceivedRight(GattCallbackRight obj, byte[] data) {
        String recibido = new String(data).trim();
        if (recibido.contains("FINAL") && descarga_activa_right) {
            descarga_activa_right = false;
            realTime.live = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iniciar.setText("INICIAR");
                    iniciar.setEnabled(true);
                }
            });
            return;
        } else if (recibido.contains("FINAL") && !descarga_activa_right) {
            return;
        }

        if (firstDato == true) {
            realTime = new Estadisticas();
            realTime.start();
            firstDato = false;
        }

        int tiempo0 = (data[4] & 0xFF);
        int medida0 = ((data[0] & 0x3F) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        int X0 = (medida0 & 0x3FF00000) >> 20;
        int Y0 = (medida0 & 0xFFC00) >> 10;
        int Z0 = (medida0 & 0x3FF);

        int tiempo1 = (data[9] & 0xFF);
        int medida1 = ((data[5] & 0x3F) << 24) | ((data[6] & 0xFF) << 16) | ((data[7] & 0xFF) << 8) | (data[8] & 0xFF);
        int X1 = (medida1 & 0x3FF00000) >> 20;
        int Y1 = (medida1 & 0xFFC00) >> 10;
        int Z1 = (medida1 & 0x3FF);

        int tiempo2 = (data[14] & 0xFF);
        int medida2 = ((data[10] & 0x3F) << 24) | ((data[11] & 0xFF) << 16) | ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);
        int X2 = (medida2 & 0x3FF00000) >> 20;
        int Y2 = (medida2 & 0xFFC00) >> 10;
        int Z2 = (medida2 & 0x3FF);

        int tiempo3 = (data[19] & 0xFF);
        int medida3 = ((data[15] & 0x3F) << 24) | ((data[16] & 0xFF) << 16) | ((data[17] & 0xFF) << 8) | (data[18] & 0xFF);
        int X3 = (medida3 & 0x3FF00000) >> 20;
        int Y3 = (medida3 & 0xFFC00) >> 10;
        int Z3 = (medida3 & 0x3FF);

        Vector a = new Vector(tiempo0, Z0, Y0, X0);
        Vector b = new Vector(tiempo1, Z1, Y1, X1);
        Vector c = new Vector(tiempo2, Z2, Y2, X2);
        Vector d = new Vector(tiempo3, Z3, Y3, X3);

        if (derecha.size() == 0) {
            a.t = 0;
            tr_anterior = a.t;
            derecha.add(a);
        } else {
            a.t = tr_anterior + diferencia(a.millis, derecha.get(derecha.size() - 1).millis);
            tr_anterior = a.t;
            derecha.add(a);
        }

        b.t = tr_anterior + diferencia(b.millis, derecha.get(derecha.size() - 1).millis);
        tr_anterior = b.t;
        derecha.add(b);

        c.t = tr_anterior + diferencia(c.millis, derecha.get(derecha.size() - 1).millis);
        tr_anterior = c.t;
        derecha.add(c);

        d.t = tr_anterior + diferencia(d.millis, derecha.get(derecha.size() - 1).millis);
        tr_anterior = d.t;
        derecha.add(d);

    }

    @Override
    public void onConnectedRight(GattCallbackRight obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iniciar.setEnabled(true);
                rightIsConected = true;
                boton_derecha.setEnabled(false);
                boton_derecha.setBackgroundResource(R.drawable.conectado);
                Toast.makeText(MainActivity.this, "Manilla derecha conectada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDisconectedRight(GattCallbackRight gatt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rightIsConected = false;
                boton_derecha.setBackgroundResource(R.drawable.conectar);
                if (!leftIsConected && !rightIsConected) {
                    iniciar.setEnabled(false);
                }
            }
        });
    }

    //LEFT HIGHER
    @Override
    public void onDataReceivedLeftHigher(GattCallbackLeftHigher obj, byte[] data) {
        String recibido = new String(data).trim();
        if (recibido.contains("FINAL") && descarga_activa_left_higher) {
            descarga_activa_left_higher = false;
            realTime.live = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iniciar.setText("INICIAR");
                    iniciar.setEnabled(true);
                }
            });
            return;
        } else if (recibido.contains("FINAL") && !descarga_activa_left_higher) {
            return;
        }

        if (firstDato == true) {
            realTime = new Estadisticas();
            realTime.start();
            firstDato = false;
        }

        int tiempo0 = (data[4] & 0xFF);
        int medida0 = ((data[0] & 0x3F) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        int X0 = (medida0 & 0x3FF00000) >> 20;
        int Y0 = (medida0 & 0xFFC00) >> 10;
        int Z0 = (medida0 & 0x3FF);

        int tiempo1 = (data[9] & 0xFF);
        int medida1 = ((data[5] & 0x3F) << 24) | ((data[6] & 0xFF) << 16) | ((data[7] & 0xFF) << 8) | (data[8] & 0xFF);
        int X1 = (medida1 & 0x3FF00000) >> 20;
        int Y1 = (medida1 & 0xFFC00) >> 10;
        int Z1 = (medida1 & 0x3FF);

        int tiempo2 = (data[14] & 0xFF);
        int medida2 = ((data[10] & 0x3F) << 24) | ((data[11] & 0xFF) << 16) | ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);
        int X2 = (medida2 & 0x3FF00000) >> 20;
        int Y2 = (medida2 & 0xFFC00) >> 10;
        int Z2 = (medida2 & 0x3FF);

        int tiempo3 = (data[19] & 0xFF);
        int medida3 = ((data[15] & 0x3F) << 24) | ((data[16] & 0xFF) << 16) | ((data[17] & 0xFF) << 8) | (data[18] & 0xFF);
        int X3 = (medida3 & 0x3FF00000) >> 20;
        int Y3 = (medida3 & 0xFFC00) >> 10;
        int Z3 = (medida3 & 0x3FF);

        Vector a = new Vector(tiempo0, Z0, Y0, X0);
        Vector b = new Vector(tiempo1, Z1, Y1, X1);
        Vector c = new Vector(tiempo2, Z2, Y2, X2);
        Vector d = new Vector(tiempo3, Z3, Y3, X3);

        if (izquierda_higher.size() == 0) {
            a.t = 0;
            tlh_anterior = a.t;
            izquierda_higher.add(a);
        } else {
            a.t = tlh_anterior + diferencia(a.millis, izquierda_higher.get(izquierda_higher.size() - 1).millis);
            tlh_anterior = a.t;
            izquierda_higher.add(a);
        }

        b.t = tlh_anterior + diferencia(b.millis, izquierda_higher.get(izquierda_higher.size() - 1).millis);
        tlh_anterior = b.t;
        izquierda_higher.add(b);

        c.t = tlh_anterior + diferencia(c.millis, izquierda_higher.get(izquierda_higher.size() - 1).millis);
        tlh_anterior = c.t;
        izquierda_higher.add(c);

        d.t = tlh_anterior + diferencia(d.millis, izquierda_higher.get(izquierda_higher.size() - 1).millis);
        tlh_anterior = d.t;
        izquierda_higher.add(d);

    }

    @Override
    public void onConnectedLeftHigher(GattCallbackLeftHigher obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iniciar.setEnabled(true);
                leftHigherIsConected = true;
                boton_izquierda_higher.setEnabled(false);
                boton_izquierda_higher.setBackgroundResource(R.drawable.conectado);
                Toast.makeText(MainActivity.this, "Manilla derecha conectada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDisconectedLeftHigher(GattCallbackLeftHigher gatt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                leftHigherIsConected = false;
                boton_izquierda_higher.setBackgroundResource(R.drawable.conectar);
                if (!leftIsConected && !rightIsConected && !leftHigherIsConected && !rightHigherIsConected) {
                    iniciar.setEnabled(false);
                }
            }
        });
    }

    //RIGHT HIGHER
    @Override
    public void onDataReceivedRightHigher(GattCallbackRightHigher obj, byte[] data) {
        String recibido = new String(data).trim();
        if (recibido.contains("FINAL") && descarga_activa_right_higher) {
            descarga_activa_right_higher = false;
            realTime.live = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iniciar.setText("INICIAR");
                    iniciar.setEnabled(true);
                }
            });
            return;
        } else if (recibido.contains("FINAL") && !descarga_activa_right_higher) {
            return;
        }

        if (firstDato == true) {
            realTime = new Estadisticas();
            realTime.start();
            firstDato = false;
        }

        int tiempo0 = (data[4] & 0xFF);
        int medida0 = ((data[0] & 0x3F) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        int X0 = (medida0 & 0x3FF00000) >> 20;
        int Y0 = (medida0 & 0xFFC00) >> 10;
        int Z0 = (medida0 & 0x3FF);

        int tiempo1 = (data[9] & 0xFF);
        int medida1 = ((data[5] & 0x3F) << 24) | ((data[6] & 0xFF) << 16) | ((data[7] & 0xFF) << 8) | (data[8] & 0xFF);
        int X1 = (medida1 & 0x3FF00000) >> 20;
        int Y1 = (medida1 & 0xFFC00) >> 10;
        int Z1 = (medida1 & 0x3FF);

        int tiempo2 = (data[14] & 0xFF);
        int medida2 = ((data[10] & 0x3F) << 24) | ((data[11] & 0xFF) << 16) | ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);
        int X2 = (medida2 & 0x3FF00000) >> 20;
        int Y2 = (medida2 & 0xFFC00) >> 10;
        int Z2 = (medida2 & 0x3FF);

        int tiempo3 = (data[19] & 0xFF);
        int medida3 = ((data[15] & 0x3F) << 24) | ((data[16] & 0xFF) << 16) | ((data[17] & 0xFF) << 8) | (data[18] & 0xFF);
        int X3 = (medida3 & 0x3FF00000) >> 20;
        int Y3 = (medida3 & 0xFFC00) >> 10;
        int Z3 = (medida3 & 0x3FF);

        Vector a = new Vector(tiempo0, Z0, Y0, X0);
        Vector b = new Vector(tiempo1, Z1, Y1, X1);
        Vector c = new Vector(tiempo2, Z2, Y2, X2);
        Vector d = new Vector(tiempo3, Z3, Y3, X3);

        if (derecha_higher.size() == 0) {
            a.t = 0;
            trh_anterior = a.t;
            derecha_higher.add(a);
        } else {
            a.t = trh_anterior + diferencia(a.millis, derecha_higher.get(derecha_higher.size() - 1).millis);
            trh_anterior = a.t;
            derecha_higher.add(a);
        }

        b.t = trh_anterior + diferencia(b.millis, derecha_higher.get(derecha_higher.size() - 1).millis);
        trh_anterior = b.t;
        derecha_higher.add(b);

        c.t = trh_anterior + diferencia(c.millis, derecha_higher.get(derecha_higher.size() - 1).millis);
        trh_anterior = c.t;
        derecha_higher.add(c);

        d.t = trh_anterior + diferencia(d.millis, derecha_higher.get(derecha_higher.size() - 1).millis);
        trh_anterior = d.t;
        derecha_higher.add(d);
    }

    @Override
    public void onConnectedRightHigher(GattCallbackRightHigher obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iniciar.setEnabled(true);
                rightHigherIsConected = true;
                boton_derecha_higher.setEnabled(false);
                boton_derecha_higher.setBackgroundResource(R.drawable.conectado);
                Toast.makeText(MainActivity.this, "Manilla derecha conectada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDisconectedRightHigher(GattCallbackRightHigher gatt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rightHigherIsConected = false;
                boton_derecha_higher.setBackgroundResource(R.drawable.conectar);
                if (!leftIsConected && !rightIsConected && !leftHigherIsConected && !rightHigherIsConected) {
                    iniciar.setEnabled(false);
                }
            }
        });
    }
}

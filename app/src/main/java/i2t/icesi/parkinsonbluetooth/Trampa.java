package i2t.icesi.parkinsonbluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Trampa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trampa);
        Intent i = new Intent(this, ModoLive.class);
        startActivity(i);
        finish();
    }
}

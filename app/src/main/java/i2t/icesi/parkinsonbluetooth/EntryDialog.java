package i2t.icesi.parkinsonbluetooth;

/**
 * Created by Domiciano on 24/05/2016.
 */

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Domiciano on 17/05/2016.
 */
public class EntryDialog extends DialogFragment implements View.OnClickListener{

    OnDialogDismiss onDialogDismiss;
    Button ok;
    EditText entry;
    TextView entry_textview;

    String texto, confirmacion;
    String cedula;

    public static EntryDialog newInstance(String texto, String cedula, String confirmacion) {
        EntryDialog f = new EntryDialog();

        Bundle args = new Bundle();
        args.putString("texto", texto);
        args.putString("cedula", cedula);
        args.putString("confirmacion", confirmacion);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.texto = getArguments().getString("texto");
        this.cedula = getArguments().getString("cedula");
        this.confirmacion = getArguments().getString("confirmacion");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        d.getWindow().setLayout((int)(screenWidth*0.8), 650);

        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return d;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_entry, container, false);

        entry_textview = (TextView) v.findViewById(R.id.entry_textview);
        entry_textview.setText(this.texto);

        ok = (Button) v.findViewById(R.id.ok_dialog_entry);
        ok.setText(this.confirmacion);
        ok.setOnClickListener(this);

        entry = (EditText) v.findViewById(R.id.et_dialog_entry);
        entry.setText(cedula);


        return v;
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        if(b.equals(ok)){
            onDialogDismiss.finish(this, entry.getText().toString());
        }
    }

    public interface OnDialogDismiss{
        void finish(EntryDialog dialog, String sintoma);
    }

    public void setOnDialogDismiss(OnDialogDismiss onDialogDismiss){
        this.onDialogDismiss = onDialogDismiss;
    }
}


package ia.agafam;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;


public class MainActivity extends GeneralActivity {
    private Joc joc;
	private SharedPreferences valorsGuardats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();

        // Pantalla completa

        hideSystemUI();

    	valorsGuardats = getSharedPreferences(PREFERENCIES, MainActivity.MODE_PRIVATE);
        setContentView(R.layout.joc);
    }

    @Override
    public void onStart() {
    	super.onStart();

       FrameLayout pantalla = (FrameLayout) findViewById(R.id.layoutJoc);
        // ara posam per programa la zona de dibuix

        joc = new Joc(this);
        pantalla.addView(joc);   
    }     
    
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {  
            joc.setOrientation(Configuration.ORIENTATION_LANDSCAPE);  
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {  
        	joc.setOrientation(Configuration.ORIENTATION_PORTRAIT); 
        }
        joc.computeMap();
    }
    
    
    protected void onResume()
    {
    	super.onResume();
    }
    
    protected void onRestart()
    {
    	super.onRestart();
    }
    
    protected void onPause()
    {
    	super.onPause();
    	joc.aturar();
    }
    
    protected void onStop()
    {
    	super.onStop();
    	joc.aturar();
    	unbindDrawables(findViewById(R.id.layoutJoc));   	
    }

    // allibera memòria
    private void unbindDrawables(View view)
    {
    	if (view.getBackground() != null)
    	{
    		view.getBackground().setCallback(null);
    	}
    	if (view instanceof ViewGroup && !(view instanceof AdapterView))
    	{
    		for (int i=0; i<((ViewGroup) view).getChildCount();i++)
    		{
    			unbindDrawables(((ViewGroup) view).getChildAt(i));
    		}
    		((ViewGroup) view).removeAllViews();
    	}
    }
    
    protected void onDestroy()
    {
    	super.onDestroy();
    	joc.aturar();
    	unbindDrawables(findViewById(R.id.layoutJoc));
    	System.gc();
    }    

    // Gestió de la millor puntuació fins al moment
    
    public long setPunts(long punts)
    {
    	Editor editor = valorsGuardats.edit();
    	editor.putLong(JOC_RECORD, punts);
    	editor.commit();
    	return punts;
    } 
    
    public void setNom(String nom)
    {
    	Editor editor = valorsGuardats.edit();
    	editor.putString(JOC_NOMRECORD, nom);
    	editor.commit();
    }     
    public void posaNom()
    {
    	runOnUiThread(new Runnable() {
    	    public void run() {
    	        initDialogs().show();
    	    }
    	});
    }

    // Per entrar el nom a guadar per a la millor puntuació
    public AlertDialog.Builder initDialogs()
    {
    	AlertDialog.Builder alerta;
    	alerta = new AlertDialog.Builder(this);

    	alerta.setIcon(R.drawable.icon);
    	alerta.setTitle("Nou record !");
    	alerta.setMessage("Entra el teu Nom:");

    	// EditText per entrar el nom
    	final EditText input = new EditText(this);
    	alerta.setView(input);
    	alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		setNom(input.getText().toString());
    	  }
    	});

    	alerta.setNegativeButton("Cancel·la", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	  }
    	});
    	return alerta;
    }    
    
    public long getPunts()
    {
    	return valorsGuardats.getLong(JOC_RECORD, (long) 0);
    }
    public String getNom()
    {
    	return valorsGuardats.getString(JOC_NOMRECORD, "");
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // no posar amb notch
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}


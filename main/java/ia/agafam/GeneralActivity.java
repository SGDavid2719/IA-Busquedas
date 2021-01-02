package ia.agafam;

import android.app.Activity;
import android.graphics.Typeface;

public class GeneralActivity extends Activity {
	public static final String PREFERENCIES  = "GamePrefs";
    public static final String JOC_RECORD    = "record";
    public static final String JOC_NOMRECORD = "nomrecord";
    
    protected Typeface fontJoc;
    
    public void init()
    {
        // la font que vull utilitzar per tots els missatges

    	fontJoc = Typeface.createFromAsset(this.getAssets(), "fonts/arkitechbold.ttf");
    }
}

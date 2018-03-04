package corkcollector.source;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.Authenticator;

/**
 * Created by Daniel on 2018-03-04.
 */

public class AuthenticatorService extends Service{
    private CorkCollectorAccountAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new CorkCollectorAccountAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent){
        return mAuthenticator.getIBinder();
    }
}

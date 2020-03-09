package com.shels.delivery.WebService;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.preference.PreferenceManager;

import com.shels.delivery.Constants;
import com.shels.delivery.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebService1C {

    public static ContentValues sendRequest(String SOAP_METHOD, Context context){
        return sendSoapRequest(SOAP_METHOD, context, new HashMap<String, String>());
    }

    public static ContentValues sendRequest(String SOAP_METHOD, Context context, HashMap<String, String>... args){
         return sendSoapRequest(SOAP_METHOD, context, args);
    }

    private static ContentValues sendSoapRequest(String SOAP_METHOD, Context context, HashMap<String, String>... args){
        ContentValues result = new ContentValues();

        // Check internet connection
        if (hasConnection(context) == false){
            result.put(Constants.WEB_SERVICE_MSG, context.getString(R.string.check_hasConnection));
            result.put(Constants.WEB_SERVICE_AUTH, false);
            result.put(Constants.WEB_SERVICE_RESULT, "");

            return result;
        }

        // Create SOAP request
        SoapObject request = new SoapObject(Constants.SERVER_NAMESPACE, SOAP_METHOD);
        if (args.length > 0){
           for (Map.Entry entry: args[0].entrySet()){
               request.addProperty(entry.getKey().toString(), entry.getValue());
           }
        }

        // Create enveloper
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        // Set output SOAP object
        envelope.setOutputSoapObject(request);

        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportBasicAuthSE(Constants.SERVER_URL, context);
        try {
            // Invole web service
            androidHttpTransport.call(Constants.SERVER_SOAP_ACTION + SOAP_METHOD, envelope);

            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            // Assign it to fahren static variable
            result.put(Constants.WEB_SERVICE_MSG, "");
            result.put(Constants.WEB_SERVICE_AUTH, true);
            result.put(Constants.WEB_SERVICE_RESULT, response.toString());

            return result;

        } catch (org.xmlpull.v1.XmlPullParserException e){
            result.put(Constants.WEB_SERVICE_MSG, context.getString(R.string.login_error));
            result.put(Constants.WEB_SERVICE_AUTH, false);
            result.put(Constants.WEB_SERVICE_RESULT, "");

            return result;
        } catch (Exception e) {
            result.put(Constants.WEB_SERVICE_MSG, e.toString());
            result.put(Constants.WEB_SERVICE_AUTH, false);
            result.put(Constants.WEB_SERVICE_RESULT, "");

            return result;
        }
    }

    private static class HttpTransportBasicAuthSE extends HttpTransportSE {
        private String username;
        private String password;
        private SharedPreferences preferences;

        public HttpTransportBasicAuthSE(String url, Context context) {
            super(url);

            preferences = PreferenceManager.getDefaultSharedPreferences(context);

            if (preferences.contains(Constants.PREFERENCE_USER) || preferences.contains(Constants.PREFERENCE_USER_PASSWORD)) {
                this.username = preferences.getString(Constants.PREFERENCE_USER, "");
                this.password = preferences.getString(Constants.PREFERENCE_USER_PASSWORD, "");
            }
        }

        public ServiceConnection getServiceConnection() throws IOException {
            ServiceConnectionSE midpConnection = new ServiceConnectionSE(url);
            addBasicAuthentication(midpConnection);
            return midpConnection;
        }

        protected void addBasicAuthentication(ServiceConnection midpConnection) throws IOException {
            if (username != null && password != null) {
                StringBuffer buf = new StringBuffer(username);
                buf.append(':').append(password);
                byte[] raw = buf.toString().getBytes();
                buf.setLength(0);
                buf.append("Basic ");
                org.kobjects.base64.Base64.encode(raw, 0, raw.length, buf);
                midpConnection.setRequestProperty("Authorization", buf.toString());
            }
        }
    }

    public static boolean hasConnection(final Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

}

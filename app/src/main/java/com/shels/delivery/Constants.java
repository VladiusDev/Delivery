package com.shels.delivery;

public class Constants {

    // Web service params
    public static final String SERVER_NAMESPACE    = "http://192.168.10.38/UPP_meb_buh";
    public static final String SERVER_URL          = "http://192.168.10.38/UPP_meb_buh/ws/ExchangeService";
    public static final String SERVER_SOAP_ACTION  = "http://192.168.10.38/ExchangeService#ExchangeService:";

    public static final String SOAP_METHOD_GET_DELIVERY_ACTS = "getDeliveryActs";
    public static final String SOAP_METHOD_GET_USER_INFO = "getUserInfo";

    // User preferences
    public static final String PREFERENCE_USER = "pref_username";
    public static final String PREFERENCE_USER_PASSWORD = "pref_password";
    public static final String PREFERENCE_USER_PROFILE = "pref_user_profile";
    public static final String PREFERENCE_USER_ID = "pref_user_id";
    public static final String PREFERENCE_USER_AUTH_SUCCESS = "pref_auth_success";
    public static final String PREFERENCE_USER_SAVE_PASSWORD = "pref_save_password";

    // Json
    public static final String JSON_FIELD_GOODS = "goods";
    public static final String JSON_FIELD_DELIVERY_ACTS= "deliveryActs";
    public static final String JSON_FIELD_DELIVERY_BARCODES= "barcodes";
    public static final String JSON_FIELD_DATA = "data";
    public static final String JSON_FIELD_USER = "user";
    public static final String JSON_FIELD_USER_ID = "id";
    public static final String JSON_FIELD_USER_PROFILE = "profile";

    // Web service answer
    public static final String WEB_SERVICE_AUTH = "authorization";
    public static final String WEB_SERVICE_RESULT = "result";
    public static final String WEB_SERVICE_MSG = "message";

    // Yandex
    public static final String YANDEX_MAPS_API_KEY = "e65fe373-cf79-46f5-b0e8-79800b396fd2";
}

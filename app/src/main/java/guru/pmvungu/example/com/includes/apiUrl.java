package guru.pmvungu.example.com.includes;

/**
 * Created by Diasivi on 9/19/2017.
 */

public class apiUrl {
    public String msisdn;
    public boolean postpaid = false;
    private String authpin;

    //APIs URLs//ATBUKIHQPLP006
// public static String baseUrl="http://AFRICELLBSUTEST:8090/SiteSurvey.asmx/";

    public  static String[] strListOfSite;
    public  static String[] strListOfAntennaModel;
    public static  String UserId;
    public static  String DeviceId;
    public static  String RoleId;
    public static  String AntModel;
    public static  String AntPorts;
    public static  String Antenne;
    public static  String Sitename;
    public static  String SessionId;
    public static String Host;
    protected String awun = "$@";
    protected String awp = "@auth@$@";

    public static String baseUrl;
    public apiUrl(String Msisdn){
        msisdn = Msisdn;
    }
    public static void setListOfSite(String[] listSite){
        strListOfSite  = listSite;
    }
    public static void setListOfAntennaModel(String[] strings){
        strListOfAntennaModel  = strings;
    }
    public static String[] getListOfSite(){
           return strListOfSite;
    }
    public static void setUserId(String  xUserId){
        UserId  = xUserId;
    }
    public static String getUserId(){
        return UserId;
    }
    public static void setRoleId(String  xRoleId){
        RoleId  = xRoleId;
    }
    public static void setDeviceId(String  xDeviceId){
        DeviceId  = xDeviceId;
    }
    public static String getRoleId(){
        return RoleId;
    }
    public static String getDevice(){
        return DeviceId;
    }
    public static void setAntModel(String  xAntModel){
        AntModel  = xAntModel;
    }
    public static String getAntModel(){
        return AntModel;
    }
    public static void setAntPorts(String  xAntPorts){
        AntPorts  = xAntPorts;
    }
    public static String getAntPorts(){
        return AntPorts;
    }
    public static void setAntenne(String  xAntenne){
        Antenne  = xAntenne;
    }
    public static String getAntenne(){
        return Antenne;
    }
    public static void setSitename(String  xSitename){
        Sitename  = xSitename;
    }
    public static String getSitename(){
        return Sitename;
    }

    public static void setSessionId(String  xSessionId){
        SessionId  = xSessionId;
    }
    public static String getSessionId(){
        return SessionId;
    }

    public static void setHost(String  xHost){
        Host  = xHost;
    }
    public static String getHost(){
                 return Host;
    }
    public static void getBaseUrl(){
        baseUrl="https://"+getHost()+"/SiteSurvey.asmx/";

    }



}

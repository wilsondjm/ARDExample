package app.vincenthu.citrix.com.storming.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.test.AndroidTestCase;

/**
 * Created by Administrator on 6/20/2016.
 */
public class TestProvider extends AndroidTestCase{

    public TestProvider(){
        super();
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testRegistry(){
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), WeatherProvider.class.getName());
        try{
            ProviderInfo info = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: not able to find the provider: %s with coded one: %s", info.authority, StormingContract.CONTENT_AUTHORITY);
        }catch(PackageManager.NameNotFoundException e){
            assertTrue(String.format("Error: WeatherProvider not registered at %s", mContext.getPackageName()), false);
        }
    }

    public void testGetType(){
        String type1 = getContext().getContentResolver().getType(StormingContract.WeatherInfoEntry.CONTENT_URI);
        assertEquals("Error： failed to load the type", type1, StormingContract.WeatherInfoEntry.CONTENT_TYPE);

        String type2 = getContext().getContentResolver().getType(StormingContract.WeatherInfoEntry.buildWeatherWithLocationUri("Nanjing"));

    }

    public void testBasicQuery(){

    }

    @Override
    protected  void tearDown() throws Exception{
        super.tearDown();
    }
}

package app.vincenthu.citrix.com.storming.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import org.junit.Test;

import app.vincenthu.citrix.com.storming.data.StormingContract;

/**
 * Created by Administrator on 6/16/2016.
 */
public class TestContractUri extends AndroidTestCase {
    public TestContractUri(){
        super();
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testWeatherUri() throws Throwable {
        Uri result = StormingContract.WeatherInfoEntry.buildWeatherUri(1);
        assertEquals("content://com.citrix.vincenthu.app.storming/weather/1", result.toString());

        result = StormingContract.WeatherInfoEntry.buildWeatherWithLocationUri("Nanjing");
        assertEquals("content://com.citrix.vincenthu.app.storming/weather/Nanjing", result.toString());
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}

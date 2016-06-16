package app.vincenthu.citrix.com.storming.data;

import android.content.UriMatcher;
import android.test.AndroidTestCase;

import app.vincenthu.citrix.com.storming.data.WeatherProvider;

/**
 * Created by Administrator on 6/16/2016.
 */
public class TestUriMatcher extends AndroidTestCase {
    public TestUriMatcher(){
        super();
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    public void testUriMatcher(){
        UriMatcher matcher = WeatherProvider.buildUriMacher();
        assertEquals("Error: Failed to match weatherURL", WeatherProvider.WEATHER, matcher.match(StormingContract.WeatherInfoEntry.CONTENT_URI));
        assertEquals("Error: Failed to match weatherLocationURL", WeatherProvider.WEATHER_WITH_LOCATION, matcher.match(StormingContract.WeatherInfoEntry.buildWeatherWithLocationUri("Nanjing")));
        assertEquals("Error: Failed to match weatherLocationURL", WeatherProvider.LOCATION, matcher.match(StormingContract.LocationEntry.CONTENT_URI));
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}

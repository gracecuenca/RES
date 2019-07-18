package com.example.fbu_res;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.fbu_res", appContext.getPackageName());
    }

    @Test
    public void testDefaultApp() {
        System.out.println("Hello there");
        System.out.println("Blah blah blah blah");
        
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext instanceof app ? ((app) appContext) : null;

        Log.d("Okay", "test");
        Log.d("Log statement", "Okay");
        Log.d("Pizza", "Cheese pizza is great. blah blah blah");
    }
}

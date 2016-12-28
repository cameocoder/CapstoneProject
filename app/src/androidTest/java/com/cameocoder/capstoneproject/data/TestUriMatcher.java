package com.cameocoder.capstoneproject.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {
    private static final Uri TEST_WASTE_DIR = WasteContract.EventEntry.CONTENT_URI;

    @Test
    public void testUriMatcher() {
        UriMatcher testMatcher = WasteProvider.buildUriMatcher();

        Assert.assertEquals("Error: The WASTE URI was matched incorrectly.",
                testMatcher.match(TEST_WASTE_DIR), WasteProvider.EVENTS);
    }

}

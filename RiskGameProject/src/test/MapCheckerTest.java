package test;

import org.junit.Before;
import org.junit.Test;
import riskgame.model.Utils.MapChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * MapChecker tester.
 */

public class MapCheckerTest {
    private ArrayList loadableMaps;
    private ArrayList errorMaps;
    private String error_path;

    @Before
    public void setUp() throws Exception {
        loadableMaps = new ArrayList<String>();
        errorMaps = new ArrayList<String>();
        loadableMaps.add("./maps/World.map");
        loadableMaps.add("./maps/Alberta.map");
        loadableMaps.add("./maps/test_map.map");
        loadableMaps.add("./maps/USA.map");
        errorMaps.add("./maps/ErrorMap_ContinentsAmount_United States.map");
        loadableMaps.add("./maps/World-error.map");

        error_path = "./maps/World";
    }

    @Test
    public void checkMapValidity() throws IOException {
        for (int i=0;i<loadableMaps.size();i++){
            assertEquals(9527, MapChecker.checkMapValidity((String) this.loadableMaps.get(i)));
        }
    }

    @Test
    public void isMapValid() throws IOException {
        for (int i=0;i<errorMaps.size();i++){
            assertTrue(MapChecker.isMapValid((String) errorMaps.get(i)));
        }
    }

    @Test
    public void isMapPathValid() {
        assertFalse(MapChecker.isMapPathValid(this.error_path));
    }
}
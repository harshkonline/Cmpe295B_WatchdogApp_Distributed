package cmpe295b.watchdog;

/**
 * Created by harshad on 4/27/2016.
 */
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import cmpe295b.watchdog.ui.activity.FacebookLoginActivity;

@SuppressWarnings("rawtypes")
public class ExampleTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "cmpe295b.watchdog.ui.activity.FacebookLoginActivity";

    private static Class<?> launcherActivityClass;
    static{
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public ExampleTest() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        // check that we have the right activity
        solo.assertCurrentActivity("wrong activity", FacebookLoginActivity.class);
        //Wait for activity: 'com.example.ExampleActivty'
        solo.waitForActivity("FacebookLoginActivity", 2000);

    }
}

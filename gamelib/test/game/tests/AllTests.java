package game.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ConfigurableTest.class, EncoderTest.class, GraphTest.class,
		LongTaskTest.class, PluginManagerTest.class })
public class AllTests {

}

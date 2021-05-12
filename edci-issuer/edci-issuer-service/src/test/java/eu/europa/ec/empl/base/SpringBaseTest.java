package eu.europa.ec.empl.base;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/applicationContext.xml", "classpath:/spring/spring-REST-servlet.xml"})
@WebAppConfiguration
//Annotation marked with inheritance, so all subclasses will inherit this annotation.
public abstract class SpringBaseTest {

    @BeforeClass
    public static void setSystemProps() {
        System.getProperties().setProperty("spring.profiles.active", "default");
    }

}

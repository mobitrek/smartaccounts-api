package eu.mobitrek.smartaccounts;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Ignore
public class SmartAccountsTest {
    final static Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Test
    public void testEstonianDate(){
        log.info("time: {}", SmartAccounts.getEstonianDate().toString());
    }
}
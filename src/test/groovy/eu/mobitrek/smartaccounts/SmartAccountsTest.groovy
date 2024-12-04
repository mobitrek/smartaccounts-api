package eu.mobitrek.smartaccounts

import spock.lang.Specification

class SmartAccountsTest extends Specification {
    def "GetEstonianDate"() {
        when:
            SmartAccounts.getEstonianDate()
        then:
            noExceptionThrown()
    }
}

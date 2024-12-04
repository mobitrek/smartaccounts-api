package eu.mobitrek.security

import spock.lang.Specification

import static eu.mobitrek.security.HmacSha1Signature.calculateRFC2104HMAC

class HmacSha1SignatureTest extends Specification {
    def "CalculateRFC2104HMAC"() {
        when:
            String hmac = calculateRFC2104HMAC("data", "key");
        then:
            hmac.equals("104152c5bfdca07bc633eebd46199f0255c9f49d")
    }
}

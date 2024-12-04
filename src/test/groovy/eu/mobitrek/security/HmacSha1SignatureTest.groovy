package eu.mobitrek.security

import spock.lang.Specification

import static eu.mobitrek.security.HmacSha1Signature.calculateRFC2104HMAC

class HmacSha1SignatureTest extends Specification {
    def "CalculateRFC2104HMAC"() {
        when:
            String hmac = calculateRFC2104HMAC("data", "key");
        then:
            hmac == "5031fe3d989c6d1537a013fa6e739da23463fdaec3b70137d828e36ace221bd0"
    }
}

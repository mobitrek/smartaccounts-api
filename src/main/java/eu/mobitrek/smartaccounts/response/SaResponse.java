package eu.mobitrek.smartaccounts.response;

public record SaResponse(int statusCode, String reasonPhrase, String data) {
}

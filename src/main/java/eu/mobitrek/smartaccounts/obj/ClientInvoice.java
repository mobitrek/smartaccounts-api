package eu.mobitrek.smartaccounts.obj;

import java.util.ArrayList;
import java.util.List;

public class ClientInvoice {
    public String clientId;
    public String date;
    public String currency;
    public String invoiceNote;

    public List<ClientInvoiceRow> rows = new ArrayList();
}

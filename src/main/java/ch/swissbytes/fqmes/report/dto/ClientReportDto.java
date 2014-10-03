package ch.swissbytes.fqmes.report.dto;



/**
 * Created by christian on 12/06/14.
 */
public class ClientReportDto {

   /* private Integer nro;
    private String grsn;
    private String client;
    private String category;
    private String base;
    private String nit;
    private String email;
    private String telephoneFax;
    private String web;
    private String contact;
    private String fax;

    public void copyToDto(final Integer nro,final Customer customer){
        this.nro = nro;
        this.grsn = getValueValid(customer.getGrsn());
        this.client = getValueValid(customer.getName());
        this.category = getValueValid(customer.getActivities());
        this.base = getValueValid(customer.getSede());
        this.nit = getValueValid(customer.getNit());
        this.email = getValueValid(customer.getEmail());
        this.telephoneFax = getValueValid(customer.getPhone());
        this.fax = getValueValid(customer.getFax());
        this.web = getValueValid(customer.getWeb());
        this.contact = getWholeContact(customer.getContactname(), customer.getPhonecontact());
    }

    public String getValueValid(Object object){
        String value = object != null ? object.toString() : "";
        return (value != null && !value.isEmpty()) ? value : "---------";
    }

    private String getWholeTelephoneFax(String phone, String fax) {
        return "Telf. "+phone+""+ System.lineSeparator()+"Fax "+fax+"";
    } 
    public String getWholeContact(String contactname, String phonecontact) {
        return contactname+ System.lineSeparator()+"Telf. "+phonecontact+"";
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephoneFax() {
        return telephoneFax;
    }

    public void setTelephoneFax(String telephoneFax) {
        this.telephoneFax = telephoneFax;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getNro() {
        return nro;
    }

    public void setNro(Integer nro) {
        this.nro = nro;
    }

    public String getGrsn() {
        return grsn;
    }

    public void setGrsn(String grsn) {
        this.grsn = grsn;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }*/
}

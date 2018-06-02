package androidmads.n2q.SmsManagement;

public class SmsClass {
    private int id;
    private String smsTen;
    private String smsSoDienThoai;
    private String smsNoiDung;
    private String smsGio;
    private String smsNgay;
    private int smsTrangThai;

    public SmsClass(int id, String smsSoDienThoai, String smsNoiDung,
                    String smsGio, String smsNgay, int smsTrangThai) {
        this.id             = id;
        this.smsSoDienThoai = smsSoDienThoai;
        this.smsNoiDung     = smsNoiDung;
        this.smsGio         = smsGio;
        this.smsNgay        = smsNgay;
        this.smsTrangThai   = smsTrangThai;
    }

    public SmsClass(int id, String smsTen, String smsSoDienThoai,
                    String smsNoiDung, String smsGio, String smsNgay, int smsTrangThai) {
        this.id = id;
        this.smsTen = smsTen;
        this.smsSoDienThoai = smsSoDienThoai;
        this.smsNoiDung = smsNoiDung;
        this.smsGio = smsGio;
        this.smsNgay = smsNgay;
        this.smsTrangThai = smsTrangThai;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSmsTen() {
        return smsTen;
    }

    public String getSmsSoDienThoai() {
        return smsSoDienThoai;
    }

    public String getSmsNoiDung() {
        return smsNoiDung;
    }

    public String getSmsGio() {
        return smsGio;
    }

    public String getSmsNgay() {
        return smsNgay;
    }

    public int getSmsTrangThai() {
        return smsTrangThai;
    }

}

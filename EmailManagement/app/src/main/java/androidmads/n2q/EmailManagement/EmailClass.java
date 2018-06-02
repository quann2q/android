package androidmads.n2q.EmailManagement;

public class EmailClass {
    private int id;
    private String emailDiaChi;
    private String emailTieuDe;
    private String emailNoiDung;
    private String emailGio;
    private String emailNgay;
    private int emailTrangThai;

    public EmailClass(int id, String emailDiaChi, String emailTieuDe, String emailNoiDung,
                      String emailGio, String emailNgay, int emailTrangThai) {
        this.id = id;
        this.emailDiaChi = emailDiaChi;
        this.emailTieuDe = emailTieuDe;
        this.emailNoiDung = emailNoiDung;
        this.emailGio = emailGio;
        this.emailNgay = emailNgay;
        this.emailTrangThai = emailTrangThai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmailDiaChi() {
        return emailDiaChi;
    }

    public String getEmailTieuDe() {
        return emailTieuDe;
    }

    public String getEmailNoiDung() {
        return emailNoiDung;
    }

    public String getEmailGio() {
        return emailGio;
    }

    public String getEmailNgay() {
        return emailNgay;
    }

    public int getEmailTrangThai() {
        return emailTrangThai;
    }

}

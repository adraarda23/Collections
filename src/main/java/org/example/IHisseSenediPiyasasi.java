package org.example;

import java.util.*;

public interface IHisseSenediPiyasasi {
    void addHisse(String sembol, Hisse hisse);
    void addOperationRecord(Islem islem);
    void addEnYuksekHisseler(Hisse hisse);

    int toplamHisseSayisi();

    // Getters
    public Optional<Hisse> hisseGetir(String sembol);
    HashMap<String, Hisse> getHisseler();
    Queue<Islem> getIslemler();
    TreeSet<Hisse> getEnYuksekHisseler();
    Set<String> getIslemGorenSektorler();
    Set<String> getSektorler();
    ArrayList<Hisse> sektorGetir(String sektor);

    // Setters
    void setHisseler(HashMap<String, Hisse> hisseler);
    void setIslemler(Queue<Islem> islemler);
    void setEnYuksekHisseler(TreeSet<Hisse> enYuksekHisseler);
    void setIslemGorenSektorler(Set<String> islemGorenSektorler);

    List<Hisse> pahalıHisseler(double esik);

    Map<String, Double> sektorOrtalamaFiyat();

    String tumSemboller();

    List<Hisse> fiyatAraligindakilar(double min, double max);

    Map<String, Long> sektorHisseSayisi();

    Double toplamHisseDegeri();


}

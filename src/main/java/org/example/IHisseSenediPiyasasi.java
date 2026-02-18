package org.example;

import java.util.HashMap;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public interface IHisseSenediPiyasasi {
    void addHisse(String sembol, Hisse hisse);
    void addOperationRecord(Islem islem);
    void addEnYuksekHisseler(Hisse hisse);

    // Getters
    HashMap<String, Hisse> getHisseler();
    Queue<Islem> getIslemler();
    TreeSet<Hisse> getEnYuksekHisseler();
    Set<String> getIslemGorenSektorler();

    // Setters
    void setHisseler(HashMap<String, Hisse> hisseler);
    void setIslemler(Queue<Islem> islemler);
    void setEnYuksekHisseler(TreeSet<Hisse> enYuksekHisseler);
    void setIslemGorenSektorler(Set<String> islemGorenSektorler);
}

package org.example;

import java.util.*;

public class HisseSenediPiyasasi implements IHisseSenediPiyasasi {
    HashMap<String,Hisse> hisseler = new HashMap<>();

    Queue<Islem> islemler = new ArrayDeque<>();

    TreeSet<Hisse> enYuksekHisseler = new TreeSet<>(
            Comparator.comparingDouble((Hisse h) -> h.fiyat)
                    .thenComparing(h -> h.sembol));

    Set<String> islemGorenSektorler = new HashSet<>();

    public void addHisse(String sembol, Hisse hisse) {
        hisseler.put(sembol, hisse);
    }

    public void addOperationRecord(Islem islem){
        if(islemler.size()==10){
            islemler.poll();
        }
        islemler.offer(islem);
    }

    public HashMap<String, Hisse> getHisseler() {
        return hisseler;
    }

    public Queue<Islem> getIslemler() {
        return islemler;
    }

    public void setIslemler(Queue<Islem> islemler) {
        this.islemler = islemler;
    }

    public TreeSet<Hisse> getEnYuksekHisseler() {
        return enYuksekHisseler;
    }

    public void setEnYuksekHisseler(TreeSet<Hisse> enYuksekHisseler) {
        this.enYuksekHisseler = enYuksekHisseler;
    }

    public Set<String> getIslemGorenSektorler() {
        return islemGorenSektorler;
    }

    public void setIslemGorenSektorler(Set<String> islemGorenSektorler) {
        this.islemGorenSektorler = islemGorenSektorler;
    }

    public void addEnYuksekHisseler(Hisse hisse){
        enYuksekHisseler.add(hisse);
        if(enYuksekHisseler.size()>5){
            enYuksekHisseler.pollFirst();
        }
    }

}

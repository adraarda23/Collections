package org.example;

import java.util.*;

public class HisseSenediPiyasasi {
    HashMap<String,Hisse> hisseler;

    Queue<Islem> islemler=new ArrayDeque<>();

    TreeSet<Hisse> enYuksekHisseler = new TreeSet<>(
            Comparator.comparingDouble((Hisse h) -> h.fiyat)
                    .thenComparing(h -> h.sembol));

    Set<String> islemGorenSektorler = new HashSet<>();



    public void addOperationRecord(Islem islem){
        if(islemler.size()==10){
            islemler.poll();
        }
        islemler.offer(islem);
    }

    public void addEnYuksekHisseler(Hisse hisse){
        enYuksekHisseler.add(hisse);
        if(enYuksekHisseler.size()>5){
            enYuksekHisseler.pollFirst();
        }

    }

}

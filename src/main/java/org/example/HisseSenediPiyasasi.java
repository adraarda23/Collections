package org.example;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Set<String> getSektorler() {
        return hisseler
                .values()
                .stream()
                .map(Hisse::getSektor)
                .collect(Collectors
                        .toCollection(TreeSet::new));
    }

    @Override
    public ArrayList<Hisse> sektorGetir(String sektor) {
        return hisseler.values().stream().filter(s -> s.getSektor().equals(sektor)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void setHisseler(HashMap<String, Hisse> hisseler) {

    }


    public void setIslemGorenSektorler(Set<String> islemGorenSektorler) {
        this.islemGorenSektorler = islemGorenSektorler;
    }

    @Override
    public List<Hisse> pahalıHisseler(double esik) {
        return hisseler.values().stream().filter(s -> s.getFiyat() > esik).collect(Collectors.toList());
    }

    @Override
    public Map<String, Double> sektorOrtalamaFiyat() {
        return hisseler.values().stream()
                .collect(Collectors.groupingBy(
                        Hisse::getSektor,
                        Collectors.averagingDouble(Hisse::getFiyat)
                ));
    }

    @Override
    public String tumSemboller() {
        StringBuilder sb = new StringBuilder();
        //return hisseler.values().stream().map(Hisse::getSembol).collect(Collectors.joining(","));
        return String.join(",", hisseler.keySet());
    }

    @Override
    public List<Hisse> fiyatAraligindakilar(double min, double max) {
        return hisseler.values().stream().filter(s -> s.getFiyat() >= min && s.getFiyat() <= max).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> sektorHisseSayisi() {
        return hisseler.values().stream()
                .collect(Collectors.groupingBy(Hisse::getSektor, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    @Override
    public Double toplamHisseDegeri() {
        return hisseler.values().stream().map(Hisse::getFiyat).reduce(0.0,Double::sum);
    }


    public void addEnYuksekHisseler(Hisse hisse){
        enYuksekHisseler.add(hisse);
        if(enYuksekHisseler.size()>5){
            enYuksekHisseler.pollFirst();
        }
    }

    @Override
    public int toplamHisseSayisi() {
        return hisseler.size();
    }

    public Optional<Hisse> hisseGetir(String sembol){
        return  Optional.ofNullable(hisseler.get(sembol));
    }



}

# TreeSet — Kapsamlı Dokümantasyon

## İçindekiler

1. [TreeSet Nedir?](#1-treeset-nedir)
2. [İç Yapı — Red-Black Tree](#2-iç-yapı--red-black-tree)
3. [Constructor'lar](#3-constructorlar)
4. [Temel Collection Methodları](#4-temel-collection-methodları)
5. [SortedSet Methodları](#5-sortedset-methodları)
6. [NavigableSet Methodları](#6-navigableset-methodları)
7. [Comparator vs Comparable](#7-comparator-vs-comparable)
8. [View'lar — headSet / tailSet / subSet](#8-viewlar--headset--tailset--subset)
9. [Top-K Deseni](#9-top-k-deseni)
10. [Dikkat Edilecekler](#10-dikkat-edilecekler)
11. [Karşılaştırma Tablosu](#11-karşılaştırma-tablosu)

---

## 1. TreeSet Nedir?

Tekrarsız elemanları **her zaman sıralı** tutan bir `Set` implementasyonudur.

Gerçek dünya karşılığı olarak bir kütüphanedeki alfabetik kitap rafını düşün. Yeni bir kitap eklediğinde kitap doğru yerine gider, rafta gezdikçe her zaman sıralı görürsün, bir kitabı aldığında yerindeki boşluğa yakın olanları hemen bulabilirsin.

```
Standart Set'ler arasındaki fark:

HashSet       → Karışık sıra, en hızlı
LinkedHashSet → Ekleme sırası, hızlı
TreeSet       → Her zaman sıralı, biraz daha yavaş
```

**Temel özellikler:**
- Kırmızı-siyah ağaç (Red-Black Tree) tabanlıdır
- Elemanlar **her zaman sıralıdır** — doğal sıra veya verilen `Comparator`'a göre
- Ekleme, silme, arama: `O(log n)`
- `null` elemana izin **vermez** → `NullPointerException`
- **Thread-safe değildir**
- `NavigableSet` → `SortedSet` → `Set` → `Collection` hiyerarşisini implemente eder

---

## 2. İç Yapı — Red-Black Tree

TreeSet içinde bir **Red-Black Tree** (Kırmızı-Siyah Ağaç) saklar. Bu, dengeli bir ikili arama ağacıdır.

### İkili Arama Ağacı Mantığı

Her düğümün solundaki tüm değerler ondan **küçük**, sağındaki tüm değerler ondan **büyüktür**.

```
[5, 3, 8, 1, 4] eklendikten sonra ağaç:

        5
       / \
      3   8
     / \
    1   4

Arama: 4 var mı?
  → 5'e bak: 4 < 5, sola git
  → 3'e bak: 4 > 3, sağa git
  → 4'e bak: bulundu ✅
  → 3 adımda bulundu (log₂ 5 ≈ 2.3)
```

### Neden O(log n)?

Denge sayesinde ağacın yüksekliği her zaman `log n` civarındadır. 1.000.000 elemanlı bir `TreeSet`'te bile en fazla ~20 adımda herhangi bir elemana ulaşırsın.

### Red-Black Tree Dengesi

Sıradan ikili arama ağaçları dengesizleşebilir (sıralı veri eklenince tek tarafa yığılır). Red-Black Tree, her düğüme kırmızı/siyah renk atayarak ve rotasyon yaparak her zaman dengeli kalır. Bu detayı bilmen gerekmez — sonuç olarak **her işlem garantili O(log n)** demektir.

---

## 3. Constructor'lar

```java
import java.util.TreeSet;
import java.util.NavigableSet;
import java.util.Comparator;
import java.util.List;

// 1. Varsayılan — doğal sıra (Comparable kullanır)
NavigableSet<Integer> set1 = new TreeSet<>();
// Integer zaten Comparable implemente ettiği için çalışır: 1, 2, 3, ...

// 2. Comparator ile — özel sıralama kuralı
NavigableSet<String> set2 = new TreeSet<>(Comparator.comparingInt(String::length));
// Stringleri uzunluklarına göre sıralar

// 3. Mevcut Collection'dan — elemanları kopyalar, doğal sırayla sıralar
NavigableSet<Integer> set3 = new TreeSet<>(List.of(5, 3, 8, 1));
// Sonuç: [1, 3, 5, 8]

// 4. Mevcut SortedSet'ten — hem elemanları hem Comparator'ı kopyalar
NavigableSet<Integer> set4 = new TreeSet<>(set1);
// set1'in sıralama kuralını ve elemanlarını alır
```

> **Not:** Constructor 2 ile `Comparator` verilmişse, constructor 3 ile `Collection` verilmişse
> doğal sıra kullanılır. İkisi birlikte verilemez — `Comparator` her zaman doğal sıraya öncelik alır.

---

## 4. Temel Collection Methodları

TreeSet, `Set`'ten ve dolayısıyla `Collection`'dan gelen methodları da içerir. Bunların TreeSet'e özgü davranışlarına dikkat et.

```java
NavigableSet<Integer> set = new TreeSet<>();

// --- Ekleme ---
boolean eklendi1 = set.add(5);   // true  — eklendi
boolean eklendi2 = set.add(3);   // true  — eklendi
boolean eklendi3 = set.add(5);   // false — zaten var, TreeSet tekrar eklemez

set.addAll(List.of(1, 8, 4));    // toplu ekleme

// --- Silme ---
boolean silindi = set.remove(3); // true  — silindi
set.removeAll(List.of(1, 4));    // toplu silme
set.retainAll(List.of(5, 8));    // sadece 5 ve 8'i tut, gerisini sil
set.clear();                     // tümünü sil

// --- Sorgulama ---
set.addAll(List.of(1, 3, 5, 8));

boolean var  = set.contains(3);             // true
boolean hepsi = set.containsAll(List.of(1, 5)); // true
boolean bos  = set.isEmpty();               // false
int     boyut = set.size();                 // 4

// --- Dönüştürme ---
Object[]  dizi1 = set.toArray();
Integer[] dizi2 = set.toArray(new Integer[0]);

// --- Döngü — SIRAYLA döner, HashSet gibi rastgele değil ---
for (Integer n : set) {
    System.out.print(n + " "); // 1 3 5 8  (her zaman sıralı)
}

set.forEach(n -> System.out.print(n + " ")); // aynı sonuç

// --- Stream ---
set.stream()
   .filter(n -> n > 3)
   .forEach(System.out::println); // 5, 8
```

---

## 5. SortedSet Methodları

`SortedSet`, `Set`'i genişletir ve sıralı yapılara özgü methodları ekler.

| Method | Açıklama | Boşsa |
|--------|----------|-------|
| `first()` | En küçük elemanı döner | `NoSuchElementException` |
| `last()` | En büyük elemanı döner | `NoSuchElementException` |
| `comparator()` | Kullanılan `Comparator`'ı döner | `null` (doğal sıra kullanılıyorsa) |
| `headSet(e)` | `e`'den küçük elemanların view'ı (`e` dahil değil) | boş set |
| `tailSet(e)` | `e`'ye eşit veya büyük elemanların view'ı (`e` dahil) | boş set |
| `subSet(from, to)` | `[from, to)` aralığının view'ı | boş set |

```java
NavigableSet<Integer> set = new TreeSet<>(List.of(1, 3, 5, 8, 10));
// set: [1, 3, 5, 8, 10]

int ilk = set.first();  // 1
int son  = set.last();  // 10

Comparator<? super Integer> comp = set.comparator(); // null — doğal sıra kullanılıyor

// headSet: 5'ten KÜÇÜK olanlar (5 dahil değil)
SortedSet<Integer> kucukler = set.headSet(5);   // [1, 3]

// tailSet: 5'e EŞİT veya büyük olanlar (5 dahil)
SortedSet<Integer> buyukler = set.tailSet(5);   // [5, 8, 10]

// subSet: [3, 8) → 3 dahil, 8 hariç
SortedSet<Integer> aralik = set.subSet(3, 8);   // [3, 5]
```

> `headSet`, `tailSet`, `subSet` **live view** döner — orijinal set değişince view da değişir.
> Detay için bkz. [Bölüm 8](#8-viewlar--headset--tailset--subset).

---

## 6. NavigableSet Methodları

`NavigableSet`, `SortedSet`'i genişletir ve navigasyon (gezinti) methodları ekler. TreeSet'in en güçlü yanı burasıdır.

### 6.1 Nokta Navigasyon Methodları

Belirli bir değere **göre** komşu eleman bulmak için kullanılır.

```
set: [1, 3, 5, 8, 10]

        floor(4) = 3     →  4'e <=  en büyük (4 yok, 3 var)
      ceiling(4) = 5     →  4'e >=  en küçük (4 yok, 5 var)
        lower(5) = 3     →  5'ten kesin < en büyük (5 dahil değil)
       higher(5) = 8     →  5'ten kesin > en küçük (5 dahil değil)

        floor(5) = 5     →  5'e <=  en büyük (5 var, kendisi)
      ceiling(5) = 5     →  5'e >=  en küçük (5 var, kendisi)
```

| Method | Açıklama | Eleman Yoksa |
|--------|----------|--------------|
| `floor(e)` | `e`'ye `<=` en büyük elemanı döner | `null` |
| `ceiling(e)` | `e`'ye `>=` en küçük elemanı döner | `null` |
| `lower(e)` | `e`'den kesinlikle küçük en büyük elemanı döner | `null` |
| `higher(e)` | `e`'den kesinlikle büyük en küçük elemanı döner | `null` |

```java
NavigableSet<Integer> set = new TreeSet<>(List.of(1, 3, 5, 8, 10));

// floor — e dahil, aşağı doğru
Integer f1 = set.floor(4);   // 3   (4 yok, 4'ten küçük en büyük)
Integer f2 = set.floor(5);   // 5   (5 var, kendisi)
Integer f3 = set.floor(0);   // null (0'dan küçük eleman yok)

// ceiling — e dahil, yukarı doğru
Integer c1 = set.ceiling(4); // 5   (4 yok, 4'ten büyük en küçük)
Integer c2 = set.ceiling(5); // 5   (5 var, kendisi)
Integer c3 = set.ceiling(11);// null (11'den büyük eleman yok)

// lower — e HARİÇ, aşağı doğru
Integer l1 = set.lower(5);   // 3   (5'ten kesin küçük en büyük)
Integer l2 = set.lower(1);   // null (1'den küçük eleman yok)

// higher — e HARİÇ, yukarı doğru
Integer h1 = set.higher(5);  // 8   (5'ten kesin büyük en küçük)
Integer h2 = set.higher(10); // null (10'dan büyük eleman yok)
```

> **floor vs lower, ceiling vs higher farkı:**
> - `floor` / `ceiling` → verilen değer sette varsa **kendisini** döner
> - `lower` / `higher`  → verilen değer sette olsa bile **komşusunu** döner (kendisini dönemez)

---

### 6.2 Polling Methodları — Al ve Sil

Elemanı setten çıkararak döner. `first()` / `last()`'tan farkı: silme de yapar.

| Method | Açıklama | Set Boşsa |
|--------|----------|-----------|
| `pollFirst()` | En küçük elemanı **alır ve siler** | `null` |
| `pollLast()` | En büyük elemanı **alır ve siler** | `null` |

```java
NavigableSet<Integer> set = new TreeSet<>(List.of(1, 3, 5, 8));
// set: [1, 3, 5, 8]

// pollFirst — en küçüğü al ve sil
Integer alinan1 = set.pollFirst(); // 1
// set: [3, 5, 8]

Integer alinan2 = set.pollFirst(); // 3
// set: [5, 8]

// pollLast — en büyüğü al ve sil
Integer alinan3 = set.pollLast();  // 8
// set: [5]

Integer alinan4 = set.pollLast();  // 5
// set: []

Integer alinan5 = set.pollFirst(); // null (set boş, exception değil!)
Integer alinan6 = set.pollLast();  // null (set boş, exception değil!)

// first() / last() farkı: set boşken exception fırlatır
// set.first(); // NoSuchElementException ← BUNU YAPMA
```

> `pollFirst()` ve `pollLast()` boş sette `null` döner.
> `first()` ve `last()` ise `NoSuchElementException` fırlatır.
> Silme + alma işlemi yapacaksan her zaman `poll` versiyonlarını tercih et.

---

### 6.3 Ters Sıralı Görünüm

```java
NavigableSet<Integer> set = new TreeSet<>(List.of(1, 3, 5, 8));

// Ters sıralı view (live — orijinali etkiler)
NavigableSet<Integer> ters = set.descendingSet();
// ters: [8, 5, 3, 1]

ters.forEach(n -> System.out.print(n + " ")); // 8 5 3 1

// ters üzerinde first() → en büyük (çünkü sıra ters)
int enBuyuk = ters.first(); // 8

// Ters iterator
set.descendingIterator().forEachRemaining(
    n -> System.out.print(n + " ") // 8 5 3 1
);
```

---

### 6.4 NavigableSet ile Aralık Methodları (Inclusive Versiyon)

`SortedSet`'teki `headSet`, `tailSet`, `subSet` metodlarının `inclusive` parametreli versiyonları:

```java
NavigableSet<Integer> set = new TreeSet<>(List.of(1, 3, 5, 8, 10));

// headSet(e, inclusive) — e dahil mi değil mi?
NavigableSet<Integer> h1 = set.headSet(5, false); // [1, 3]      5 hariç
NavigableSet<Integer> h2 = set.headSet(5, true);  // [1, 3, 5]   5 dahil

// tailSet(e, inclusive)
NavigableSet<Integer> t1 = set.tailSet(5, true);  // [5, 8, 10]  5 dahil
NavigableSet<Integer> t2 = set.tailSet(5, false); // [8, 10]     5 hariç

// subSet(from, fromInclusive, to, toInclusive)
NavigableSet<Integer> s1 = set.subSet(3, true,  8, false); // [3, 5]     [3, 8)
NavigableSet<Integer> s2 = set.subSet(3, false, 8, true);  // [5, 8]     (3, 8]
NavigableSet<Integer> s3 = set.subSet(3, true,  8, true);  // [3, 5, 8]  [3, 8]
NavigableSet<Integer> s4 = set.subSet(3, false, 8, false); // [5]        (3, 8)
```

> **Matematiksel notasyon karşılığı:**
> - `true`  → köşeli parantez `[` veya `]` → uç değer dahil
> - `false` → normal parantez `(` veya `)` → uç değer hariç

---

## 7. Comparator vs Comparable

### Comparable — Sınıfın İçinde Tanımlanır

Sınıf kendisi "nasıl karşılaştırılacağını" bilir. `compareTo` methodunu implemente eder.

```java
public class Hisse implements Comparable<Hisse> {
    String sembol;
    double fiyat;

    @Override
    public int compareTo(Hisse other) {
        // Negatif → this önce gelsin
        // 0       → eşit
        // Pozitif → other önce gelsin
        return Double.compare(this.fiyat, other.fiyat);
    }
}

// Kullanım — Comparator gerekmez
TreeSet<Hisse> set = new TreeSet<>(); // Comparable'ı otomatik kullanır
set.add(new Hisse("THYAO", 245.80));
set.add(new Hisse("GARAN",  89.40));
// Sıra: [GARAN 89.40, THYAO 245.80]
```

**Ne zaman kullan?** Sınıf için tek ve evrensel bir "doğal sıra" varsa. `String` alfabetik, `Integer` sayısal, `LocalDate` kronolojik sırayla karşılaştırılır — bunlar Comparable implemente eder.

---

### Comparator — Dışarıdan Verilir

Sınıfı değiştirmeden farklı sıralama kuralları tanımlanır.

```java
// Fiyata göre artan
Comparator<Hisse> fiyataGore = Comparator.comparingDouble(h -> h.fiyat);

// Fiyata göre azalan
Comparator<Hisse> fiyataGoreAzalan = Comparator.comparingDouble((Hisse h) -> h.fiyat).reversed();

// Sembole göre alfabetik
Comparator<Hisse> semboleGore = Comparator.comparing(h -> h.sembol);

// Önce sektöre göre, aynı sektörde fiyata göre
Comparator<Hisse> cokluKriteri = Comparator
    .comparing((Hisse h) -> h.sektor)
    .thenComparingDouble(h -> h.fiyat);

// Farklı TreeSet'lere farklı Comparator
TreeSet<Hisse> fiyatSirasiyla = new TreeSet<>(fiyataGore);
TreeSet<Hisse> sembolSirasiyla = new TreeSet<>(semboleGore);
```

**Ne zaman kullan?**
- Sınıfın kodu sende değilse (üçüncü parti kütüphane)
- Birden fazla farklı sıralama kriterine ihtiyaç varsa
- Sıralama mantığı bağlama göre değişiyorsa

---

### compareTo ile equals Tutarsızlığı — Sessiz Hata

`TreeSet` eşitliği **`compareTo`/`compare` sonucuna** bakarak belirler, `equals`'a değil. İkisi tutarsız olursa elemanlar sessizce kaybolabilir.

```java
// YANLIŞ — compareTo ve equals tutarsız
public class Hisse implements Comparable<Hisse> {
    String sembol;
    double fiyat;

    @Override
    public int compareTo(Hisse other) {
        return Double.compare(this.fiyat, other.fiyat); // fiyata göre
    }

    @Override
    public boolean equals(Object o) {
        // equals sembole bakıyor ama compareTo fiyata bakıyor!
        return this.sembol.equals(((Hisse) o).sembol);
    }
}

TreeSet<Hisse> set = new TreeSet<>();
Hisse h1 = new Hisse("THYAO", 245.80);
Hisse h2 = new Hisse("THYAO", 245.80); // aynı sembol, aynı fiyat

set.add(h1);
set.add(h2);
System.out.println(set.size()); // 1 — compareTo 0 döndürdü, aynı kabul edildi ✅

// Ama şimdi:
Hisse h3 = new Hisse("THYAO", 300.00); // aynı sembol, farklı fiyat
set.add(h3);
System.out.println(set.size()); // 2 — compareTo ≠ 0, eklendi ama equals true diyordu!
// Set'te "aynı" sembolden iki tane var — equals tutarsız, sessiz bug ❌

// DOĞRU — compareTo ve equals aynı kriteri kullanmalı
@Override
public int compareTo(Hisse other) {
    int fiyatKarsilastirma = Double.compare(this.fiyat, other.fiyat);
    if (fiyatKarsilastirma != 0) return fiyatKarsilastirma;
    return this.sembol.compareTo(other.sembol); // fiyat eşitse sembole bak
}

@Override
public boolean equals(Object o) {
    if (!(o instanceof Hisse)) return false;
    Hisse other = (Hisse) o;
    return this.fiyat == other.fiyat && this.sembol.equals(other.sembol); // aynı kriter
}
```

---

### Mutable (Değişebilir) Nesne Riski

TreeSet, elemanı eklerken sıraya koyar. Sonradan alanı değiştirirsen TreeSet bunu bilmez — sıralama bozulur.

```java
TreeSet<Hisse> set = new TreeSet<>(Comparator.comparingDouble(h -> h.fiyat));
Hisse thyao = new Hisse("THYAO", 245.80);
set.add(thyao);
set.add(new Hisse("GARAN", 89.40));
// [GARAN 89.40, THYAO 245.80]

// YANLIŞ: Nesneyi sette tutarken alanını değiştirme
thyao.fiyat = 50.00; // TreeSet bunu bilmiyor!
System.out.println(set.first()); // GARAN beklenirken THYAO gelebilir — tanımsız davranış
System.out.println(set.contains(thyao)); // false! Artık bulunamıyor
```

---

## 8. View'lar — headSet / tailSet / subSet

`headSet`, `tailSet`, `subSet` gerçek bir kopya **değil**, orijinal setin bir **penceresidir (live view)**.

### Live View Ne Demek?

```java
NavigableSet<Integer> orijinal = new TreeSet<>(List.of(1, 3, 5, 8, 10));
NavigableSet<Integer> view = orijinal.headSet(6, true); // [1, 3, 5]

// Orijinale 4 ekle → view'da görünür
orijinal.add(4);
System.out.println(view); // [1, 3, 4, 5] ← view güncellendi

// View'dan sil → orijinalden de silinir
view.remove(3);
System.out.println(orijinal); // [1, 4, 5, 8, 10] ← orijinal değişti
```

### View Aralığı Dışına Eleman Eklenemez

```java
NavigableSet<Integer> view = orijinal.headSet(6, true); // [1, 4, 5]

view.add(2);  // ✅ 2 < 6, aralık içinde
view.add(9);  // ❌ IllegalArgumentException — 9 > 6, aralık dışı
```

### View Üzerinde Dönerken Orijinali Değiştirme

```java
NavigableSet<Integer> set = new TreeSet<>(List.of(1, 3, 5, 8));

// YANLIŞ — iterasyon sırasında orijinali değiştirme
for (Integer n : set) {
    set.remove(n); // ❌ ConcurrentModificationException
}

// DOĞRU — pollFirst ile döngü
while (!set.isEmpty()) {
    Integer n = set.pollFirst(); // ✅ her adımda siler ve alır
    System.out.println(n);
}
```

### Kopya Almak İstersen

```java
// Orijinalden bağımsız kopya
NavigableSet<Integer> kopya = new TreeSet<>(orijinal.headSet(6, true));
// Artık orijinal değişse kopya etkilenmez
```

---

## 9. Top-K Deseni

"En yüksek fiyatlı 5 hisseyi her zaman sıralı tut" gibi senaryolarda `pollFirst()` ile boyut sınırı uygulanır.

### Nasıl Çalışır?

```
Hedef: Daima en yüksek fiyatlı 5 hisseyi tut.

TreeSet sıralama: artan (en küçük solda, en büyük sağda)
pollFirst()     : en küçüğü sil

Mantık: 6. eleman eklenince en küçük fiyatlı çıkar → 5 en büyük kalır.
```

```java
TreeSet<Hisse> enYuksekBes = new TreeSet<>(
    Comparator.comparingDouble((Hisse h) -> h.fiyat)
              .thenComparing(h -> h.sembol)  // fiyat eşitse sembole göre ayır
);

void hisseEkle(Hisse h) {
    enYuksekBes.add(h);
    if (enYuksekBes.size() > 5) {
        enYuksekBes.pollFirst(); // en düşük fiyatlıyı çıkar
    }
}
```

**Adım adım:**
```
Ekle GARAN   89.40 → [89.40]
Ekle AKBNK   71.20 → [71.20, 89.40]
Ekle THYAO  245.80 → [71.20, 89.40, 245.80]
Ekle ASELS  187.60 → [71.20, 89.40, 187.60, 245.80]
Ekle FROTO  923.00 → [71.20, 89.40, 187.60, 245.80, 923.00]   ← 5 eleman, sınıra ulaştı

Ekle TUPRS  387.50 → [71.20, 89.40, 187.60, 245.80, 387.50, 923.00]  ← 6 oldu
            pollFirst() → 71.20 (AKBNK) çıkar
            Sonuç: [89.40, 187.60, 245.80, 387.50, 923.00]            ← AKBNK elendi ✅

Ekle BIMAS  456.00 → pollFirst() → 89.40 (GARAN) çıkar
            Sonuç: [187.60, 245.80, 387.50, 456.00, 923.00]           ← GARAN elendi ✅

Ekle SISE    62.50 → 62.50 eklendi, size > 5 → pollFirst() → 62.50 kendisi çıkar!
            Sonuç: [187.60, 245.80, 387.50, 456.00, 923.00]           ← SISE hiç girmedi ✅
```

> Son örnekte dikkat: 62.50 eklendi ama hemen kendisi çıktı.
> Bu doğru davranış — top 5'e girecek kadar yüksek değildi.

### Mevcut En Düşüğü Kontrol Ederek Optimize Etme

6 elemanlı TreeSet yerine 5 elemanda kalmasını garanti altına almak istersen:

```java
void hisseEkle(Hisse h) {
    if (enYuksekBes.size() < 5) {
        enYuksekBes.add(h);
    } else if (h.fiyat > enYuksekBes.first().fiyat) {
        // Yeni hisse mevcut en düşükten yüksekse
        enYuksekBes.pollFirst();
        enYuksekBes.add(h);
    }
    // Değilse hiç ekleme — gereksiz ekleme/silme yok
}
```

---

## 10. Dikkat Edilecekler

### null Eleman — Her Zaman Hata

```java
TreeSet<String> set = new TreeSet<>();
set.add("Elma");
set.add(null); // ❌ NullPointerException

// HashSet ve LinkedHashSet null kabul eder, TreeSet etmez.
// Neden? null ile compareTo çağrısı yapılamaz.
```

### Boş Sette first() / last() — Exception

```java
TreeSet<Integer> set = new TreeSet<>();
set.first(); // ❌ NoSuchElementException
set.last();  // ❌ NoSuchElementException

// Güvenli yol:
if (!set.isEmpty()) {
    int ilk = set.first();
}

// veya pollFirst/pollLast — boşsa null döner, exception değil
Integer ilk = set.pollFirst(); // null (exception değil)
```

### Comparator 0 Dönerse Eleman Eklenmez

```java
// Sadece fiyata bakıyoruz, sembol göz ardı ediliyor
TreeSet<Hisse> set = new TreeSet<>(Comparator.comparingDouble(h -> h.fiyat));

set.add(new Hisse("THYAO", 245.80));
set.add(new Hisse("GARAN", 245.80)); // Fiyat aynı → Comparator 0 döner → EKLENMEDİ!

System.out.println(set.size()); // 1 — GARAN kayboldu, sessiz hata ❌

// ÇÖZÜM: İkincil kriter ekle
TreeSet<Hisse> setDuzeltilmis = new TreeSet<>(
    Comparator.comparingDouble((Hisse h) -> h.fiyat)
              .thenComparing(h -> h.sembol) // fiyat eşitse sembol farklılaştırır
);
set.add(new Hisse("THYAO", 245.80));
set.add(new Hisse("GARAN", 245.80));
System.out.println(setDuzeltilmis.size()); // 2 ✅
```

---

## 11. Karşılaştırma Tablosu

| Özellik | `TreeSet` | `HashSet` | `LinkedHashSet` |
|---------|-----------|-----------|-----------------|
| **Sıra** | Her zaman sıralı | Garanti yok | Ekleme sırası |
| **Hız (add/remove/contains)** | O(log n) | O(1) ort. | O(1) ort. |
| **null eleman** | ❌ | ✅ (1 adet) | ✅ (1 adet) |
| **Bellek** | Fazla (ağaç düğümleri) | Az | Orta (bağlı liste) |
| **`first()` / `last()`** | ✅ | ❌ | ❌ |
| **`floor()` / `ceiling()`** | ✅ | ❌ | ❌ |
| **`pollFirst()` / `pollLast()`** | ✅ | ❌ | ❌ |
| **Aralık sorgusu (subSet)** | ✅ | ❌ | ❌ |
| **Thread-safe** | ❌ | ❌ | ❌ |
| **Ne zaman kullan** | Sıralı tut, aralık sorgula | Hız öncelik, sıra önemsiz | Ekleme sırasını koru |

---

> **Kaynaklar:**
> - [TreeSet Javadoc — Java 24](https://docs.oracle.com/en/java/docs/api/java.base/java/util/TreeSet.html)
> - [NavigableSet Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/NavigableSet.html)
> - [SortedSet Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/SortedSet.html)

"# Proje Görevi — Borsa Veri İşleme ve Analiz Platformu

> **Konu:** Gerçek bir senaryo üzerinden Collections, Stream API ve Parallel Programming'i aynı anda pekiştir.
> **Süre:** Kendi hızında ilerle. Her adımı bitirmeden bir sonrakine geçme.
> **Kural:** Bir adımda 30 dakikadan fazla takılırsan Claude'a danış — ama önce kendin dene.

---

## Senaryo

Bir **borsa veri analiz platformu** geliştiriyorsun.
Platform; hisse senetlerini takip eder, anlık fiyatları günceller, portföyleri yönetir ve analizler üretir.

Her adım bir öncekinin üzerine inşa edilir. Proje sonunda elimde çalışan, gerçekçi bir sistem olacak.

---

## Veri Modeli

### Gerçek Dünya Karşılıkları

Borsa uygulaması 4 ana kavram üzerine kurulu. Her birini gerçek dünyadan bir örnekle düşün:

```
Hisse       →  Borsada alınıp satılan bir şirketin kağıdı          (örn: THYAO)
Portfoy     →  Bir yatırımcının elinde tuttuğu hisseler ve nakiti   (örn: Ali'nin hesabı)
Islem       →  Gerçekleşmiş bir alış veya satış kaydı              (örn: Ali 100 THYAO aldı)
PiyasaOzeti →  Günün kapanışında oluşan istatistik raporu          (örn: bugünün kazananları)
```

---

### Sınıf Detayları

#### `Hisse` — Bir şirkete ait hisse senedi

```java
public class Hisse {
    String sembol;               // Borsadaki kısa kodu         → "THYAO"
    String sirketAdi;            // Şirketin tam adı            → "Türk Hava Yolları"
    String sektor;               // Faaliyet alanı              → "Havacılık"
    double fiyat;                // Anlık fiyat (TL)            → 245.80
    double gunlukDegisimYuzdesi; // Önceki güne göre değişim    → +3.2 (yüzde olarak sakla, % işareti değil)
    long   hacim;                // Gün içinde el değiştiren lot sayısı → 12_500_000
}
```

> `hacim` neden önemli? Yüksek hacim = o hisse çok işlem görüyor = likit demek.
> `gunlukDegisimYuzdesi` pozitifse hisse yükselmiş, negatifse düşmüş.

---

#### `Portfoy` — Bir yatırımcının varlık tablosu

```java
public class Portfoy {
    String              sahipAdi;  // Yatırımcının adı               → "Ali Yılmaz"
    Map<String, Integer> pozisyonlar; // sembol → kaç lot tutuyor     → {"THYAO": 100, "GARAN": 250}
    double              nakit;     // Henüz hisseye dönüşmemiş para  → 15_000.0 (TL)
}
```

> `pozisyonlar` neden `Map<String, Integer>`?
> Anahtar sembol (benzersiz), değer lot adedi. Ali'nin portföyünde THYAO'dan kaç tane var sorusuna O(1)'de cevap verir.
>
> Portföy değeri = her sembol için `(fiyat × lot adedi)` toplamı + nakit.
> Bunun için `Portfoy` tek başına yetmez; `Hisse` verisine de ihtiyaç var çünkü anlık fiyat orada.

---

#### `Islem` — Gerçekleşmiş bir alış/satış kaydı

```java
public enum IslemTipi { AL, SAT }

public class Islem {
    String    islemId;    // Benzersiz işlem kimliği              → "TXN-00042"
    String    sembol;     // Hangi hisse işlem gördü              → "THYAO"
                          // (Hisse nesnesi değil, sadece sembol — neden? ↓)
    IslemTipi tip;        // Alış mı satış mı                     → IslemTipi.AL
    int       miktar;     // Kaç lot                              → 100
    double    fiyat;      // İşlemin gerçekleştiği anlık fiyat    → 245.80
                          // (şu anki fiyattan farklı olabilir!)
    LocalDateTime zaman;  // İşlem zamanı                        → 2024-03-15T10:23:45
}
```

> `sembol` neden `Hisse` nesnesi değil de `String`?
> İşlem geçmişe ait bir kayıttır. O sıradaki fiyat zaten `fiyat` alanında saklanıyor.
> Hisse nesnesini tutsan ve fiyatı sonradan güncelleşen nesneyi referans etsen, tarihi veri bozulur.

---

#### `PiyasaOzeti` — Günün kapanış raporu

```java
public class PiyasaOzeti {
    LocalDate    tarih;               // Hangi güne ait               → 2024-03-15
    List<Hisse>  enCokYukselenler;    // O gün en çok % artan 5 hisse
    List<Hisse>  enCokDusenler;       // O gün en çok % düşen 5 hisse
    List<Hisse>  enCokIslemGorenler;  // Hacme göre en çok el değiştiren 5 hisse
    double       toplamPiyasaDegeri;  // Tüm hisselerin fiyat × hacim toplamı
}
```

> Bu sınıfı sen üretmeyeceksin — Stream API ile hesaplayıp dolduracaksın (Adım 2-3).

---

### Nesneler Arası İlişki

```
┌─────────────────────────────────────────────────────────┐
│                    HisseSenediPiyasasi                   │
│  (Tüm sistemi tutan ana sınıf)                          │
│                                                         │
│  hisseler: Map<sembol, Hisse>  ←──── 15 hisse senedi    │
│  portfoyler: List<Portfoy>     ←──── N yatırımcı        │
│  islemGecmisi: Queue<Islem>    ←──── son N işlem kaydı  │
└─────────────────────────────────────────────────────────┘
          │                    │
          │                    │
          ▼                    ▼
   ┌────────────┐       ┌─────────────────────────┐
   │   Hisse    │       │        Portfoy           │
   │            │       │                          │
   │ sembol     │◄──────│ pozisyonlar:             │
   │ sirketAdi  │       │   Map<String, Integer>   │
   │ sektor     │       │   ("THYAO" → 100)        │
   │ fiyat      │       │                          │
   │ degisim    │       │ Portföy değeri hesabı:   │
   │ hacim      │       │   pozisyonlar üzerinden  │
   └────────────┘       │   hisseler map'inden     │
          ▲             │   fiyat çekilerek yapılır│
          │             └─────────────────────────┘
          │
   ┌────────────┐
   │   Islem    │
   │            │
   │ sembol ────┼──► hisseler.get(sembol) ile erişilir
   │ tip        │    (doğrudan referans yok, gevşek bağlı)
   │ miktar     │
   │ fiyat      │    NOT: fiyat buraya anlık fiyatı kopyalar
   │ zaman      │    çünkü 1 hafta sonra bakınca hangi
   └────────────┘    fiyattan alındığını görmek isteriz
```

---

### İş Kuralları (Bunları Kodlarken Aklında Tut)

| Kural | Açıklama |
|-------|----------|
| Sembol benzersizdir | Aynı sembol ile iki farklı `Hisse` olamaz |
| Fiyat her zaman pozitiftir | `fiyat <= 0` geçersiz veridir |
| `Islem.fiyat` sabit kalır | İşlem kaydedildikten sonra `Hisse.fiyat` değişse de `Islem.fiyat` değişmez |
| Portföyde olmayan hisse satılamaz | SAT işlemi öncesi `pozisyonlar.get(sembol) >= miktar` kontrolü yapılmalı |
| Yetersiz nakit ile alım yapılamaz | AL işlemi öncesi `nakit >= fiyat * miktar` kontrolü yapılmalı |
| `PiyasaOzeti` sadece okunur | Günün anlık verisinden türetilir, dışarıdan set edilmez |

---

### Örnek Senaryo (Kafanda Canlandır)

```
1. Ali'nin portföyü: {"THYAO": 100 lot}, nakit: 50.000 TL

2. THYAO fiyatı 245.80 TL
   → Ali'nin portföy değeri = (100 × 245.80) + 50.000 = 74.580 TL

3. Ali 50 lot GARAN alıyor (fiyat: 89.40 TL)
   → Maliyet: 50 × 89.40 = 4.470 TL
   → Nakit: 50.000 - 4.470 = 45.530 TL
   → pozisyonlar: {"THYAO": 100, "GARAN": 50}
   → Yeni bir Islem nesnesi oluşturulur ve islemGecmisi kuyruğuna eklenir

4. Gün biter, PiyasaOzeti hesaplanır:
   → enCokYukselenler: Stream ile gunlukDegisimYuzdesi'ne göre sıralanır
   → toplamPiyasaDegeri: Stream reduce ile hesaplanır
```

---

## ADIM 1 — Temel Koleksiyonları Kur

**Hedef:** Doğru koleksiyon seçimi ve veri yükleme.

### Görevler

**1.1** `HisseSenediPiyasasi` adında bir sınıf oluştur. İçinde şu koleksiyonları tut:

- Tüm hisseleri sembollerine göre tut → hangi koleksiyon? *(ipucu: sembol benzersizdir)*
- Son 10 işlemi sıra koruyarak tut → hangi koleksiyon?
- En yüksek fiyatlı 5 hisseyi her zaman sıralı tut → hangi koleksiyon? *(ipucu: sınırı koru)*
- İşlem gören sektörleri tekrarsız tut → hangi koleksiyon?

**1.2** Aşağıdaki hisse verilerini manuel olarak sisteme yükle (en az 15 hisse):

```
THYAO  | Türk Hava Yolları  | Havacılık      | 245.80 | +3.2% | 12_500_000
GARAN  | Garanti Bankası    | Bankacılık     | 89.40  | -1.1% |  8_200_000
ASELS  | Aselsan            | Savunma        | 187.60 | +5.7% |  3_400_000
EREGL  | Ereğli Demir Çelik | Metal          | 134.20 | -0.8% |  6_700_000
BIMAS  | BİM Mağazaları     | Perakende      | 456.00 | +0.3% |  2_100_000
SISE   | Şişecam            | Cam            | 62.50  | +1.9% |  9_800_000
KCHOL  | Koç Holding        | Holding        | 198.30 | -2.4% |  4_300_000
FROTO  | Ford Otosan        | Otomotiv       | 923.00 | +4.1% |  1_800_000
AKBNK  | Akbank             | Bankacılık     | 71.20  | -0.6% | 11_200_000
TUPRS  | Tüpraş             | Enerji         | 387.50 | +2.8% |  3_900_000
PGSUS  | Pegasus            | Havacılık      | 678.00 | -3.5% |  2_600_000
EKGYO  | Emlak Konut        | Gayrimenkul    | 28.90  | +0.9% | 15_400_000
OYAKC  | Oyak Çimento       | İnşaat         | 44.60  | +1.4% |  7_800_000
SAHOL  | Sabancı Holding    | Holding        | 109.70 | -1.7% |  5_500_000
VESTL  | Vestel             | Teknoloji      | 53.30  | +6.2% |  4_200_000
```

**1.3** Şu methodları yaz:
- `hisseEkle(Hisse h)`
- `hisseGetir(String sembol)` → Optional dönsün
- `sektorGetir(String sektor)` → o sektördeki tüm hisseleri dönsün
- `toplamHisseSayisi()` → int dönsün

### Tamamlandı Sayılma Kriteri
- Koleksiyon seçimlerini ve **neden** seçtiğini yorum satırı olarak yaz.
- `main` içinde verileri yükleyip konsola yazdır.

---

## ADIM 2 — Stream API ile Temel Analizler

**Hedef:** Intermediate ve terminal operasyonları gerçek verilerle kullan.

### Görevler

**2.1** Aşağıdaki methodları **yalnızca Stream API kullanarak** yaz (döngü yasak):

```java
// Fiyatı 100 TL üzerindeki hisseleri fiyatına göre büyükten küçüğe listele
List<Hisse> pahalıHisseler(double esik)

// Her sektörün ortalama fiyatını hesapla
Map<String, Double> sektorOrtalamaFiyat()

// En yüksek günlük değişime sahip N hisseyi bul (artış ve düşüş ayrı ayrı)
List<Hisse> enCokYukselenler(int n)
List<Hisse> enCokDusenler(int n)

// Toplam piyasa değerini hesapla (fiyat × hacim toplamı)
double toplamPiyasaDegeri()

// Sektörleri ortalama hacme göre sırala (en yüksek hacimli sektör önce)
LinkedHashMap<String, Double> sektoreGoreOrtalamaHacim()

// Hisse sembollerini büyük harfe çevirip virgülle birleştir
String tumSemboller()

// Fiyatı verilen aralıkta olan hisseleri bul [min, max]
List<Hisse> fiyatAraligindakilar(double min, double max)

// Her sektördeki hisse sayısını döndür
Map<String, Long> sektorHisseSayisi()
```

**2.2** Sonuçları `main` içinde güzel formatlı şekilde konsola yazdır.

**2.3 Bonus:** `flatMap` kullanımını zorunlu kılacak şekilde her `Portfoy`'un tuttuğu hisselerin tüm sembollerini tek bir listede topla.

### Tamamlandı Sayılma Kriteri
- Hiçbir `for`/`while` döngüsü kullanılmamış olmalı.
- `Collectors.groupingBy`, `Collectors.toMap`, `Collectors.joining` en az bir kez kullanılmış olmalı.

---

## ADIM 3 — Gelişmiş Stream: Gruplama, Özelleştirme, Birleştirme

**Hedef:** `collect`, `reduce`, özel `Collector`, `Stream.of` / `Stream.concat` gibi ileri operasyonları öğren.

### Görevler

**3.1** Aşağıdaki methodları yaz:

```java
// Sektöre göre grupla; her grubun en pahalı hissesini bul
Map<String, Optional<Hisse>> sektorEnPahaliHisse()

// Sektöre göre grupla → alt gruplama: fiyat 100 üstü mü altı mı?
Map<String, Map<Boolean, List<Hisse>>> sektorFiyatGrubu()

// İstatistik özeti: her sektör için min, max, ortalama, sayı
Map<String, IntSummaryStatistics> sektorFiyatIstatistik()

// reduce ile toplam hacmi hesapla (Collections.stream().reduce() kullan)
long toplamHacim()

// Portföydeki hisselerin toplam değerini hesapla (fiyat × miktar)
double portfoyDegeri(Portfoy p)
```

**3.2** Özel bir `Collector` yaz:
- Hisseleri alan, sektöre göre gruplayıp her sektörün toplam hacmini `TreeMap<String, Long>` olarak dönen bir `Collector`.
- `Collector.of(...)` kullanarak implement et.

**3.3** İki farklı sektörün hisse listelerini `Stream.concat` ile birleştir, ardından işle.

### Tamamlandı Sayılma Kriteri
- `Collector.of(supplier, accumulator, combiner, finisher)` 4 parametresi de doldurulmuş olmalı.
- `groupingBy` içinde downstream collector kullanılmış olmalı.

---

## ADIM 4 — Concurrent Collections ile Thread-Safe Piyasa

**Hedef:** Birden fazla thread aynı anda fiyat güncellerken veri bütünlüğünü koru.

### Görevler

**4.1** `CanlıPiyasa` adında yeni bir sınıf oluştur. Koleksiyonları thread-safe versiyonlarla değiştir:

- Hisse haritası → `ConcurrentHashMap`
- Son 100 işlem kaydı → `LinkedBlockingQueue` (kapasite: 100)
- Anlık fiyat değişimlerini sayan sayaç → `AtomicLong`
- En son güncellenen hisse sembolü → `AtomicReference<String>`

**4.2** Şu 3 thread'i aynı anda çalıştır (ExecutorService kullan):

```
FiyatGüncelleyici  → Her 200ms'de rastgele bir hissenin fiyatını ±%3 değiştir
IslemKayıtçısı     → Her fiyat değişiminde kuyruğa bir Islem nesnesi ekle
EkranYazıcı        → Her 1 saniyede tüm hisselerin anlık fiyatını konsola yazdır
```

**4.3** `ConcurrentHashMap.compute()` kullanarak fiyat güncelleme işlemini atomik yap.

**4.4** 10 saniye çalıştıktan sonra sistemi düzgün kapat (`shutdown` + `awaitTermination`).

**4.5** Programı sonlandırırken `AtomicLong` sayacını yazdır: kaç fiyat güncellemesi yapıldı?

### Tamamlandı Sayılma Kriteri
- `ConcurrentModificationException` hiç fırlatılmamalı.
- `volatile`, `synchronized` blok **kullanmadan** yalnızca concurrent API ile çözüm üretilmeli.

---

## ADIM 5 — Parallel Stream ile Büyük Veri Analizi

**Hedef:** `parallelStream()` ne zaman işe yarar, ne zaman yaramaz — farkı ölç.

### Görevler

**5.1** 500.000 adet sentetik `Hisse` nesnesi üret (rastgele sembol, sektör, fiyat, hacim).

```java
// İpucu: IntStream.range(0, 500_000).mapToObj(...).collect(...)
```

**5.2** Şu işlemleri hem `stream()` hem `parallelStream()` ile yap, süreyi `System.nanoTime()` ile ölç:

```
- Toplam piyasa değeri hesabı (fiyat × hacim)
- Sektöre göre gruplayıp her sektörün ortalama fiyatını bul
- Fiyatı 100-500 arasındaki hisseleri filtrele ve büyükten küçüğe sırala
- Tüm hisse sembollerini büyük harfe çevirip birleştir (Collectors.joining)
```

**5.3** Sonuçları tablolayarak yazdır:

```
İşlem                  | Serial (ms) | Parallel (ms) | Kazanım
-----------------------|-------------|---------------|--------
Toplam piyasa değeri   |      120    |       38      |  3.2x
...
```

**5.4** `Collectors.joining` işleminde neden parallel stream bazen **daha yavaş** çalıştığını yorum satırında açıkla.

**5.5 Bonus:** `ForkJoinPool` özelleştir ve paralel stream'i bu havuzla çalıştır:

```java
ForkJoinPool ozelHavuz = new ForkJoinPool(4);
ozelHavuz.submit(() -> liste.parallelStream()...);
```

### Tamamlandı Sayılma Kriteri
- Ölçüm tablosu konsola yazdırılmış olmalı.
- Hangi işlemlerde parallel'in avantaj sağladığı/sağlamadığı yorum olarak yazılmış olmalı.

---

## ADIM 6 — CompletableFuture ile Asenkron Portföy Sistemi

**Hedef:** Bağımsız ve bağımlı asenkron işlemleri zincirleme ile yönet.

### Görevler

**6.1** Şu asenkron servisleri simüle et (her biri rastgele 500–1500ms geciktirir):

```java
CompletableFuture<Double> dovizKuruGetir(String para)      // USD/TRY, EUR/TRY
CompletableFuture<Hisse>  hisseFiyatGetir(String sembol)   // anlık fiyat
CompletableFuture<String> haberGetir(String sembol)        // son haber başlığı
```

**6.2** Aşağıdaki pipeline'ı kur:

```
[Eş Zamanlı]
├── THYAO fiyatını getir
├── GARAN fiyatını getir
└── USD/TRY kurunu getir
        ↓ (hepsi gelince)
[Birleştir] Portföy değerini hem TRY hem USD cinsinden hesapla
        ↓
[Eş Zamanlı]
├── THYAO haberini getir
└── GARAN haberini getir
        ↓ (hepsi gelince)
[Yazdır] Özet raporu konsola bas
```

**6.3** Hata yönetimi ekle:
- Herhangi bir servis hata verirse (`CompletableFuture.failedFuture(...)`) `exceptionally` ile varsayılan değer kullan.
- Servis 2 saniyeden uzun sürerse timeout uygula (`orTimeout` — Java 9+).

**6.4** `CompletableFuture.allOf()` ve `CompletableFuture.anyOf()` ikisini de kullan; ne zaman hangisinin tercih edilmesi gerektiğini yorum olarak açıkla.

### Tamamlandı Sayılma Kriteri
- `get()` çağrısı **yalnızca** en son sonucu almak için kullanılmış olmalı; zincirleme içinde `get()` kullanılmamalı.
- Hata senaryosu test edilmiş olmalı (bir servisi kasıtlı hata verdirt).

---

## ADIM 7 — Final: Hepsini Birleştir

**Hedef:** Tüm adımları tek bir çalışan sistemde entegre et.

### Görevler

**7.1** `PlatformBaslatici` sınıfı yaz. `main` metodu sırasıyla şunları yapmalı:

1. Hisse verilerini yükle (Adım 1)
2. Anlık piyasa simülasyonunu başlat — 5 saniye çalıştır (Adım 4)
3. 5 saniye sonra anlık snapshot al; 500K sentetik veri ile merge et (Adım 5)
4. Parallel stream ile büyük veri analizini çalıştır (Adım 5)
5. Asenkron portföy raporu üret (Adım 6)
6. Stream analizlerini ekrana yazdır (Adım 2-3)

**7.2** Konsol çıktısı düzenli ve okunabilir olsun:
```
═══════════════════════════════════════
   BORSA ANALİZ PLATFORMU — v1.0
═══════════════════════════════════════
[PIYASA]  15 hisse yüklendi
[SİMÜLASYON]  Başladı... (5 sn)
[SİMÜLASYON]  Bitti — 87 fiyat güncellendi
[ANALİZ]  500.000 kayıt işleniyor...
...
```

**7.3** Tüm `ExecutorService` ve `ForkJoinPool`'ların düzgün kapatıldığından emin ol.

### Tamamlandı Sayılma Kriteri
- Program baştan sona hatasız çalışmalı.
- Kaynak sızıntısı (leak) olmamalı: tüm thread pool'lar kapatılmış olmalı.

---

## Danışma Kuralları

Bir adımda takılırsan şu formatta sor:

```
Adım [numara] — [konu]
Ne yapmaya çalıştım: ...
Aldığım hata / anlamadığım yer: ...
Şu ana kadar yazdığım kod: ...
```

Bu format, sana en hızlı ve en faydalı yanıtı almamı sağlar.

---

## Konu Haritası

| Adım | Collections | Stream API | Parallel / Concurrent |
|------|-------------|------------|-----------------------|
| 1    | Map, Queue, Set, List seçimi | — | — |
| 2    | — | filter, map, sorted, collect, groupingBy | — |
| 3    | — | reduce, flatMap, Collector.of, concat | — |
| 4    | ConcurrentHashMap, LinkedBlockingQueue, AtomicLong | — | ExecutorService, thread-safe ops |
| 5    | — | parallelStream, Collectors | ForkJoinPool, ölçüm |
| 6    | — | — | CompletableFuture, allOf, anyOf, timeout |
| 7    | Hepsi | Hepsi | Hepsi |

---

> Başarılar. Sıkıştığında buradayım.

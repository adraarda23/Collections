# Java Concurrent Collections — Kapsamlı Dokümantasyon

## İçindekiler

1. [Neden Concurrent Collections?](#1-neden-concurrent-collections)
2. [Hiyerarşi ve Genel Yapı](#2-hiyerarşi-ve-genel-yapı)
3. [Temel Kavramlar](#3-temel-kavramlar)
   - [Thread Safety Seviyeleri](#31-thread-safety-seviyeleri)
   - [Atomicity, Visibility, Ordering](#32-atomicity-visibility-ordering)
   - [Lock Mekanizmaları](#33-lock-mekanizmaları)
4. [Concurrent List](#4-concurrent-list)
   - [CopyOnWriteArrayList](#41-copyonwritearraylist)
5. [Concurrent Set](#5-concurrent-set)
   - [CopyOnWriteArraySet](#51-copyonwritearrayset)
   - [ConcurrentSkipListSet](#52-concurrentskiplistset)
6. [Concurrent Map](#6-concurrent-map)
   - [ConcurrentHashMap](#61-concurrenthashmap)
   - [ConcurrentSkipListMap](#62-concurrentskiplistmap)
7. [Blocking Queue (Engelleme Kuyruğu)](#7-blocking-queue-engelleme-kuyruğu)
   - [BlockingQueue Interface](#71-blockingqueue-interface)
   - [ArrayBlockingQueue](#72-arrayblockingqueue)
   - [LinkedBlockingQueue](#73-linkedblockingqueue)
   - [PriorityBlockingQueue](#74-priorityblockingqueue)
   - [DelayQueue](#75-delayqueue)
   - [SynchronousQueue](#76-synchronousqueue)
   - [LinkedTransferQueue](#77-linkedtransferqueue)
8. [Blocking Deque](#8-blocking-deque)
   - [LinkedBlockingDeque](#81-linkedblockingdeque)
9. [Atomic Değişkenler (java.util.concurrent.atomic)](#9-atomic-değişkenler-javautilconcurrentatomic)
   - [AtomicInteger / AtomicLong / AtomicBoolean](#91-atomicinteger--atomiclong--atomicboolean)
   - [AtomicReference](#92-atomicreference)
   - [AtomicIntegerArray / AtomicLongArray](#93-atomicintegerarray--atomiclongarray)
   - [LongAdder / LongAccumulator](#94-longadder--longaccumulator)
10. [Synchronizer Sınıfları](#10-synchronizer-sınıfları)
    - [CountDownLatch](#101-countdownlatch)
    - [CyclicBarrier](#102-cyclicbarrier)
    - [Semaphore](#103-semaphore)
    - [Phaser](#104-phaser)
    - [Exchanger](#105-exchanger)
11. [Lock Mekanizmaları (java.util.concurrent.locks)](#11-lock-mekanizmaları-javautilconcurrentlocks)
    - [ReentrantLock](#111-reentrantlock)
    - [ReentrantReadWriteLock](#112-reentrantreadwritelock)
    - [StampedLock](#113-stampedlock)
    - [Condition](#114-condition)
12. [ExecutorService ve Thread Pool](#12-executorservice-ve-thread-pool)
    - [ThreadPoolExecutor](#121-threadpoolexecutor)
    - [Executors Factory Methodları](#122-executors-factory-methodları)
    - [ScheduledExecutorService](#123-scheduledexecutorservice)
13. [Future ve CompletableFuture](#13-future-ve-completablefuture)
    - [Future\<V\>](#131-futurev)
    - [CompletableFuture\<T\>](#132-completablefuturet)
14. [Karşılaştırma Tablosu](#14-karşılaştırma-tablosu)
15. [Hangi Yapı Ne Zaman Kullanılır?](#15-hangi-yapı-ne-zaman-kullanılır)
16. [Yaygın Anti-Pattern ve Hatalar](#16-yaygın-anti-pattern-ve-hatalar)

---

## 1. Neden Concurrent Collections?

`java.util` altındaki standart koleksiyonlar (`ArrayList`, `HashMap`, vb.) **thread-safe değildir**. Birden fazla thread aynı anda bu yapılara erişirse aşağıdaki sorunlar ortaya çıkabilir:

| Sorun | Açıklama |
|-------|----------|
| **Race Condition** | İki thread aynı anda okuyup yazarsa veri tutarsızlığı oluşur. |
| **ConcurrentModificationException** | Bir thread koleksiyonu dönerken başka bir thread onu değiştirirse fırlatılır. |
| **Veri Kaybı** | `HashMap`'te çakışma olduğunda bazı put işlemleri kaybolabilir. |
| **Sonsuz Döngü** | Java 7 öncesi `HashMap`'te multi-thread kullanımda rehash sırasında döngüsel bağlı liste oluşabilir. |

**Çözüm alternatifleri:**

```
1. Collections.synchronizedXxx()  →  Tüm methodları synchronized yapar, kaba kilitleme
2. java.util.concurrent yapıları  →  Fine-grained locking, lock-free algoritmalar
3. Immutable (değiştirilemez) nesneler kullanmak
```

`java.util.concurrent` yapıları, `synchronized` sarmalayıcılardan çok daha iyi **throughput** (işlem hacmi) sağlar.

---

## 2. Hiyerarşi ve Genel Yapı

```
java.util.concurrent
│
├── Concurrent Collections
│     ├── ConcurrentMap<K,V>
│     │     ├── ConcurrentHashMap<K,V>
│     │     └── ConcurrentSkipListMap<K,V>   (NavigableMap)
│     ├── ConcurrentNavigableMap<K,V>
│     │     └── ConcurrentSkipListMap<K,V>
│     ├── CopyOnWriteArrayList<E>             (List)
│     ├── CopyOnWriteArraySet<E>              (Set)
│     └── ConcurrentSkipListSet<E>            (NavigableSet)
│
├── Blocking Queues
│     ├── BlockingQueue<E>
│     │     ├── ArrayBlockingQueue<E>
│     │     ├── LinkedBlockingQueue<E>
│     │     ├── PriorityBlockingQueue<E>
│     │     ├── DelayQueue<E>
│     │     ├── SynchronousQueue<E>
│     │     └── LinkedTransferQueue<E>        (TransferQueue)
│     └── BlockingDeque<E>
│           └── LinkedBlockingDeque<E>
│
├── Atomic Variables (java.util.concurrent.atomic)
│     ├── AtomicBoolean
│     ├── AtomicInteger / AtomicLong
│     ├── AtomicReference<V>
│     ├── AtomicIntegerArray / AtomicLongArray
│     ├── AtomicStampedReference<V>
│     ├── LongAdder / DoubleAdder
│     └── LongAccumulator / DoubleAccumulator
│
├── Synchronizers
│     ├── CountDownLatch
│     ├── CyclicBarrier
│     ├── Semaphore
│     ├── Phaser
│     └── Exchanger<V>
│
├── Locks (java.util.concurrent.locks)
│     ├── Lock (interface)
│     │     └── ReentrantLock
│     ├── ReadWriteLock (interface)
│     │     └── ReentrantReadWriteLock
│     ├── StampedLock
│     └── Condition (interface)
│
└── Executor Framework
      ├── Executor (interface)
      │     └── ExecutorService (interface)
      │           ├── ThreadPoolExecutor
      │           └── ScheduledExecutorService
      │                 └── ScheduledThreadPoolExecutor
      ├── Executors (factory)
      ├── Future<V> (interface)
      │     └── FutureTask<V>
      └── CompletableFuture<T>
```

---

## 3. Temel Kavramlar

### 3.1 Thread Safety Seviyeleri

| Seviye | Açıklama | Örnek |
|--------|----------|-------|
| **Immutable** | Değiştirilemez; her zaman güvenli. | `String`, `Integer`, `List.of()` |
| **Thread-safe** | Tüm işlemler atomik; dışarıdan ek senkronizasyon gerekmez. | `ConcurrentHashMap`, `AtomicInteger` |
| **Conditionally thread-safe** | Tek methodlar güvenli ama bileşik işlemler güvenli değil. | `Collections.synchronizedList()` |
| **Not thread-safe** | Dışarıdan senkronizasyon gerekir. | `ArrayList`, `HashMap` |

### 3.2 Atomicity, Visibility, Ordering

- **Atomicity:** Bir işlemin bölünemez (ya tam ya hiç) şekilde gerçekleşmesi.
- **Visibility:** Bir thread'in yaptığı değişikliğin diğer thread'ler tarafından görülebilmesi (`volatile`, `happens-before`).
- **Ordering:** JVM ve CPU, performans için komutları yeniden sıralayabilir; `synchronized` ve `volatile` bu yeniden sıralamayı önler.

### 3.3 Lock Mekanizmaları

| Mekanizma | Açıklama |
|-----------|----------|
| `synchronized` | JVM seviyesinde monitör kilidi; basit ama kaba. |
| `volatile` | Görünürlük garantisi; atomicity **vermez**. |
| `ReentrantLock` | `synchronized`'ın esnek alternatifi; timeout, try-lock, fairness. |
| **CAS (Compare-And-Swap)** | Lock-free algoritma; CPU seviyesinde atomik karşılaştır-ve-değiştir. Atomic sınıflar kullanır. |
| **Segment locking** | `ConcurrentHashMap`'in eski yöntemi (Java 7 ve öncesi). |
| **Fine-grained locking** | Her bucket için ayrı kilit; `ConcurrentHashMap` Java 8+ yaklaşımı. |

---

## 4. Concurrent List

### 4.1 CopyOnWriteArrayList

**Özellikler:**
- Her yazma işleminde (add, set, remove) dahili dizi **kopyalanır**; yazma pahalı, okuma çok ucuz.
- Iterator hiçbir zaman `ConcurrentModificationException` fırlatmaz (snapshot alır).
- `null` elemana izin verir.
- **Okuma yoğun, yazma seyrek** senaryolar için idealdir (event listener listeleri, subscriber listeleri).

```java
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

CopyOnWriteArrayList<String> liste = new CopyOnWriteArrayList<>();

// Thread-safe ekleme
liste.add("Elma");
liste.add("Armut");
liste.addIfAbsent("Elma");          // Zaten varsa eklemez → false
liste.addAllAbsent(List.of("Kiraz", "Elma")); // Olmayanları ekler

// Okuma (lock-free)
String eleman = liste.get(0);
int boyut = liste.size();
boolean var = liste.contains("Armut");

// Güncelleme
liste.set(0, "Mango");

// Silme
liste.remove("Armut");
liste.remove(0);

// Snapshot üzerinde döngü (yazma işlemleri etkilemez)
for (String s : liste) {
    // Döngü sırasında başka thread add/remove yapabilir, exception olmaz
    System.out.println(s);
}

// Toplu işlemler
liste.addAll(List.of("A", "B", "C"));
liste.removeAll(List.of("A", "B"));
liste.retainAll(List.of("Mango", "C"));

// subList
List<String> sub = liste.subList(0, 1);

// Stream (snapshot üzerinde çalışır)
liste.stream()
     .filter(s -> s.length() > 4)
     .forEach(System.out::println);
```

**Ne zaman kullanılmaz:**
- Yazma işlemi sık olduğunda (her yazma için dizi kopyalanır → bellek ve CPU maliyeti).
- Büyük listelerle (kopyalama maliyeti artar).

---

## 5. Concurrent Set

### 5.1 CopyOnWriteArraySet

**Özellikler:**
- `CopyOnWriteArrayList` üzerine kurulu; her yazma dizisi kopyalar.
- **Küçük setler** ve **okuma yoğun** senaryolar için uygundur.
- Ekleme `O(n)` (duplicate kontrolü için tüm diziyi tarar).
- `null` elemana izin verir.

```java
import java.util.concurrent.CopyOnWriteArraySet;

CopyOnWriteArraySet<String> set = new CopyOnWriteArraySet<>();

set.add("A");
set.add("B");
set.add("A"); // duplicate — eklenmez

boolean var = set.contains("A");
set.remove("B");
int boyut = set.size();

// Thread-safe iterasyon (snapshot)
for (String s : set) {
    System.out.println(s);
}
```

---

### 5.2 ConcurrentSkipListSet

**Özellikler:**
- Skip List veri yapısına dayanır; doğal sıra veya `Comparator` ile **sıralıdır**.
- `NavigableSet` interface'ini implemente eder → `TreeSet`'in concurrent versiyonu.
- Ekleme, silme, arama: `O(log n)` ortalama.
- `null` elemana izin **vermez**.
- **Thread-safe**; tüm işlemler atomik.

```java
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.NavigableSet;

NavigableSet<Integer> skipSet = new ConcurrentSkipListSet<>();

skipSet.add(5);
skipSet.add(1);
skipSet.add(8);
skipSet.add(3);
// Her zaman sıralı: 1, 3, 5, 8

// NavigableSet methodları (thread-safe)
int ilk = skipSet.first();           // 1
int son = skipSet.last();            // 8
Integer floor = skipSet.floor(4);   // 3
Integer ceil = skipSet.ceiling(4);  // 5
Integer lower = skipSet.lower(5);   // 3
Integer higher = skipSet.higher(5); // 8

skipSet.pollFirst(); // 1 — al ve sil
skipSet.pollLast();  // 8 — al ve sil

NavigableSet<Integer> bas = skipSet.headSet(5, false); // < 5
NavigableSet<Integer> son2 = skipSet.tailSet(5, true); // >= 5
NavigableSet<Integer> aralik = skipSet.subSet(3, true, 8, false); // [3,8)
NavigableSet<Integer> ters = skipSet.descendingSet();

// Thread-safe iterasyon
for (int n : skipSet) {
    System.out.println(n);
}
```

---

## 6. Concurrent Map

### 6.1 ConcurrentHashMap

**Özellikler:**
- Java 8+'da **CAS + synchronized (bucket seviyesi)** kullanır; Java 7'de segment locking kullanıyordu.
- `HashMap`'ten farklı olarak **null anahtar ve null değer** kabul etmez.
- Okuma işlemleri genellikle **lock-free**; yazma işlemleri yalnızca ilgili bucket'ı kilitler.
- **Thread-safe** ve `HashMap`'e yakın performans.
- `size()` yaklaşık değer döner (kesin değil); `mappingCount()` tercih edilir.
- Yüksek concurrent okuma/yazma senaryoları için idealdir.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Standart Map methodları (thread-safe)
map.put("A", 1);
map.put("B", 2);
map.put("C", 3);

int deger = map.get("A");
int varsayilan = map.getOrDefault("Z", -1);

map.remove("B");
boolean anahtarVar = map.containsKey("A");
boolean degerVar = map.containsValue(3);

// Atomic bileşik işlemler
map.putIfAbsent("D", 4);              // D yoksa ekle
map.replace("A", 1, 99);             // A==1 ise 99 yap
map.remove("C", 3);                  // C==3 ise sil

// Java 8+ compute methodları (atomic)
map.computeIfAbsent("E", k -> k.length());        // E yoksa hesapla-ekle
map.computeIfPresent("A", (k, v) -> v + 1);       // A varsa güncelle
map.compute("A", (k, v) -> v == null ? 1 : v + 1); // her durumda

// merge: varsa toplama fonksiyonu uygular, yoksa değeri koyar
map.merge("A", 10, Integer::sum);

// forEach (paralel)
map.forEach(2, (k, v) -> System.out.println(k + "=" + v)); // 2: parallelism threshold

// Bulk işlemler (Java 8+)
long count = map.mappingCount(); // size()'dan daha güvenilir

// search: değer bulununca durur, paralel çalışır
String bulunanAnahtar = map.search(2, (k, v) -> v > 2 ? k : null);

// reduce: tüm değerleri birleştirir
int toplam = map.reduceValues(2, Integer::sum);
int max = map.reduceValues(2, 0, Integer::max);

// keys/values/entries set'leri (live view, thread-safe)
map.keySet().forEach(System.out::println);
map.values().forEach(System.out::println);
map.entrySet().forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));

// KeySetView (ConcurrentHashMap.newKeySet)
var keySet = ConcurrentHashMap.newKeySet();
keySet.add("x");
keySet.add("y");
```

**Kelime Sayacı Örneği (klasik kullanım):**

```java
ConcurrentHashMap<String, Integer> kelimeSayaci = new ConcurrentHashMap<>();

String[] kelimeler = {"elma", "armut", "elma", "kiraz", "armut", "elma"};

for (String kelime : kelimeler) {
    kelimeSayaci.merge(kelime, 1, Integer::sum);
    // veya:
    // kelimeSayaci.compute(kelime, (k, v) -> v == null ? 1 : v + 1);
}
// {elma=3, armut=2, kiraz=1}
kelimeSayaci.forEach((k, v) -> System.out.println(k + ": " + v));
```

---

### 6.2 ConcurrentSkipListMap

**Özellikler:**
- Skip List tabanlı; anahtarları **sıralı** tutar → `TreeMap`'in concurrent versiyonu.
- `ConcurrentNavigableMap` interface'ini implemente eder.
- Ekleme, silme, arama: `O(log n)` ortalama.
- `null` anahtar veya değer **kabul etmez**.
- **Thread-safe**; tüm işlemler atomik.

```java
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentNavigableMap;

ConcurrentNavigableMap<String, Integer> skipMap = new ConcurrentSkipListMap<>();

skipMap.put("C", 3);
skipMap.put("A", 1);
skipMap.put("B", 2);
// Alfabetik sıra: A, B, C

// SortedMap / NavigableMap methodları (thread-safe)
String ilkAnahtar = skipMap.firstKey();   // "A"
String sonAnahtar = skipMap.lastKey();    // "C"
Map.Entry<String, Integer> ilkGiris = skipMap.firstEntry();
Map.Entry<String, Integer> sonGiris = skipMap.lastEntry();

skipMap.floorKey("B");    // "B"
skipMap.ceilingKey("B");  // "B"
skipMap.lowerKey("B");    // "A"
skipMap.higherKey("B");   // "C"

Map.Entry<String, Integer> pollIlk = skipMap.pollFirstEntry(); // al+sil
Map.Entry<String, Integer> pollSon = skipMap.pollLastEntry();  // al+sil

ConcurrentNavigableMap<String, Integer> bas = skipMap.headMap("B", false); // < B
ConcurrentNavigableMap<String, Integer> son = skipMap.tailMap("B", true);  // >= B
ConcurrentNavigableMap<String, Integer> aralik = skipMap.subMap("A", true, "C", false);
ConcurrentNavigableMap<String, Integer> ters = skipMap.descendingMap();

// Atomic bileşik işlemler
skipMap.putIfAbsent("D", 4);
skipMap.replace("A", 1, 99);
skipMap.remove("B", 2);
```

---

## 7. Blocking Queue (Engelleme Kuyruğu)

### 7.1 BlockingQueue Interface

**Engelleme (Blocking) kuyruğu**, Producer-Consumer (üretici-tüketici) deseni için tasarlanmıştır.

- Kuyruk **doluysa** `put()` bloklayarak bekler.
- Kuyruk **boşsa** `take()` bloklayarak bekler.
- Timeout versiyonlarıyla belirli süre beklenir.

| İşlem | Exception Fırlatır | Özel Değer | Bloklayarak Bekler | Timeout ile Bekler |
|-------|--------------------|-----------|--------------------|--------------------|
| Ekleme | `add(e)` | `offer(e)` | `put(e)` | `offer(e, t, unit)` |
| Alma | `remove()` | `poll()` | `take()` | `poll(t, unit)` |
| Bakma | `element()` | `peek()` | — | — |
| Tümünü Aktar | — | `drainTo(c)` | — | `drainTo(c, maxElements)` |

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

BlockingQueue<String> kuyruk = new ArrayBlockingQueue<>(5);

// Engelleme olmayan
kuyruk.offer("A");          // doluysa false döner
kuyruk.poll();              // boşsa null döner

// Engelleme (thread bloklayabilir)
kuyruk.put("B");            // dolu ise thread uyur
String alinacak = kuyruk.take(); // boşsa thread uyur

// Timeout ile
boolean eklendi = kuyruk.offer("C", 1, TimeUnit.SECONDS);
String alinan = kuyruk.poll(1, TimeUnit.SECONDS);

// Toplu alma
List<String> liste = new ArrayList<>();
int alinanSayi = kuyruk.drainTo(liste);           // tümünü al
int alinanSayi2 = kuyruk.drainTo(liste, 3);       // en fazla 3 al
```

---

### 7.2 ArrayBlockingQueue

**Özellikler:**
- **Sınırlı (bounded)** kapasiteli; dizi tabanlı FIFO kuyruğu.
- Kapasite baştan belirlenir, sonradan değiştirilemez.
- **Fairness** (adalet) modu: true ile bekleyen thread'ler FIFO sırasıyla uyandırılır (varsayılan false; daha yavaş ama adil).
- Producer ve consumer tek bir kilit paylaşır.

```java
import java.util.concurrent.ArrayBlockingQueue;

// Kapasite 10, fairness false (varsayılan)
ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue<>(10);

// Fairness true (adil sıra, biraz daha yavaş)
ArrayBlockingQueue<Integer> adilAbq = new ArrayBlockingQueue<>(10, true);

abq.put(1);
abq.put(2);
int alinan = abq.take(); // 1 (FIFO)

int kalan = abq.remainingCapacity(); // Dolu olmayan alan
int boyut = abq.size();

// Producer-Consumer örneği
Thread uretici = new Thread(() -> {
    try {
        for (int i = 0; i < 5; i++) {
            abq.put(i);
            System.out.println("Üretildi: " + i);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

Thread tuketici = new Thread(() -> {
    try {
        for (int i = 0; i < 5; i++) {
            int deger = abq.take();
            System.out.println("Tüketildi: " + deger);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

uretici.start();
tuketici.start();
```

---

### 7.3 LinkedBlockingQueue

**Özellikler:**
- Bağlı düğüm tabanlı FIFO kuyruğu.
- Opsiyonel kapasite sınırı (belirtilmezse `Integer.MAX_VALUE` — pratik olarak sınırsız).
- Producer ve consumer **ayrı kilitler** kullanır → `ArrayBlockingQueue`'dan daha yüksek throughput.
- `null` elemana izin **vermez**.

```java
import java.util.concurrent.LinkedBlockingQueue;

// Sınırsız (dikkat: OutOfMemoryError riski)
LinkedBlockingQueue<String> sinirsiz = new LinkedBlockingQueue<>();

// Sınırlı
LinkedBlockingQueue<String> sinirli = new LinkedBlockingQueue<>(100);

sinirli.put("A");
sinirli.put("B");
String alinan = sinirli.take(); // "A"

int kalan = sinirli.remainingCapacity();

// Peek (silmeden bak)
String bas = sinirli.peek(); // null olabilir

// Toplu alma
List<String> liste = new ArrayList<>();
sinirli.drainTo(liste, 50); // en fazla 50 al
```

---

### 7.4 PriorityBlockingQueue

**Özellikler:**
- **Sınırsız** (unbounded) öncelikli kuyruk; min-heap tabanlı.
- Her zaman **en küçük eleman** (doğal sıra) veya `Comparator`'a göre en öncelikli çıkar.
- `null` elemana izin **vermez**.
- `take()` yalnızca kuyruk **boşsa** bloklayabilir (dolu olamaz).
- `peek()` ve `poll()` lock kullanır; eleman sırası kesinlikle garanti edilmez iterasyonda.

```java
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;

// Min-heap (doğal sıra)
PriorityBlockingQueue<Integer> pbq = new PriorityBlockingQueue<>();
pbq.put(5);
pbq.put(1);
pbq.put(3);

System.out.println(pbq.take()); // 1 (en küçük)
System.out.println(pbq.take()); // 3

// Özel Comparator (aciliyet seviyesine göre)
record Gorev(String isim, int oncelik) {}

PriorityBlockingQueue<Gorev> gorevKuyrugu = new PriorityBlockingQueue<>(
    11, Comparator.comparingInt(Gorev::oncelik)
);
gorevKuyrugu.put(new Gorev("Düşük Öncelik", 3));
gorevKuyrugu.put(new Gorev("Yüksek Öncelik", 1));
gorevKuyrugu.put(new Gorev("Orta Öncelik", 2));

Gorev ilk = gorevKuyrugu.take(); // Yüksek Öncelik (oncelik=1)
```

---

### 7.5 DelayQueue

**Özellikler:**
- Elemanlar yalnızca **gecikme süresi dolunca** alınabilir.
- Elemanlar `Delayed` interface'ini implemente etmelidir.
- Sınırsız kapasiteli.
- Zamanlayıcı görevler, oturum sona erme, cache eviction için kullanılır.

```java
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

// Delayed implemente eden sınıf
record ZamanliGorev(String isim, long zamanMs) implements Delayed {
    @Override
    public long getDelay(TimeUnit unit) {
        long kalan = zamanMs - System.currentTimeMillis();
        return unit.convert(kalan, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS),
                            other.getDelay(TimeUnit.MILLISECONDS));
    }
}

DelayQueue<ZamanliGorev> delayQueue = new DelayQueue<>();

long simdi = System.currentTimeMillis();
delayQueue.put(new ZamanliGorev("2 saniye sonra", simdi + 2000));
delayQueue.put(new ZamanliGorev("1 saniye sonra", simdi + 1000));
delayQueue.put(new ZamanliGorev("3 saniye sonra", simdi + 3000));

// take() süresi dolmuş en yakın elemanı döner; dolmamışsa bloklayarak bekler
ZamanliGorev gorev = delayQueue.take(); // ~1 saniye bekler → "1 saniye sonra"
```

---

### 7.6 SynchronousQueue

**Özellikler:**
- **Kapasitesi yoktur** (0 kapasiteli); her `put()` bir `take()` ile eşleşmeden tamamlanmaz.
- Producer, consumer hazır olana kadar **bloklayarak bekler** (ve tam tersi).
- Direkt el değiştirme (handoff) kanalı gibi davranır.
- `Executors.newCachedThreadPool()` içinde kullanılır.

```java
import java.util.concurrent.SynchronousQueue;

SynchronousQueue<String> sq = new SynchronousQueue<>();

Thread uretici = new Thread(() -> {
    try {
        System.out.println("Üretici bekliyor...");
        sq.put("Merhaba");  // Tüketici hazır olana kadar bekler
        System.out.println("Üretici teslim etti.");
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
});

Thread tuketici = new Thread(() -> {
    try {
        Thread.sleep(1000);  // Biraz bekle
        String mesaj = sq.take(); // Üretici hazır olana kadar bekler
        System.out.println("Tüketici aldı: " + mesaj);
    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
});

uretici.start();
tuketici.start();
```

---

### 7.7 LinkedTransferQueue

**Özellikler:**
- `TransferQueue` interface'ini implemente eder; `LinkedBlockingQueue`'nun gelişmiş versiyonu.
- **Sınırsız** kapasiteli.
- `transfer(e)`: Consumer hazır değilse bloklayarak bekler (SynchronousQueue gibi).
- `tryTransfer(e)`: Consumer hazır değilse hemen `false` döner (bloklama yok).
- `hasWaitingConsumer()`: Bekleyen consumer var mı?
- `getWaitingConsumerCount()`: Bekleyen consumer sayısı.

```java
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

LinkedTransferQueue<String> ltq = new LinkedTransferQueue<>();

// Normal ekleme (consumer beklemiyorsa kuyruğa bırakır)
ltq.put("Normal");
ltq.offer("Offer");

// transfer: consumer beklemiyorsa bloklayarak bekler
// (ayrı thread'de çalıştırılmalı)
// ltq.transfer("Transfer");

// tryTransfer: consumer yoksa hemen false döner
boolean iletildi = ltq.tryTransfer("TryTransfer");

// Timeout ile
boolean iletildi2 = ltq.tryTransfer("Timeout", 500, TimeUnit.MILLISECONDS);

System.out.println("Bekleyen consumer: " + ltq.hasWaitingConsumer());
System.out.println("Bekleyen sayısı: " + ltq.getWaitingConsumerCount());
```

---

## 8. Blocking Deque

### 8.1 LinkedBlockingDeque

**Özellikler:**
- Her iki uçtan da engelleme ile ekleme/alma yapılabilen `Deque`.
- Opsiyonel kapasite sınırı (varsayılan `Integer.MAX_VALUE`).
- Work-stealing algoritmaları için kullanılır.

```java
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

LinkedBlockingDeque<String> deque = new LinkedBlockingDeque<>(10);

// Her iki uçtan engelleme ile ekleme
deque.putFirst("Baş");
deque.putLast("Son");

// Her iki uçtan alma
String bas = deque.takeFirst();
String son = deque.takeLast();

// Timeout ile
deque.offerFirst("X", 1, TimeUnit.SECONDS);
deque.offerLast("Y", 1, TimeUnit.SECONDS);
String x = deque.pollFirst(1, TimeUnit.SECONDS);
String y = deque.pollLast(1, TimeUnit.SECONDS);

// Bakma (silmeden)
String ilk = deque.peekFirst();
String ilk2 = deque.peekLast();

int kalan = deque.remainingCapacity();
```

---

## 9. Atomic Değişkenler (java.util.concurrent.atomic)

Atomic sınıflar, **CAS (Compare-And-Swap)** CPU instruction'ını kullanarak **lock olmadan** atomik işlem yapar.

### 9.1 AtomicInteger / AtomicLong / AtomicBoolean

```java
import java.util.concurrent.atomic.*;

AtomicInteger sayac = new AtomicInteger(0);

// Okuma
int deger = sayac.get();

// Yazma
sayac.set(10);
sayac.lazySet(5); // Görünürlük garantisi daha zayıf ama daha hızlı

// Atomik artırma/azaltma
int eskiDeger = sayac.getAndIncrement(); // deger++ gibi, eskisini döner
int yeniDeger = sayac.incrementAndGet(); // ++deger gibi, yenisini döner
int eski2 = sayac.getAndDecrement();     // deger--
int yeni2 = sayac.decrementAndGet();     // --deger
int eski3 = sayac.getAndAdd(5);          // deger += 5, eskisini döner
int yeni3 = sayac.addAndGet(5);          // deger += 5, yenisini döner

// CAS (Compare-And-Swap)
boolean basarili = sayac.compareAndSet(10, 99);     // 10 ise 99 yap
int sonuc = sayac.compareAndExchange(99, 0);         // 99 ise 0 yap; önceki değeri döner

// Java 9+
int eski4 = sayac.getAndUpdate(v -> v * 2);          // lambda ile güncelle
int yeni4 = sayac.updateAndGet(v -> v * 2);
int eski5 = sayac.getAndAccumulate(10, Integer::sum); // değer ile birleştir
int yeni5 = sayac.accumulateAndGet(10, Integer::sum);

// AtomicLong — aynı API, long için
AtomicLong uzunSayac = new AtomicLong(0L);
uzunSayac.incrementAndGet();

// AtomicBoolean
AtomicBoolean bayrak = new AtomicBoolean(false);
boolean eskiBayrak = bayrak.getAndSet(true);          // false, şimdi true
boolean cas = bayrak.compareAndSet(true, false);      // true ise false yap
```

---

### 9.2 AtomicReference

Herhangi bir nesne referansını atomik olarak güncellemek için kullanılır.

```java
import java.util.concurrent.atomic.AtomicReference;

AtomicReference<String> ref = new AtomicReference<>("başlangıç");

String eski = ref.get();
ref.set("yeni değer");

boolean basarili = ref.compareAndSet("yeni değer", "güncel");
String eskiRef = ref.getAndSet("son");

// Lambda ile güncelleme (Java 9+)
ref.updateAndGet(s -> s.toUpperCase());

// AtomicStampedReference — ABA problemini çözer
import java.util.concurrent.atomic.AtomicStampedReference;

AtomicStampedReference<String> stampedRef =
    new AtomicStampedReference<>("değer", 0);

int[] stamHolder = new int[1];
String deger = stampedRef.get(stamHolder); // değer + stamp alır
int stamp = stamHolder[0];

// Hem değer hem stamp eşleşmezse güncelleme olmaz (ABA safe)
boolean guncellendi = stampedRef.compareAndSet("değer", "yeni", stamp, stamp + 1);
```

---

### 9.3 AtomicIntegerArray / AtomicLongArray

Dizi elemanlarını atomik olarak güncellemek için kullanılır.

```java
import java.util.concurrent.atomic.AtomicIntegerArray;

AtomicIntegerArray dizi = new AtomicIntegerArray(5); // [0,0,0,0,0]
// veya mevcut dizi ile
AtomicIntegerArray dizi2 = new AtomicIntegerArray(new int[]{1, 2, 3});

dizi.set(0, 10);
int deger = dizi.get(0);
int eskiD = dizi.getAndIncrement(0);  // dizi[0]++
int yeniD = dizi.incrementAndGet(1);  // ++dizi[1]
boolean cas = dizi.compareAndSet(0, 10, 99); // dizi[0]==10 ise 99 yap
dizi.getAndAdd(2, 5); // dizi[2] += 5
int uzunluk = dizi.length();
```

---

### 9.4 LongAdder / LongAccumulator

Çok sayıda thread'in sık artırma yaptığı senaryolarda `AtomicLong`'dan çok daha hızlıdır (her thread kendi cell'inde çalışır, sonuçta toplanır).

```java
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.atomic.LongAccumulator;

// LongAdder — sadece toplama için optimize
LongAdder sayac = new LongAdder();
sayac.increment();       // +1
sayac.decrement();       // -1
sayac.add(5);            // +5
long toplam = sayac.sum();      // anlık toplam (kesin değil, yaklaşık)
long sifirla = sayac.sumThenReset(); // toplamı al ve sıfırla
sayac.reset();           // 0'a sıfırla

// LongAccumulator — özel fonksiyon ile birleştirme
LongAccumulator maxBulucu = new LongAccumulator(Long::max, Long.MIN_VALUE);
maxBulucu.accumulate(5);
maxBulucu.accumulate(12);
maxBulucu.accumulate(3);
long max = maxBulucu.get(); // 12

LongAccumulator carpan = new LongAccumulator((x, y) -> x * y, 1L);
carpan.accumulate(2);
carpan.accumulate(3);
carpan.accumulate(4);
long carpim = carpan.get(); // 24
```

**AtomicLong vs LongAdder:**
- Düşük contention (az thread): `AtomicLong` yeterli.
- Yüksek contention (çok thread, sık artırma): `LongAdder` daha hızlı.
- `LongAdder.sum()` kesin değil; okunurken yazma devam edebilir.

---

## 10. Synchronizer Sınıfları

### 10.1 CountDownLatch

Bir veya birden fazla thread'i, belirtilen sayıda olayın gerçekleşmesini bekletir. **Tek kullanımlıktır** (reset edilemez).

```java
import java.util.concurrent.CountDownLatch;

// 3 işin bitmesini bekle
CountDownLatch latch = new CountDownLatch(3);

Runnable is = () -> {
    try {
        System.out.println(Thread.currentThread().getName() + " çalışıyor");
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        latch.countDown(); // Sayacı 1 azalt
        System.out.println(Thread.currentThread().getName() + " bitti (kalan: " + latch.getCount() + ")");
    }
};

new Thread(is, "İş-1").start();
new Thread(is, "İş-2").start();
new Thread(is, "İş-3").start();

latch.await(); // 3 countDown olana kadar bloklayarak bekle
System.out.println("Tüm işler tamamlandı!");

// Timeout ile bekleme
boolean bitti = latch.await(5, TimeUnit.SECONDS);
if (!bitti) System.out.println("Zaman aşımı!");
```

---

### 10.2 CyclicBarrier

Birden fazla thread'i bir **buluşma noktasında** toplar; hepsi gelince devam eder. **Yeniden kullanılabilir** (reset edilebilir).

```java
import java.util.concurrent.CyclicBarrier;

int threadSayisi = 3;
CyclicBarrier barrier = new CyclicBarrier(threadSayisi, () -> {
    // Tüm thread'ler gelince çalışacak aksiyon (opsiyonel)
    System.out.println("Tüm thread'ler bariyer noktasına ulaştı!");
});

Runnable gorev = () -> {
    try {
        System.out.println(Thread.currentThread().getName() + " çalışıyor...");
        Thread.sleep((long) (Math.random() * 2000));
        System.out.println(Thread.currentThread().getName() + " bariyerde bekliyor");

        barrier.await(); // Hepsi gelene kadar bekle

        System.out.println(Thread.currentThread().getName() + " devam ediyor");
    } catch (Exception e) {
        Thread.currentThread().interrupt();
    }
};

for (int i = 0; i < threadSayisi; i++) {
    new Thread(gorev, "Thread-" + i).start();
}

// Reset (tüm bekleyen thread'lere BrokenBarrierException fırlatır)
// barrier.reset();

// Kaç kişi beklediği
int bekleyen = barrier.getNumberWaiting();
boolean kirik = barrier.isBroken();
```

---

### 10.3 Semaphore

**İzin (permit)** sayısını yönetir; belirli sayıda thread'in aynı anda kaynağa erişmesine izin verir.

```java
import java.util.concurrent.Semaphore;

// Maksimum 3 thread aynı anda veritabanına bağlanabilir
Semaphore semaphore = new Semaphore(3);

// Adil Semaphore (FIFO sırası, biraz yavaş)
Semaphore adilSemaphore = new Semaphore(3, true);

Runnable gorev = () -> {
    try {
        semaphore.acquire();          // 1 izin al (yoksa bloklayarak bekle)
        // veya: semaphore.acquire(2); // 2 izin birden al

        System.out.println(Thread.currentThread().getName() + " kaynağa erişiyor");
        Thread.sleep(1000);

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        semaphore.release();          // 1 izin geri ver
        // veya: semaphore.release(2);
        System.out.println(Thread.currentThread().getName() + " kaynağı bıraktı");
    }
};

// Bloklama olmadan dene
boolean izinAlindi = semaphore.tryAcquire();
boolean izinAlindi2 = semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);

int mevcutIzin = semaphore.availablePermits();
int bekleyenThread = semaphore.getQueueLength();
boolean adil = semaphore.isFair();

for (int i = 0; i < 6; i++) {
    new Thread(gorev, "Thread-" + i).start();
}
```

---

### 10.4 Phaser

`CountDownLatch` + `CyclicBarrier` kombinasyonu; dinamik katılımcı sayısı ve çok aşamalı eşitleme için kullanılır.

```java
import java.util.concurrent.Phaser;

Phaser phaser = new Phaser(1); // 1 = "main" thread kaydolur

// Thread kayıt
phaser.register();            // 1 katılımcı ekle
phaser.bulkRegister(3);       // 3 katılımcı ekle

Runnable gorev = () -> {
    phaser.arriveAndAwaitAdvance(); // Aşamayı tamamla, hepsi gelene kadar bekle
    System.out.println("Faz 1 tamamlandı");

    phaser.arriveAndAwaitAdvance(); // Faz 2
    System.out.println("Faz 2 tamamlandı");

    phaser.arriveAndDeregister();   // Katılımı bitir ve kayıt sil
};

// Fazı tamamla ama beklemeden devam et
phaser.arrive();

// Mevcut faz numarası
int faz = phaser.getPhase();
int kayitli = phaser.getRegisteredParties();
int gelen = phaser.getArrivedParties();
int gelmesi = phaser.getUnarrivedParties();
boolean sonlandi = phaser.isTerminated();

// Phaser'ı sonlandır
phaser.forceTermination();
```

---

### 10.5 Exchanger

İki thread arasında nesne **karşılıklı takas** etmek için kullanılır.

```java
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

Exchanger<String> exchanger = new Exchanger<>();

Thread uretici = new Thread(() -> {
    try {
        String uretimVerisi = "Üretici verisi";
        System.out.println("Üretici gönderdi: " + uretimVerisi);
        String alinan = exchanger.exchange(uretimVerisi); // Tüketiciyi bekler
        System.out.println("Üretici aldı: " + alinan);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

Thread tuketici = new Thread(() -> {
    try {
        String tuketimVerisi = "Tüketici verisi";
        System.out.println("Tüketici gönderdi: " + tuketimVerisi);
        String alinan = exchanger.exchange(tuketimVerisi); // Üreticiyi bekler
        System.out.println("Tüketici aldı: " + alinan);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

// Timeout ile takas
// exchanger.exchange("veri", 2, TimeUnit.SECONDS);

uretici.start();
tuketici.start();
```

---

## 11. Lock Mekanizmaları (java.util.concurrent.locks)

### 11.1 ReentrantLock

`synchronized`'ın esnek alternatifi; aynı thread kilidini birden fazla kez alabilir (reentrant).

| Method | Açıklama |
|--------|----------|
| `lock()` | Kilidi alır; müsait değilse bloklayarak bekler. |
| `lockInterruptibly()` | Kilit alırken interrupt edilebilir. |
| `tryLock()` | Kilitlerse `true`, değilse hemen `false`. |
| `tryLock(time, unit)` | Belirli süre dener. |
| `unlock()` | Kilidi bırakır (`finally` bloğunda çağrılmalı!). |
| `isLocked()` | Kilit tutuluyorsa `true`. |
| `isHeldByCurrentThread()` | Mevcut thread tutuyorsa `true`. |
| `getHoldCount()` | Mevcut thread'in kilit sayısı. |
| `getQueueLength()` | Bekleyen thread sayısı. |
| `isFair()` | Adil modda mı? |

```java
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock kilit = new ReentrantLock();
// Adil mod (fairness=true): FIFO sırası, daha yavaş ama adil
ReentrantLock adilKilit = new ReentrantLock(true);

// Temel kullanım
kilit.lock();
try {
    // Kritik bölge
    System.out.println("Kritik bölge çalışıyor");
} finally {
    kilit.unlock(); // MUTLAKA finally içinde!
}

// Try-lock (non-blocking)
if (kilit.tryLock()) {
    try {
        // Hemen kilitlenebildi
    } finally {
        kilit.unlock();
    }
} else {
    System.out.println("Kilit alınamadı, başka şey yap");
}

// Timeout ile try-lock
try {
    if (kilit.tryLock(1, TimeUnit.SECONDS)) {
        try {
            // Kilit alındı
        } finally {
            kilit.unlock();
        }
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}

// Interrupt edilebilir kilit
try {
    kilit.lockInterruptibly();
    try {
        // Kritik bölge
    } finally {
        kilit.unlock();
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    // Thread interrupt edildi, kilit alamadı
}
```

---

### 11.2 ReentrantReadWriteLock

**Okuma-Yazma kilidi**: Birden fazla thread eş zamanlı okuyabilir, ama yazma sırasında yalnızca bir thread çalışır.

```java
import java.util.concurrent.locks.ReentrantReadWriteLock;

ReentrantReadWriteLock rwKilit = new ReentrantReadWriteLock();
ReentrantReadWriteLock.ReadLock okumaKilidi = rwKilit.readLock();
ReentrantReadWriteLock.WriteLock yazmaKilidi = rwKilit.writeLock();

// Okuma (aynı anda birden fazla thread okuyabilir)
okumaKilidi.lock();
try {
    System.out.println("Veri okunuyor...");
} finally {
    okumaKilidi.unlock();
}

// Yazma (tek thread; okumalar da engellenir)
yazmaKilidi.lock();
try {
    System.out.println("Veri yazılıyor...");
} finally {
    yazmaKilidi.unlock();
}

// Bilgi methodları
int okuyucuSayisi = rwKilit.getReadLockCount();
int yazmaKuyrugu = rwKilit.getQueueLength();
boolean yazmaKilitli = rwKilit.isWriteLocked();
boolean mevcutThreadYazma = rwKilit.isWriteLockedByCurrentThread();

// Pratik örnek: Thread-safe cache
class GuvenliCache {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, String> cache = new HashMap<>();

    public String oku(String anahtar) {
        lock.readLock().lock();
        try {
            return cache.get(anahtar);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void yaz(String anahtar, String deger) {
        lock.writeLock().lock();
        try {
            cache.put(anahtar, deger);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

---

### 11.3 StampedLock

Java 8 ile eklendi; `ReentrantReadWriteLock`'tan daha yüksek performanslı. **Optimistik okuma** desteği.

| Method | Açıklama |
|--------|----------|
| `writeLock()` | Yazma kilidi al; stamp döner. |
| `readLock()` | Okuma kilidi al; stamp döner. |
| `tryOptimisticRead()` | Kilitsiz stamp al (0 ise kilit var). |
| `validate(stamp)` | Stamp hâlâ geçerliyse `true`. |
| `unlockWrite(stamp)` / `unlockRead(stamp)` | Kilidi bırak. |
| `tryConvertToWriteLock(stamp)` | Okuma kilidini yazma kilidine çevir. |
| `tryConvertToReadLock(stamp)` | Yazma kilidini okuma kilidine çevir. |

```java
import java.util.concurrent.locks.StampedLock;

StampedLock stamped = new StampedLock();
double x = 0, y = 0;

// Yazma
long stamp = stamped.writeLock();
try {
    x = 1.0;
    y = 2.0;
} finally {
    stamped.unlockWrite(stamp);
}

// Kilitli okuma
stamp = stamped.readLock();
try {
    System.out.println(x + ", " + y);
} finally {
    stamped.unlockRead(stamp);
}

// Optimistik okuma (kilit almadan; yazma olursa yeniden oku)
long optimistikStamp = stamped.tryOptimisticRead();
double okunanX = x;
double okunanY = y;

if (!stamped.validate(optimistikStamp)) {
    // Bu arada yazma oldu; kilitli okumaya geç
    stamp = stamped.readLock();
    try {
        okunanX = x;
        okunanY = y;
    } finally {
        stamped.unlockRead(stamp);
    }
}
System.out.println(okunanX + ", " + okunanY);
```

---

### 11.4 Condition

`ReentrantLock` ile kullanılır; `synchronized` + `wait()`/`notify()`'ın daha esnek alternatifi.

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock kilit = new ReentrantLock();
Condition bos = kilit.newCondition();
Condition dolu = kilit.newCondition();
int kapasite = 5;
Queue<Integer> kuyruk = new LinkedList<>();

// Producer
void uret(int deger) throws InterruptedException {
    kilit.lock();
    try {
        while (kuyruk.size() == kapasite) {
            dolu.await(); // Kuyruk doluysa bekle (kilidi geçici bırakır)
        }
        kuyruk.offer(deger);
        bos.signalAll(); // Tüketicileri uyandır
    } finally {
        kilit.unlock();
    }
}

// Consumer
int tuket() throws InterruptedException {
    kilit.lock();
    try {
        while (kuyruk.isEmpty()) {
            bos.await(); // Kuyruk boşsa bekle
        }
        int deger = kuyruk.poll();
        dolu.signalAll(); // Üreticileri uyandır
        return deger;
    } finally {
        kilit.unlock();
    }
}
// Diğer Condition methodları:
// condition.await(1, TimeUnit.SECONDS); // timeout
// condition.awaitUntil(Date); // deadline
// condition.awaitUninterruptibly(); // interrupt edilemez
// condition.signal(); // tek bir thread uyandır
```

---

## 12. ExecutorService ve Thread Pool

### 12.1 ThreadPoolExecutor

Manuel thread oluşturmak yerine **thread havuzu** kullanılır; thread oluşturma maliyeti azalır, thread sayısı kontrol altına alınır.

```java
import java.util.concurrent.*;

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    2,                               // corePoolSize: her zaman aktif thread
    5,                               // maximumPoolSize: maksimum thread
    60L, TimeUnit.SECONDS,           // keepAliveTime: boşta kalma süresi
    new ArrayBlockingQueue<>(100),   // workQueue: görev kuyruğu
    new ThreadFactory() {            // threadFactory (opsiyonel)
        int sayac = 0;
        public Thread newThread(Runnable r) {
            return new Thread(r, "Havuz-Thread-" + sayac++);
        }
    },
    new ThreadPoolExecutor.CallerRunsPolicy() // rejectionPolicy
);

// Görev gönderme
executor.execute(() -> System.out.println("Görev çalışıyor"));
Future<String> future = executor.submit(() -> "Sonuç");

// Bilgi methodları
int aktifThread = executor.getActiveCount();
long tamamlananGorev = executor.getCompletedTaskCount();
long toplamGorev = executor.getTaskCount();
int havuzBoyutu = executor.getPoolSize();

// Kapatma
executor.shutdown();           // Yeni görev almaz, mevcutları tamamlar
executor.shutdownNow();        // Çalışan görevleri interrupt eder; listesini döner
boolean bitti = executor.awaitTermination(10, TimeUnit.SECONDS);
boolean kapali = executor.isShutdown();
boolean sonlandi = executor.isTerminated();
```

**Rejection Policy'ler:**

| Policy | Davranış |
|--------|----------|
| `AbortPolicy` (varsayılan) | `RejectedExecutionException` fırlatır. |
| `CallerRunsPolicy` | Görevi çağıran thread'de çalıştırır (geri baskı sağlar). |
| `DiscardPolicy` | Görevi sessizce atar. |
| `DiscardOldestPolicy` | Kuyruktaki en eski görevi atar, yenisini ekler. |

---

### 12.2 Executors Factory Methodları

```java
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

// Sabit boyutlu havuz
ExecutorService fixed = Executors.newFixedThreadPool(4);
// newFixedThreadPool(4, threadFactory) — özel factory

// Tek thread'li (sıralı çalışma garantisi)
ExecutorService single = Executors.newSingleThreadExecutor();

// Dinamik büyüyen havuz (60s boşta kalanlar silinir)
ExecutorService cached = Executors.newCachedThreadPool();

// Work-stealing havuzu (Java 8+; ForkJoinPool tabanlı)
ExecutorService stealing = Executors.newWorkStealingPool();
ExecutorService stealing2 = Executors.newWorkStealingPool(4); // parallelism

// Tüm havuzlar için aynı API
fixed.execute(() -> System.out.println("Görev"));
Future<Integer> f = fixed.submit(() -> 42);
fixed.shutdown();

// Çok sayıda görev toplu gönderme
List<Callable<String>> gorevler = List.of(
    () -> "Sonuç 1",
    () -> "Sonuç 2",
    () -> "Sonuç 3"
);

// Hepsini çalıştır, hepsinin bitmesini bekle
List<Future<String>> sonuclar = fixed.invokeAll(gorevler);

// En hızlı birini al (diğerleri iptal edilir)
String enHizli = fixed.invokeAny(gorevler);
```

---

### 12.3 ScheduledExecutorService

Görevleri belirli zamanlarda veya belirli aralıklarla çalıştırır.

```java
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;

ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// 3 saniye sonra bir kez çalıştır
ScheduledFuture<?> tekSefer = scheduler.schedule(
    () -> System.out.println("3 saniye sonra"),
    3, TimeUnit.SECONDS
);

// 1 saniye sonra başla, sonra her 2 saniyede bir çalıştır (sabit hız)
ScheduledFuture<?> sabitHiz = scheduler.scheduleAtFixedRate(
    () -> System.out.println("Sabit hızda çalışıyor"),
    1, 2, TimeUnit.SECONDS
);

// 1 saniye sonra başla, tamamlandıktan 2 saniye sonra tekrar çalıştır
ScheduledFuture<?> sabitGecikme = scheduler.scheduleWithFixedDelay(
    () -> System.out.println("Sabit gecikmeyle çalışıyor"),
    1, 2, TimeUnit.SECONDS
);

// İptal
tekSefer.cancel(false);       // Çalışıyorsa interrupt etmeden bekle
sabitHiz.cancel(true);        // Interrupt et

// scheduleAtFixedRate ile scheduleWithFixedDelay farkı:
// scheduleAtFixedRate: Görev T0'da başlar, T0+period'da tekrar başlar
//                      (görev gecikmeli biterse üst üste binebilir)
// scheduleWithFixedDelay: Görev biter, delay sonra tekrar başlar
//                         (üst üste binme olmaz)

scheduler.shutdown();
```

---

## 13. Future ve CompletableFuture

### 13.1 Future\<V\>

Asenkron bir işlemin sonucunu temsil eder.

| Method | Açıklama |
|--------|----------|
| `V get()` | Sonucu bloklayarak bekler. |
| `V get(timeout, unit)` | Belirli süre bekler; `TimeoutException`. |
| `boolean isDone()` | Tamamlandıysa (başarılı/hatalı/iptal) `true`. |
| `boolean isCancelled()` | İptal edildiyse `true`. |
| `boolean cancel(boolean mayInterrupt)` | İptal etmeye çalışır. |

```java
import java.util.concurrent.*;

ExecutorService exec = Executors.newFixedThreadPool(2);

Future<Integer> future = exec.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

System.out.println("Hesaplanıyor...");

try {
    Integer sonuc = future.get(2, TimeUnit.SECONDS); // En fazla 2s bekle
    System.out.println("Sonuç: " + sonuc);
} catch (TimeoutException e) {
    System.out.println("Zaman aşımı!");
    future.cancel(true);
} catch (ExecutionException e) {
    System.out.println("Görev hata verdi: " + e.getCause());
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}

exec.shutdown();
```

---

### 13.2 CompletableFuture\<T\>

Java 8 ile gelen güçlü asenkron programlama aracı; zincirleme, birleştirme ve hata yönetimi.

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

// Asenkron başlatma
CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
    // Arka planda çalışır (ForkJoinPool.commonPool())
    return "Merhaba";
});

// Özel thread pool ile
ExecutorService exec = Executors.newFixedThreadPool(4);
CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "Dünya", exec);

// Zincirleme dönüşümler (aynı thread'de veya async)
CompletableFuture<Integer> uzunluk = cf
    .thenApply(s -> s + " Dünya")       // dönüşüm (sync)
    .thenApplyAsync(s -> s.length());   // dönüşüm (async)

// Sonucu tüketme (dönüş yok)
cf.thenAccept(s -> System.out.println("Sonuç: " + s));
cf.thenAcceptAsync(s -> System.out.println("Async: " + s));

// Hem girdi hem çıktı yok (Runnable)
cf.thenRun(() -> System.out.println("Tamamlandı"));
cf.thenRunAsync(() -> System.out.println("Async tamamlandı"), exec);

// İki future'ı birleştirme
CompletableFuture<String> birlesik = cf
    .thenCombine(cf2, (a, b) -> a + " " + b); // ikisi de bitince

// Flat-map (başka future dönen işlemler)
CompletableFuture<String> flatMap = cf
    .thenCompose(s -> CompletableFuture.supplyAsync(() -> s.toUpperCase()));

// İkisinden hangisi önce biterse
CompletableFuture<Object> ilkBiten = cf.applyToEither(cf2, s -> s);
cf.acceptEither(cf2, s -> System.out.println("İlk biten: " + s));
cf.runAfterEither(cf2, () -> System.out.println("Biri bitti"));

// İkisi de bitince (döndürmeden)
cf.runAfterBoth(cf2, () -> System.out.println("İkisi de bitti"));
cf.thenAcceptBoth(cf2, (a, b) -> System.out.println(a + " " + b));

// Birden fazla future
CompletableFuture<Void> hepsi = CompletableFuture.allOf(cf, cf2);     // hepsi
CompletableFuture<Object> herhangi = CompletableFuture.anyOf(cf, cf2); // biri

// Hata yönetimi
CompletableFuture<String> hataYonetimi = cf
    .exceptionally(ex -> "Hata: " + ex.getMessage()) // hata durumunda
    .handle((sonuc, hata) -> {                        // her iki durumda
        if (hata != null) return "Hata: " + hata.getMessage();
        return sonuc.toUpperCase();
    })
    .whenComplete((sonuc, hata) -> {                  // yan etki (değiştirmez)
        if (hata != null) System.err.println("Hata: " + hata);
        else System.out.println("Başarı: " + sonuc);
    });

// Manuel tamamlama
CompletableFuture<String> manuel = new CompletableFuture<>();
manuel.complete("Manuel sonuç");
manuel.completeExceptionally(new RuntimeException("Manuel hata"));
boolean tamamlandi = manuel.isDone();

// Sonucu alma (bloklayarak)
try {
    String sonuc = cf.get();         // bloklayarak bekle
    String sonuc2 = cf.join();       // get() gibi ama checked exception yok
    String sonuc3 = cf.getNow("varsayilan"); // hemen al, yoksa varsayılan
} catch (Exception e) {
    e.printStackTrace();
}

exec.shutdown();
```

---

## 14. Karşılaştırma Tablosu

### Concurrent Collections

| Yapı | Karşılığı | Thread-Safe | Null | Sıra | Big-O |
|------|-----------|------------|------|------|-------|
| `CopyOnWriteArrayList` | `ArrayList` | Evet | Evet | Ekleme | O(n) yaz, O(1) oku |
| `CopyOnWriteArraySet` | `HashSet` | Evet | Evet | Ekleme | O(n) yaz, O(n) oku |
| `ConcurrentSkipListSet` | `TreeSet` | Evet | Hayır | Sıralı | O(log n) |
| `ConcurrentHashMap` | `HashMap` | Evet | Hayır | Yok | O(1) ort. |
| `ConcurrentSkipListMap` | `TreeMap` | Evet | Hayır | Sıralı | O(log n) |

### Blocking Queues

| Yapı | Kapasite | Sıra | Null | Özellik |
|------|---------|------|------|---------|
| `ArrayBlockingQueue` | Sınırlı | FIFO | Hayır | Dizi tabanlı |
| `LinkedBlockingQueue` | Opsiyonel | FIFO | Hayır | Ayrı kilit, yüksek throughput |
| `PriorityBlockingQueue` | Sınırsız | Öncelik | Hayır | Min-heap |
| `DelayQueue` | Sınırsız | Gecikme | Hayır | `Delayed` gerektirir |
| `SynchronousQueue` | 0 | — | Hayır | Direkt el değiştirme |
| `LinkedTransferQueue` | Sınırsız | FIFO | Hayır | `transfer()` desteği |
| `LinkedBlockingDeque` | Opsiyonel | FIFO/LIFO | Hayır | İki uçlu |

---

## 15. Hangi Yapı Ne Zaman Kullanılır?

```
İhtiyacınız nedir?
│
├── Thread-safe LIST
│     ├── Okuma çok, yazma nadir  →  CopyOnWriteArrayList
│     └── Okuma ve yazma dengeli  →  Collections.synchronizedList()
│                                    veya manuel Lock + ArrayList
│
├── Thread-safe SET
│     ├── Sıra gerekmez, okuma çok  →  CopyOnWriteArraySet (küçük setler)
│     │                                ConcurrentHashMap.newKeySet() (büyük setler)
│     └── Sıralı olmalı            →  ConcurrentSkipListSet
│
├── Thread-safe MAP
│     ├── Sıra gerekmez            →  ConcurrentHashMap (yüksek performans)
│     └── Sıralı olmalı            →  ConcurrentSkipListMap
│
├── Producer-Consumer kuyruğu
│     ├── Sınırlı kapasite, dizi   →  ArrayBlockingQueue
│     ├── Sınırlı/sınırsız, yüksek throughput  →  LinkedBlockingQueue
│     ├── Öncelikli görevler       →  PriorityBlockingQueue
│     ├── Zamanlı görevler         →  DelayQueue
│     ├── Direkt el değiştirme     →  SynchronousQueue
│     ├── Transfer desteği gerekli →  LinkedTransferQueue
│     └── Her iki uçtan ekleme/alma →  LinkedBlockingDeque
│
├── Sayaç / basit atomik değer
│     ├── Düşük contention         →  AtomicInteger / AtomicLong
│     ├── Yüksek contention (sık++)→  LongAdder
│     └── Özel birleştirme fonksiyonu →  LongAccumulator
│
├── Thread eşitleme
│     ├── N işin bitmesini bekle (bir kez)  →  CountDownLatch
│     ├── Tekrar kullanılabilir bariyer     →  CyclicBarrier
│     ├── Kaynak erişim kotası              →  Semaphore
│     ├── Çok aşamalı, dinamik katılım      →  Phaser
│     └── İki thread arası veri takası      →  Exchanger
│
├── Thread pool / asenkron görev
│     ├── Sabit boyutlu havuz       →  Executors.newFixedThreadPool()
│     ├── Tek thread, sıralı        →  Executors.newSingleThreadExecutor()
│     ├── Dinamik büyüyen           →  Executors.newCachedThreadPool()
│     ├── Zamanlı görevler          →  Executors.newScheduledThreadPool()
│     └── Zincirleme async işlem    →  CompletableFuture
│
└── Kilit mekanizması
      ├── Basit kritik bölge        →  synchronized
      ├── Try-lock, timeout, fair   →  ReentrantLock
      ├── Okuma çok, yazma nadir    →  ReentrantReadWriteLock
      └── Yüksek performans okuma  →  StampedLock (optimistik okuma)
```

---

## 16. Yaygın Anti-Pattern ve Hatalar

### 1. synchronized koleksiyon + bileşik işlem

```java
// YANLIŞ — synchronized döner ama iki işlem atomik değil
List<String> syncListe = Collections.synchronizedList(new ArrayList<>());
if (!syncListe.contains("A")) {  // Bu ikisi ayrı synchronized bloklar
    syncListe.add("A");          // Aralarında başka thread müdahale edebilir
}

// DOĞRU — blok üzerinde senkronizasyon
synchronized (syncListe) {
    if (!syncListe.contains("A")) {
        syncListe.add("A");
    }
}

// DOĞRU — ConcurrentHashMap ile atomik
ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<>();
map.putIfAbsent("A", true); // atomik
```

### 2. ConcurrentHashMap'te null kontrol

```java
// YANLIŞ — ConcurrentHashMap null anahtarı kabul etmez
// map.put(null, "değer"); // NullPointerException

// YANLIŞ — get null dönerse anahtar yoktur, null değer değil
// ConcurrentHashMap'te null değer de saklanamaz
String v = map.get("anahtar");
if (v == null) {
    // Anahtar yok VEYA değer null — ama ikincisi olamaz
}

// DOĞRU
String v2 = map.getOrDefault("anahtar", "varsayilan");
```

### 3. ReentrantLock unlock unutma

```java
ReentrantLock lock = new ReentrantLock();

// YANLIŞ — exception olursa kilit asla bırakılmaz (deadlock!)
lock.lock();
falanBirIslem(); // exception fırlatabilir
lock.unlock();

// DOĞRU — her zaman finally içinde unlock
lock.lock();
try {
    falanBirIslem();
} finally {
    lock.unlock(); // her zaman çalışır
}
```

### 4. CompletableFuture exception yutma

```java
// YANLIŞ — exception sessizce yutulur
CompletableFuture.supplyAsync(() -> hatayiAt())
    .thenApply(s -> s.toUpperCase()); // Exception yutuldu!

// DOĞRU
CompletableFuture.supplyAsync(() -> hatayiAt())
    .thenApply(s -> s.toUpperCase())
    .exceptionally(ex -> {
        System.err.println("Hata: " + ex.getMessage());
        return "varsayilan";
    });
```

### 5. CopyOnWriteArrayList'i yoğun yazma ile kullanmak

```java
// YANLIŞ — her add() tüm diziyi kopyalar; çok pahalı
CopyOnWriteArrayList<Integer> liste = new CopyOnWriteArrayList<>();
for (int i = 0; i < 100_000; i++) {
    liste.add(i); // 100.000 kopya işlemi!
}

// DOĞRU — yazma yoğunsa başka yapı kullan
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
for (int i = 0; i < 100_000; i++) {
    queue.offer(i);
}
```

### 6. Thread pool'u kapatmamak (kaynak sızıntısı)

```java
// YANLIŞ — JVM kapanmaz (non-daemon thread havuzu)
ExecutorService exec = Executors.newFixedThreadPool(4);
exec.submit(() -> System.out.println("Görev"));
// exec.shutdown() — unutulursa thread'ler çalışmaya devam eder!

// DOĞRU — try-with-resources (Java 19 preview / 21 final)
try (ExecutorService execAuto = Executors.newVirtualThreadPerTaskExecutor()) {
    execAuto.submit(() -> System.out.println("Görev"));
} // otomatik shutdown

// veya klasik yöntem
ExecutorService exec2 = Executors.newFixedThreadPool(4);
try {
    exec2.submit(() -> System.out.println("Görev"));
} finally {
    exec2.shutdown();
    if (!exec2.awaitTermination(10, TimeUnit.SECONDS)) {
        exec2.shutdownNow();
    }
}
```

---

> **Kaynaklar:**
> - [java.util.concurrent Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/concurrent/package-summary.html)
> - [java.util.concurrent.atomic Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/concurrent/atomic/package-summary.html)
> - [java.util.concurrent.locks Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/concurrent/locks/package-summary.html)
> - Brian Goetz — *Java Concurrency in Practice*
> - Java Language Specification (JLS) — Java 24

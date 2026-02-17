# Java Collections Framework — Kapsamlı Dokümantasyon

## İçindekiler

1. [Collections Framework Nedir?](#1-collections-framework-nedir)
2. [Hiyerarşi ve Genel Yapı](#2-hiyerarşi-ve-genel-yapı)
3. [Collection Interface — Temel Methodlar](#3-collection-interface--temel-methodlar)
4. [List Interface](#4-list-interface)
   - [ArrayList](#41-arraylist)
   - [LinkedList](#42-linkedlist)
   - [Vector & Stack](#43-vector--stack)
5. [Set Interface](#5-set-interface)
   - [HashSet](#51-hashset)
   - [LinkedHashSet](#52-linkedhashset)
   - [TreeSet (SortedSet)](#53-treeset-sortedset)
6. [Queue Interface](#6-queue-interface)
   - [PriorityQueue](#61-priorityqueue)
   - [LinkedList (Queue olarak)](#62-linkedlist-queue-olarak)
7. [Deque Interface](#7-deque-interface)
   - [ArrayDeque](#71-arraydeque)
8. [Map Interface](#8-map-interface)
   - [HashMap](#81-hashmap)
   - [LinkedHashMap](#82-linkedhashmap)
   - [TreeMap (SortedMap)](#83-treemap-sortedmap)
   - [Hashtable](#84-hashtable)
9. [Iterator ve Iterable](#9-iterator-ve-iterable)
10. [Collections Yardımcı Sınıfı](#10-collections-yardımcı-sınıfı)
11. [Karşılaştırma Tablosu](#11-karşılaştırma-tablosu)
12. [Hangi Collection Neden Seçilir?](#12-hangi-collection-neden-seçilir)

---

## 1. Collections Framework Nedir?

Java Collections Framework (JCF), nesneleri gruplamak, saklamak ve üzerinde işlem yapmak için kullanılan hazır veri yapıları ve algoritmalar kümesidir. Java 1.2 ile tanıtılmış, `java.util` paketi altında yer almaktadır.

**Temel Faydaları:**
- Hazır, test edilmiş veri yapıları (tekrar implementasyon gerekmez)
- Ortak bir API sayesinde farklı yapılar arasında kolayca geçiş
- Generics desteğiyle tip güvenliği
- Algoritmalar (sıralama, arama) hazır gelir

---

## 2. Hiyerarşi ve Genel Yapı

```
Iterable<E>
    └── Collection<E>
            ├── List<E>
            │     ├── ArrayList<E>
            │     ├── LinkedList<E>
            │     └── Vector<E>
            │           └── Stack<E>
            ├── Set<E>
            │     ├── HashSet<E>
            │     │     └── LinkedHashSet<E>
            │     └── SortedSet<E>
            │           └── NavigableSet<E>
            │                 └── TreeSet<E>
            └── Queue<E>
                  ├── PriorityQueue<E>
                  └── Deque<E>
                        ├── ArrayDeque<E>
                        └── LinkedList<E>

Map<K,V>  (Collection'dan türemez, ayrı bir hiyerarşidir)
    ├── HashMap<K,V>
    │     └── LinkedHashMap<K,V>
    ├── SortedMap<K,V>
    │     └── NavigableMap<K,V>
    │           └── TreeMap<K,V>
    └── Hashtable<K,V>
```

> **Not:** `Map` interface'i `Collection`'dan türemez; ayrı bir hiyerarşidedir ancak Collections Framework'ün ayrılmaz bir parçasıdır.

---

## 3. Collection Interface — Temel Methodlar

`Collection<E>` tüm koleksiyon sınıflarının ana interface'idir. Aşağıdaki methodlar tüm `Collection` implementasyonlarında bulunur.

### Ekleme

| Method | Açıklama |
|--------|----------|
| `boolean add(E e)` | Koleksiyona eleman ekler. Başarılıysa `true` döner. |
| `boolean addAll(Collection<? extends E> c)` | Verilen koleksiyondaki tüm elemanları ekler. |

```java
List<String> liste = new ArrayList<>();
liste.add("Elma");
liste.addAll(List.of("Armut", "Kiraz"));
```

### Silme

| Method | Açıklama |
|--------|----------|
| `boolean remove(Object o)` | Belirtilen elemanın ilk eşleşmesini siler. |
| `boolean removeAll(Collection<?> c)` | Verilen koleksiyonda bulunan tüm elemanları siler. |
| `boolean retainAll(Collection<?> c)` | Yalnızca verilen koleksiyonda da bulunan elemanları tutar (diğerlerini siler). |
| `void clear()` | Tüm elemanları siler. |

```java
liste.remove("Elma");
liste.removeAll(List.of("Armut"));
liste.clear();
```

### Sorgulama

| Method | Açıklama |
|--------|----------|
| `boolean contains(Object o)` | Eleman koleksiyonda varsa `true` döner. |
| `boolean containsAll(Collection<?> c)` | Tüm elemanlar varsa `true` döner. |
| `boolean isEmpty()` | Koleksiyon boşsa `true` döner. |
| `int size()` | Eleman sayısını döner. |

```java
boolean var = liste.contains("Elma");
int boyut = liste.size();
boolean bos = liste.isEmpty();
```

### Dönüştürme

| Method | Açıklama |
|--------|----------|
| `Object[] toArray()` | `Object` dizisine dönüştürür. |
| `<T> T[] toArray(T[] a)` | Belirli bir tipte diziye dönüştürür. |
| `Stream<E> stream()` | Stream oluşturur (Java 8+). |
| `Stream<E> parallelStream()` | Paralel stream oluşturur (Java 8+). |

```java
String[] dizi = liste.toArray(new String[0]);
liste.stream().filter(s -> s.startsWith("E")).forEach(System.out::println);
```

### Döngü

| Method | Açıklama |
|--------|----------|
| `Iterator<E> iterator()` | Iterator döner. |
| `void forEach(Consumer<? super E> action)` | Her eleman için lambda çalıştırır (Java 8+). |

```java
liste.forEach(System.out::println);
```

---

## 4. List Interface

`List<E>`, `Collection<E>`'yi genişletir. **Sıralı** ve **index tabanlı** bir yapıdır; aynı eleman birden fazla kez bulunabilir (duplicate).

### List'e Özel Methodlar

| Method | Açıklama |
|--------|----------|
| `E get(int index)` | Belirtilen indexteki elemanı döner. |
| `E set(int index, E element)` | Belirtilen indexe eleman koyar, eskisini döner. |
| `void add(int index, E element)` | Belirtilen indexe eleman ekler. |
| `E remove(int index)` | Belirtilen indexteki elemanı siler ve döner. |
| `int indexOf(Object o)` | Elemanın ilk bulunduğu indeksi döner, yoksa -1. |
| `int lastIndexOf(Object o)` | Elemanın son bulunduğu indeksi döner, yoksa -1. |
| `List<E> subList(int from, int to)` | Belirtilen aralığı döner (from dahil, to hariç). |
| `ListIterator<E> listIterator()` | İki yönlü iterator döner. |
| `void sort(Comparator<? super E> c)` | Listeyi sıralar (Java 8+). |
| `static List<E> of(E... e)` | Değiştirilemez liste oluşturur (Java 9+). |
| `static List<E> copyOf(Collection<? extends E> c)` | Değiştirilemez kopya oluşturur (Java 10+). |

---

### 4.1 ArrayList

**Özellikler:**
- Dinamik boyutlu dizi tabanlıdır (iç tarafta `Object[]` kullanır).
- Rastgele erişim `O(1)` — `get(index)` çok hızlıdır.
- Ortadan ekleme/silme `O(n)` — yavaş olabilir.
- `null` elemana izin verir.
- **Thread-safe değildir.**
- Başlangıç kapasitesi 10'dur; dolduğunda %50 büyür.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

List<String> meyveler = new ArrayList<>();

// Ekleme
meyveler.add("Elma");
meyveler.add("Armut");
meyveler.add(0, "Kiraz");       // index'e ekleme

// Erişim
String ilk = meyveler.get(0);   // "Kiraz"

// Güncelleme
meyveler.set(1, "Mango");

// Silme
meyveler.remove(0);             // index ile
meyveler.remove("Armut");       // değer ile

// Arama
int idx = meyveler.indexOf("Mango");

// Sıralama
Collections.sort(meyveler);
meyveler.sort(Comparator.reverseOrder());

// Alt liste
List<String> sub = meyveler.subList(0, 2);

// Boyut & boşluk kontrolü
int boyut = meyveler.size();
boolean bos = meyveler.isEmpty();

// Döngü
for (String m : meyveler) {
    System.out.println(m);
}
meyveler.forEach(System.out::println);
```

---

### 4.2 LinkedList

**Özellikler:**
- Çift yönlü bağlı liste (doubly-linked list) yapısındadır.
- Ortadan ekleme/silme `O(1)` (düğüm referansı biliniyorsa).
- Rastgele erişim `O(n)` — `get(index)` yavaştır.
- Hem `List` hem de `Deque` interface'ini implemente eder.
- `null` elemana izin verir.
- **Thread-safe değildir.**
- Bellek kullanımı `ArrayList`'ten fazladır (her eleman için önceki/sonraki referanslar).

```java
import java.util.LinkedList;

LinkedList<String> linkedList = new LinkedList<>();

// List methodları (ArrayList ile aynı)
linkedList.add("A");
linkedList.add("B");
linkedList.get(0);

// Deque/Queue methodları (LinkedList'e özel avantajlar)
linkedList.addFirst("Başa");      // Başa ekle
linkedList.addLast("Sona");       // Sona ekle
linkedList.removeFirst();         // Baştan sil
linkedList.removeLast();          // Sondan sil
String ilk = linkedList.getFirst();
String son = linkedList.getLast();
linkedList.peekFirst();           // Silmeden başa bak
linkedList.peekLast();            // Silmeden sona bak
linkedList.offerFirst("X");       // Queue semantiğiyle başa ekle
linkedList.offerLast("Y");        // Queue semantiğiyle sona ekle
linkedList.pollFirst();           // Baştan al ve sil (boşsa null)
linkedList.pollLast();            // Sondan al ve sil (boşsa null)
```

---

### 4.3 Vector & Stack

**Vector:** Thread-safe `ArrayList` alternatifi. Synchronized methodları olduğu için yavaştır. Modern kodda tercih edilmez; yerine `Collections.synchronizedList()` veya `CopyOnWriteArrayList` kullanılır.

**Stack:** `Vector`'dan türer, LIFO (Last In First Out) mantığıyla çalışır. Modern kodda `Deque` / `ArrayDeque` tercih edilir.

```java
import java.util.Stack;

Stack<Integer> stack = new Stack<>();
stack.push(1);       // Üste ekle
stack.push(2);
stack.push(3);
int ust = stack.peek();  // Üste bak (silmez) → 3
int cikar = stack.pop(); // Üstten çıkar → 3
boolean bos = stack.empty();
int konum = stack.search(1); // 1-tabanlı index, tepeden uzaklık
```

---

## 5. Set Interface

`Set<E>`, `Collection<E>`'yi genişletir. **Tekrarsız (unique)** elemanlar tutar; aynı eleman iki kez eklenemez (`equals()` / `hashCode()` kullanılır).

`List`'teki index tabanlı methodlar (`get`, `set`, `add(index,e)`) `Set`'te **yoktur**.

---

### 5.1 HashSet

**Özellikler:**
- Hash tablosu tabanlıdır (`HashMap` üzerine kurulu).
- Ekleme, silme, arama: ortalama `O(1)`.
- **Sırasız** — elemanların sırası garanti edilmez.
- Bir tane `null` elemana izin verir.
- **Thread-safe değildir.**

```java
import java.util.HashSet;
import java.util.Set;

Set<String> set = new HashSet<>();

// Ekleme
set.add("Elma");
set.add("Armut");
set.add("Elma");    // duplicate, eklenmez

// Kontrol
boolean var = set.contains("Elma");  // true
int boyut = set.size();               // 2

// Silme
set.remove("Armut");

// Küme işlemleri
Set<String> diger = new HashSet<>(Set.of("Kiraz", "Elma"));

// Birleşim (Union)
Set<String> birlesim = new HashSet<>(set);
birlesim.addAll(diger);

// Kesişim (Intersection)
Set<String> kesisim = new HashSet<>(set);
kesisim.retainAll(diger);

// Fark (Difference)
Set<String> fark = new HashSet<>(set);
fark.removeAll(diger);

// Alt küme kontrolü
boolean altKume = set.containsAll(diger);

// Döngü
for (String s : set) {
    System.out.println(s);
}
```

---

### 5.2 LinkedHashSet

**Özellikler:**
- `HashSet`'in alt sınıfıdır.
- **Ekleme sırasını** korur (doubly-linked list + hash table).
- Ekleme, silme, arama: ortalama `O(1)` (HashSet'ten biraz yavaş).
- Bir tane `null` elemana izin verir.
- **Thread-safe değildir.**

```java
import java.util.LinkedHashSet;

LinkedHashSet<String> linked = new LinkedHashSet<>();
linked.add("Üçüncü");
linked.add("Birinci");
linked.add("İkinci");

// Sıra korunur: Üçüncü, Birinci, İkinci
linked.forEach(System.out::println);
```

---

### 5.3 TreeSet (SortedSet)

**Özellikler:**
- Kırmızı-siyah ağaç (Red-Black Tree) tabanlıdır.
- Elemanları **doğal sırayla** (Comparable) veya verilen `Comparator`'a göre sıralar.
- Ekleme, silme, arama: `O(log n)`.
- `null` elemana izin **vermez** (NullPointerException fırlatır).
- **Thread-safe değildir.**
- `NavigableSet` interface'ini implemente eder.

```java
import java.util.TreeSet;
import java.util.NavigableSet;

NavigableSet<Integer> treeSet = new TreeSet<>();
treeSet.add(5);
treeSet.add(3);
treeSet.add(8);
treeSet.add(1);
// Sıralı: 1, 3, 5, 8

// SortedSet / NavigableSet methodları
int ilk = treeSet.first();               // En küçük → 1
int son = treeSet.last();                // En büyük → 8
Integer kucukEsit = treeSet.floor(4);   // 4'e <=  en büyük → 3
Integer buyukEsit = treeSet.ceiling(4); // 4'e >=  en küçük → 5
Integer kesinKucuk = treeSet.lower(5);  // 5'ten < en büyük → 3
Integer kesinBuyuk = treeSet.higher(5); // 5'ten > en küçük → 8

// Alt/üst küme görünümleri (view — orijinali değiştirir)
NavigableSet<Integer> bas = treeSet.headSet(5, false); // 5'ten küçükler
NavigableSet<Integer> son2 = treeSet.tailSet(5, true);  // 5 dahil büyükler
NavigableSet<Integer> aralik = treeSet.subSet(3, true, 8, false); // [3, 8)

// Ters sıralı görünüm
NavigableSet<Integer> ters = treeSet.descendingSet();

// Polling (al ve sil)
int enKucuk = treeSet.pollFirst();  // 1 (kaldırıldı)
int enBuyuk = treeSet.pollLast();   // 8 (kaldırıldı)

// Özel sıralama (Comparator ile)
TreeSet<String> ozelSirali = new TreeSet<>(Comparator.comparingInt(String::length));
ozelSirali.add("Elma");
ozelSirali.add("Armut");
ozelSirali.add("Kiraz");
// Uzunluk sırasına göre: Elma, Kiraz, Armut
```

---

## 6. Queue Interface

`Queue<E>`, FIFO (First In First Out — İlk giren ilk çıkar) mantığıyla çalışır. İki tip method seti sunar: exception fırlatanlar ve özel değer dönenler.

| İşlem | Exception Fırlatır | Özel Değer Döner |
|-------|--------------------|-----------------|
| Ekleme | `add(e)` | `offer(e)` → false |
| Başa bakma | `element()` | `peek()` → null |
| Alma & Silme | `remove()` | `poll()` → null |

```java
import java.util.Queue;
import java.util.LinkedList;

Queue<String> kuyruk = new LinkedList<>();

// Ekleme
kuyruk.offer("Birinci");
kuyruk.offer("İkinci");
kuyruk.offer("Üçüncü");

// Başa bak (silme yok)
String bas = kuyruk.peek();     // "Birinci" (null döner boşsa)
String basEx = kuyruk.element(); // "Birinci" (exception fırlatır boşsa)

// Al ve sil
String cikan = kuyruk.poll();   // "Birinci" (null döner boşsa)
String cikanEx = kuyruk.remove(); // "İkinci" (exception fırlatır boşsa)

int boyut = kuyruk.size();
boolean bos = kuyruk.isEmpty();
```

---

### 6.1 PriorityQueue

**Özellikler:**
- Min-heap tabanlıdır; her zaman **en küçük eleman** önce çıkar (doğal sıra).
- `Comparator` ile farklı öncelik tanımlanabilir (max-heap gibi).
- `null` elemana izin **vermez**.
- **Thread-safe değildir.**
- Ekleme/silme: `O(log n)`, en küçüğe erişim: `O(1)`.

```java
import java.util.PriorityQueue;

// Min-heap (varsayılan)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5);
minHeap.offer(1);
minHeap.offer(3);

System.out.println(minHeap.peek());  // 1 (en küçük)
System.out.println(minHeap.poll());  // 1 (çıkar)
System.out.println(minHeap.poll());  // 3

// Max-heap (Comparator ile)
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
maxHeap.offer(5);
maxHeap.offer(1);
maxHeap.offer(3);
System.out.println(maxHeap.poll());  // 5 (en büyük)

// Özel öncelik
PriorityQueue<String> uzunlukSirali = new PriorityQueue<>(
    Comparator.comparingInt(String::length)
);
uzunlukSirali.offer("Muz");
uzunlukSirali.offer("Armut");
uzunlukSirali.offer("Elma");
System.out.println(uzunlukSirali.poll()); // "Muz" (en kısa)
```

---

### 6.2 LinkedList (Queue olarak)

`LinkedList`, `Queue` interface'ini implemente ettiğinden `Queue` referansıyla kullanılabilir. Bkz. [4.2 LinkedList](#42-linkedlist).

---

## 7. Deque Interface

`Deque<E>` (Double-Ended Queue), her iki uçtan da ekleme/silme yapılabilen yapıdır. Hem **stack** hem de **queue** olarak kullanılabilir.

| İşlem | Baş (First) | Son (Last) |
|-------|-------------|------------|
| Ekleme (exception) | `addFirst(e)` | `addLast(e)` |
| Ekleme (null/false) | `offerFirst(e)` | `offerLast(e)` |
| Silme (exception) | `removeFirst()` | `removeLast()` |
| Silme (null) | `pollFirst()` | `pollLast()` |
| Bakma (exception) | `getFirst()` | `getLast()` |
| Bakma (null) | `peekFirst()` | `peekLast()` |

**Stack olarak kullanım:**

| Stack Method | Deque Karşılığı |
|---|---|
| `push(e)` | `addFirst(e)` |
| `pop()` | `removeFirst()` |
| `peek()` | `peekFirst()` |

---

### 7.1 ArrayDeque

**Özellikler:**
- Dairesel dizi (circular array) tabanlıdır.
- Stack ve Queue olarak `Stack` ve `LinkedList`'ten **daha hızlıdır**.
- `null` elemana izin **vermez**.
- **Thread-safe değildir.**
- Kapasite otomatik büyür.

```java
import java.util.ArrayDeque;
import java.util.Deque;

Deque<String> deque = new ArrayDeque<>();

// Her iki uçtan ekleme
deque.addFirst("Orta");
deque.addFirst("Baş");
deque.addLast("Son");
// Sonuç: [Baş, Orta, Son]

// Stack gibi kullan (LIFO)
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);
stack.push(2);
stack.push(3);
int ust = stack.peek();   // 3
int cikan = stack.pop();  // 3

// Queue gibi kullan (FIFO)
Deque<Integer> queue = new ArrayDeque<>();
queue.offer(1);
queue.offer(2);
queue.offer(3);
int bas = queue.peek();   // 1
int cikan2 = queue.poll(); // 1

// Her iki uçtan silme
deque.pollFirst();
deque.pollLast();

// Ters iterator
deque.descendingIterator().forEachRemaining(System.out::println);
```

---

## 8. Map Interface

`Map<K, V>`, anahtar-değer (key-value) çiftlerini saklar. **Her anahtar benzersizdir** (unique key); bir anahtara yalnızca bir değer atanabilir. `Collection`'dan türemez ama Collections Framework'ün parçasıdır.

### Map'e Genel Methodlar

| Method | Açıklama |
|--------|----------|
| `V put(K key, V value)` | Anahtar-değer ekler/günceller; önceki değeri döner. |
| `V get(Object key)` | Anahtara karşılık değeri döner, yoksa `null`. |
| `V getOrDefault(Object key, V defaultValue)` | Yoksa `defaultValue` döner. |
| `V remove(Object key)` | Anahtarı ve değerini siler; değeri döner. |
| `boolean remove(Object key, Object value)` | Yalnızca anahtar-değer eşleşirse siler. |
| `boolean containsKey(Object key)` | Anahtar varsa `true`. |
| `boolean containsValue(Object value)` | Değer varsa `true`. |
| `int size()` | Giriş sayısı. |
| `boolean isEmpty()` | Boşsa `true`. |
| `void clear()` | Tüm girdileri siler. |
| `Set<K> keySet()` | Tüm anahtarların `Set` görünümü. |
| `Collection<V> values()` | Tüm değerlerin `Collection` görünümü. |
| `Set<Map.Entry<K,V>> entrySet()` | Tüm girdilerin `Set` görünümü. |
| `void putAll(Map<? extends K, ? extends V> m)` | Tüm girdileri ekler. |
| `V putIfAbsent(K key, V value)` | Anahtar yoksa ekler; mevcut değeri döner. |
| `V computeIfAbsent(K key, Function<K,V> f)` | Anahtar yoksa fonksiyonla üretir. |
| `V computeIfPresent(K key, BiFunction<K,V,V> f)` | Anahtar varsa fonksiyonla günceller. |
| `V compute(K key, BiFunction<K,V,V> f)` | Her durumda fonksiyonla hesaplar. |
| `V merge(K key, V value, BiFunction<V,V,V> f)` | Var/yok duruma göre birleştirir. |
| `void forEach(BiConsumer<K,V> action)` | Her giriş için işlem yapar. |
| `boolean replace(K key, V oldValue, V newValue)` | Koşullu günceller. |
| `V replace(K key, V value)` | Varsa günceller; öncekini döner. |
| `static Map<K,V> of(...)` | Değiştirilemez map (Java 9+). |
| `static Map<K,V> copyOf(Map<K,V> m)` | Değiştirilemez kopya (Java 10+). |

---

### 8.1 HashMap

**Özellikler:**
- Hash tablosu tabanlıdır.
- Ekleme, silme, arama: ortalama `O(1)`.
- **Sırasız** — anahtar sırası garanti edilmez.
- Bir tane `null` anahtar ve birden fazla `null` değere izin verir.
- **Thread-safe değildir.**
- Varsayılan kapasite 16, load factor 0.75.

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> yaslar = new HashMap<>();

// Ekleme
yaslar.put("Ali", 25);
yaslar.put("Ayşe", 30);
yaslar.put("Mehmet", 22);

// Okuma
int yasAli = yaslar.get("Ali");               // 25
int yok = yaslar.getOrDefault("Zeynep", -1); // -1

// Güncelleme
yaslar.put("Ali", 26);                        // üzerine yazar
yaslar.replace("Ayşe", 30, 31);              // koşullu güncelle
yaslar.putIfAbsent("Veli", 28);              // yoksa ekle

// Silme
yaslar.remove("Mehmet");
yaslar.remove("Ali", 99); // değer eşleşmediğinden silinmez

// Kontrol
boolean var = yaslar.containsKey("Ayşe");
boolean degerVar = yaslar.containsValue(26);

// Döngü — entrySet en verimli yol
for (Map.Entry<String, Integer> giris : yaslar.entrySet()) {
    System.out.println(giris.getKey() + " → " + giris.getValue());
}
yaslar.forEach((k, v) -> System.out.println(k + " → " + v));

// Yalnızca anahtarlar veya değerler
for (String isim : yaslar.keySet()) { /* ... */ }
for (int yas : yaslar.values()) { /* ... */ }

// Gelişmiş methodlar (Java 8+)
yaslar.computeIfAbsent("Yeni", k -> k.length()); // key uzunluğunu değer yap
yaslar.computeIfPresent("Ayşe", (k, v) -> v + 1); // varsa 1 artır
yaslar.merge("Ali", 1, Integer::sum); // Ali varsa topla, yoksa 1 koy
```

---

### 8.2 LinkedHashMap

**Özellikler:**
- `HashMap`'in alt sınıfıdır.
- **Ekleme sırasını** veya **erişim sırasını** (access-order) korur.
- Ekleme, silme, arama: ortalama `O(1)` (HashMap'ten biraz yavaş).
- Bir tane `null` anahtar, çoklu `null` değer.
- **Thread-safe değildir.**
- LRU Cache implementasyonu için uygundur.

```java
import java.util.LinkedHashMap;
import java.util.Map;

// Ekleme sırası (varsayılan)
Map<String, Integer> linkedMap = new LinkedHashMap<>();
linkedMap.put("C", 3);
linkedMap.put("A", 1);
linkedMap.put("B", 2);
// C, A, B sırası korunur
linkedMap.forEach((k, v) -> System.out.println(k + "=" + v));

// Erişim sırası (access-order=true) — LRU Cache için
LinkedHashMap<String, Integer> lruCache = new LinkedHashMap<>(16, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
        return size() > 3; // Maksimum 3 eleman
    }
};
lruCache.put("a", 1);
lruCache.put("b", 2);
lruCache.put("c", 3);
lruCache.get("a");     // "a" en son erişildi, sona geçer
lruCache.put("d", 4); // "b" en eski → silinir
```

---

### 8.3 TreeMap (SortedMap)

**Özellikler:**
- Kırmızı-siyah ağaç (Red-Black Tree) tabanlıdır.
- Anahtarları **doğal sırayla** veya `Comparator`'a göre sıralar.
- Ekleme, silme, arama: `O(log n)`.
- `null` anahtara izin **vermez**.
- **Thread-safe değildir.**
- `NavigableMap` interface'ini implemente eder.

```java
import java.util.TreeMap;
import java.util.NavigableMap;

NavigableMap<String, Integer> treeMap = new TreeMap<>();
treeMap.put("Elma", 3);
treeMap.put("Armut", 5);
treeMap.put("Kiraz", 2);
// Alfabetik sıra: Armut, Elma, Kiraz

// SortedMap methodları
String ilkAnahtar = treeMap.firstKey();  // "Armut"
String sonAnahtar = treeMap.lastKey();   // "Kiraz"

// NavigableMap methodları
String kucukEsit = treeMap.floorKey("Elma");    // "Elma"
String buyukEsit = treeMap.ceilingKey("D");     // "Elma"
String kesinKucuk = treeMap.lowerKey("Elma");   // "Armut"
String kesinBuyuk = treeMap.higherKey("Elma");  // "Kiraz"

// Entry dönen versiyonlar
Map.Entry<String, Integer> ilkGiris = treeMap.firstEntry();
Map.Entry<String, Integer> sonGiris = treeMap.lastEntry();
Map.Entry<String, Integer> pollIlk = treeMap.pollFirstEntry(); // al+sil
Map.Entry<String, Integer> pollSon = treeMap.pollLastEntry();  // al+sil

// Alt map görünümleri
NavigableMap<String, Integer> bas = treeMap.headMap("Elma", true);  // <=Elma
NavigableMap<String, Integer> son = treeMap.tailMap("Elma", false); // >Elma
NavigableMap<String, Integer> aralik = treeMap.subMap("A", true, "K", false);

// Ters sıralı görünüm
NavigableMap<String, Integer> ters = treeMap.descendingMap();

// Ters anahtar seti
treeMap.descendingKeySet().forEach(System.out::println);
```

---

### 8.4 Hashtable

**Özellikler:**
- **Thread-safe** (synchronized) — eski API.
- `null` anahtar veya değere izin **vermez**.
- Modern kodda yerine `ConcurrentHashMap` tercih edilir.

```java
import java.util.Hashtable;

Hashtable<String, Integer> table = new Hashtable<>();
table.put("A", 1);
// Tüm işlemler synchronized — çoklu thread'de güvenli ama yavaş
```

---

## 9. Iterator ve Iterable

### Iterator\<E\>

Koleksiyon elemanları üzerinde tek yönlü gezinti sağlar.

| Method | Açıklama |
|--------|----------|
| `boolean hasNext()` | Sonraki eleman varsa `true`. |
| `E next()` | Sonraki elemanı döner ve ilerler. |
| `void remove()` | Son döndürülen elemanı siler (opsiyonel). |
| `void forEachRemaining(Consumer<E> action)` | Kalan elemanlar için işlem yapar (Java 8+). |

```java
import java.util.Iterator;

List<String> liste = new ArrayList<>(List.of("A", "B", "C", "D"));
Iterator<String> it = liste.iterator();

while (it.hasNext()) {
    String s = it.next();
    if (s.equals("B")) {
        it.remove(); // Güvenli silme (ConcurrentModificationException'ı önler)
    }
}
```

### ListIterator\<E\>

`List` için iki yönlü gezinti.

| Method | Açıklama |
|--------|----------|
| `boolean hasPrevious()` | Önceki eleman varsa `true`. |
| `E previous()` | Önceki elemanı döner ve geri gider. |
| `int nextIndex()` | Sonraki elemanın indeksi. |
| `int previousIndex()` | Önceki elemanın indeksi. |
| `void set(E e)` | Son döndürülen elemanı günceller. |
| `void add(E e)` | Mevcut konuma eleman ekler. |

```java
ListIterator<String> lit = liste.listIterator(liste.size()); // sondan başla
while (lit.hasPrevious()) {
    System.out.println(lit.previous());
}
```

---

## 10. Collections Yardımcı Sınıfı

`java.util.Collections` (statik methodlar), koleksiyonlar üzerinde çeşitli işlemler sunar.

| Method | Açıklama |
|--------|----------|
| `sort(List<T>)` | Doğal sırayla sıralar. |
| `sort(List<T>, Comparator<T>)` | Comparator ile sıralar. |
| `reverse(List<?>)` | Listeyi tersine çevirir. |
| `shuffle(List<?>)` | Karıştırır. |
| `swap(List<?>, int i, int j)` | İki indeksi takas eder. |
| `fill(List<? super T>, T obj)` | Tüm elemanları belirtilen değerle doldurur. |
| `copy(List<? super T> dest, List<? extends T> src)` | Kopyalar. |
| `binarySearch(List, key)` | İkili arama (sıralı listede). |
| `min(Collection<?>)` | En küçük elemanı döner. |
| `max(Collection<?>)` | En büyük elemanı döner. |
| `frequency(Collection<?>, Object)` | Elemanın kaç kez geçtiğini döner. |
| `disjoint(Collection<?>, Collection<?>)` | Ortak eleman yoksa `true`. |
| `nCopies(int n, T o)` | n adet kopyadan oluşan değiştirilemez liste. |
| `unmodifiableList(List<T>)` | Değiştirilemez sarmalayıcı döner. |
| `synchronizedList(List<T>)` | Thread-safe sarmalayıcı döner. |
| `singletonList(T o)` | Tek elemanlı değiştirilemez liste. |
| `emptyList()` | Boş değiştirilemez liste. |
| `reverseOrder()` | Ters sıralama Comparator'ı döner. |

```java
import java.util.Collections;

List<Integer> sayilar = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9, 2, 6));

Collections.sort(sayilar);                          // [1, 1, 2, 3, 4, 5, 6, 9]
Collections.reverse(sayilar);                       // [9, 6, 5, 4, 3, 2, 1, 1]
Collections.shuffle(sayilar);                       // rastgele sıra
int max = Collections.max(sayilar);                 // 9
int min = Collections.min(sayilar);                 // 1
int freq = Collections.frequency(sayilar, 1);       // 2
Collections.swap(sayilar, 0, sayilar.size() - 1);  // ilk ve son takas

Collections.sort(sayilar);
int idx = Collections.binarySearch(sayilar, 5);    // ikili arama indeksi

List<String> dondurulamaz = Collections.unmodifiableList(new ArrayList<>(List.of("A")));
List<Integer> syncListe = Collections.synchronizedList(new ArrayList<>());
```

---

## 11. Karşılaştırma Tablosu

| Yapı | Sıra | Duplicate | Null | Thread-Safe | Erişim | Ekleme/Silme |
|------|------|-----------|------|-------------|--------|--------------|
| `ArrayList` | Ekleme sırası | Evet | Evet | Hayır | O(1) | O(n) orta |
| `LinkedList` | Ekleme sırası | Evet | Evet | Hayır | O(n) | O(1) iki uç |
| `HashSet` | Yok | Hayır | 1 null | Hayır | O(1) ort. | O(1) ort. |
| `LinkedHashSet` | Ekleme sırası | Hayır | 1 null | Hayır | O(1) ort. | O(1) ort. |
| `TreeSet` | Sıralı | Hayır | Hayır | Hayır | O(log n) | O(log n) |
| `PriorityQueue` | Öncelik | Evet | Hayır | Hayır | O(1) peek | O(log n) |
| `ArrayDeque` | Ekleme sırası | Evet | Hayır | Hayır | O(1) uçlar | O(1) uçlar |
| `HashMap` | Yok | (key hayır) | 1 null key | Hayır | O(1) ort. | O(1) ort. |
| `LinkedHashMap` | Ekleme/erişim | (key hayır) | 1 null key | Hayır | O(1) ort. | O(1) ort. |
| `TreeMap` | Sıralı (key) | (key hayır) | Hayır | Hayır | O(log n) | O(log n) |
| `Hashtable` | Yok | (key hayır) | Hayır | Evet | O(1) ort. | O(1) ort. |

---

## 12. Hangi Collection Neden Seçilir?

```
İhtiyacınız nedir?
│
├── Sıralı liste, index ile erişim
│     ├── Sık okuma/erişim  →  ArrayList
│     └── Sık ekleme/silme (özellikle baş-son)  →  LinkedList / ArrayDeque
│
├── Tekrarsız eleman (Set)
│     ├── Sıra önemli değil, hız öncelik  →  HashSet
│     ├── Ekleme sırası korunacak  →  LinkedHashSet
│     └── Sıralı olacak  →  TreeSet
│
├── Öncelikli kuyruk (en küçük/büyük önce çıkar)
│     └── PriorityQueue
│
├── Stack (LIFO) veya Queue (FIFO)
│     └── ArrayDeque  (Stack ve LinkedList'ten daha hızlı)
│
├── Anahtar-Değer (Map)
│     ├── Sıra önemli değil  →  HashMap
│     ├── Ekleme sırası korunacak  →  LinkedHashMap
│     └── Anahtara göre sıralı  →  TreeMap
│
└── Thread-Safe ihtiyacı
      ├── List  →  CopyOnWriteArrayList (java.util.concurrent)
      ├── Set   →  ConcurrentSkipListSet / Collections.synchronizedSet()
      └── Map   →  ConcurrentHashMap (java.util.concurrent)
```

---

> **Kaynaklar:**
> - [Java SE Documentation — java.util](https://docs.oracle.com/en/java/docs/api/java.base/java/util/package-summary.html)
> - [Oracle Collections Tutorial](https://docs.oracle.com/javase/tutorial/collections/)
> - Java Language Specification (JLS) — Java 24

package org.example;

public class Main {
    public static void main(String[] args) {

        IHisseSenediPiyasasi piyasa = new HisseSenediPiyasasi();

        piyasa.addHisse("THYAO", new Hisse("THYAO", "Türk Hava Yolları",  "Havacılık",    245.80,  3.2,  12_500_000));
        piyasa.addHisse("GARAN", new Hisse("GARAN", "Garanti Bankası",    "Bankacılık",    89.40, -1.1,   8_200_000));
        piyasa.addHisse("ASELS", new Hisse("ASELS", "Aselsan",            "Savunma",       187.60,  5.7,  3_400_000));
        piyasa.addHisse("EREGL", new Hisse("EREGL", "Ereğli Demir Çelik","Metal",          134.20, -0.8,  6_700_000));
        piyasa.addHisse("BIMAS", new Hisse("BIMAS", "BİM Mağazaları",     "Perakende",     456.00,  0.3,  2_100_000));
        piyasa.addHisse("SISE",  new Hisse("SISE",  "Şişecam",            "Cam",            62.50,  1.9,  9_800_000));
        piyasa.addHisse("KCHOL", new Hisse("KCHOL", "Koç Holding",        "Holding",        198.30, -2.4,  4_300_000));
        piyasa.addHisse("FROTO", new Hisse("FROTO", "Ford Otosan",        "Otomotiv",       923.00,  4.1,  1_800_000));
        piyasa.addHisse("AKBNK", new Hisse("AKBNK", "Akbank",             "Bankacılık",     71.20, -0.6, 11_200_000));
        piyasa.addHisse("TUPRS", new Hisse("TUPRS", "Tüpraş",             "Enerji",         387.50,  2.8,  3_900_000));
        piyasa.addHisse("PGSUS", new Hisse("PGSUS", "Pegasus",            "Havacılık",      678.00, -3.5,  2_600_000));
        piyasa.addHisse("EKGYO", new Hisse("EKGYO", "Emlak Konut",        "Gayrimenkul",     28.90,  0.9, 15_400_000));
        piyasa.addHisse("OYAKC", new Hisse("OYAKC", "Oyak Çimento",       "İnşaat",          44.60,  1.4,  7_800_000));
        piyasa.addHisse("SAHOL", new Hisse("SAHOL", "Sabancı Holding",    "Holding",         109.70, -1.7,  5_500_000));
        piyasa.addHisse("VESTL", new Hisse("VESTL", "Vestel",             "Teknoloji",        53.30,  6.2,  4_200_000));

    }
}

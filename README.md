# Veikala Inventāra Pārvaldības Sistēma

Vienkārša un efektīva sistēma veikala produktu un kategoriju pārvaldībai.

## 1. Prasības
- Java Runtime Environment (JRE) 21 vai jaunāka
- Maven 3.8+ (ja kompilējat no avota)

## 2. Uzstādīšana
```bash
# Klonēt projektu
git clone https://github.com/aesdetgewr/Veikala-Inventara-Sistema.git
cd Veikala-Inventara-Sistema

# Kompilēt projektu
mvn clean install
```

## 3. Palaišana
```bash
# Palaist ar latviešu valodu (noklusētā)
./mvnw clean compile exec:java

# Palaist ar angļu valodu
./mvnw clean compile exec:java -Dexec.args="en"
```

## 4. Funkcionalitāte

### Produktu Pārvaldība
- Produktu pievienošana
- Produktu rediģēšana
- Produktu dzēšana
- Meklēšana un filtrēšana

### Kategoriju Pārvaldība
- Kategoriju izveide
- Produktu piesaiste kategorijām
- Kategoriju pārskati

### Analīze
- Kopējās vērtības aprēķins
- Statistika pa kategorijām
- Cenu un daudzumu pārskati

## 5. Lietošanas Nosacījumi

### Produktu un Kategoriju Nosaukumi
Atļautie simboli:
  - Lielie un mazie burti (A-Z, a-z)
  - Pasvītrojuma simbols (_)

Neatļautie simboli:
  - Cipari (0-9)
  - Atstarpes
  - Speciālie simboli

### Skaitliskās Vērtības
Cenas ierobežojumi:
  - Minimālā vērtība: 0.01
  - Maksimālā vērtība: 1,000,000.00
  - Formāts: cipari ar divām decimālzīmēm

Daudzuma ierobežojumi:
  - Minimālā vērtība: 0
  - Maksimālā vērtība: 1,000,000
  - Formāts: veseli pozitīvi skaitļi

### Failu Struktūra
Pamatdati:
  - Produkti:         data/products.csv
  - Kategorijas:      data/categories.csv

Papilddati:
  - Rezerves kopijas: data/backup/
  - Eksporta mape:    data/export/

## 6. Atbalsts
```bash
git clone https://github.com/aesdetgewr/Veikala-Inventara-Sistema
cd Veikala-Inventara-Sistema
mvn clean install
```

## Lietošanas pamācība
1. Programmas palaišana:
```bash
java -jar target/java-console-1.0-SNAPSHOT.jar
```

2. Galvenās komandas:
- 1-4: Produktu pārvaldība
- 5-7: Kategoriju un meklēšanas opcijas
- 8-9: Datu saglabāšana un programmas aizvēršana
- 10: Informācija un statistika

## 5. Tehniskā Informācija

### Projekta Struktūra
```
src/main/java/lv/rvt/
├── interfaces/     # Interfeisi
├── tools/         # Palīgrīki
│   ├── BackupConfig.java    # Rezerves kopiju konfigurācija
│   ├── ConfigManager.java   # Sistēmas konfigurācija
│   ├── ConsoleUI.java      # Konsoles UI komponentes
│   ├── CsvHelper.java      # CSV failu apstrāde
│   ├── Helper.java         # Validācijas un palīgfunkcijas
│   ├── MessageManager.java # Daudzvalodu atbalsts
│   └── RecoveryManager.java # Datu atjaunošana
├── Product.java   # Produktu modelis
├── Category.java  # Kategoriju modelis
└── Main.java      # Programmas sākumpunkts
```

### Datu Glabāšana
- Produkti: data/products.csv
- Kategorijas: data/categories.csv
- Rezerves kopijas: data/backup/YYYYMMDD_HHMMSS/
- Eksports: data/export/YYYYMMDD_HHMMSS/

### Sistēmas Prasības
- Java Runtime Environment (JRE) 21
- Brīva vieta diskā: vismaz 100MB
- RAM: vismaz 256MB
- Atbalstītās OS: Windows, Linux, macOS

### Drošība un Datu Aizsardzība
- Automātiskas rezerves kopijas ik pēc 5 izmaiņām
- Validācija visiem ievades datiem
- Datu integritātes pārbaudes
- Droša failu lasīšana/rakstīšana
#### Palīdzība
- GitHub: https://github.com/aesdetgewr/Veikala-Inventara-Sistema
- E-pasts: A231130RS@RVT.LV

### Kļūdu Ziņošana
1. Aprakstiet problēmu
2. Pievienojiet kļūdas paziņojumu
3. Norādiet, kā atkārtot problēmu

---
© 2025 Veikala Inventāra Sistēma


# Veikala Inventāra Pārvaldības Sistēma

## Apraksts
Java konsoļu aplikācija veikala inventāra pārvaldībai, kas nodrošina produktu un kategoriju pārvaldību ar paplašinātām funkcijām.

## Funkcionalitāte
- Produktu pārvaldība (pievienošana, rediģēšana, dzēšana)
- Kategoriju pārvaldība ar hierarhiju
- Meklēšana un filtrēšana pēc vairākiem parametriem
- Datu eksports CSV un JSON formātos
- Statistikas aprēķini un pārskati
- Konfigurējama lietotāja saskarne

## Instalācija
```bash
git clone https://github.com/your-username/Veikala-Inventara-Sistema.git
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

## Testēšana
```bash
mvn test
```

## Test Results
| Test Category | Tests | Passed | Failed |
|--------------|-------|---------|--------|
| Products     | 15    | 15      | 0      |
| Categories   | 8     | 8       | 0      |
| File I/O     | 6     | 6       | 0      |
| Validation   | 10    | 10      | 0      |
| User Auth    | 4     | 4       | 0      |
| Total        | 43    | 43      | 0      |

### Test Coverage
- Line Coverage: 92%
- Branch Coverage: 87%
- Method Coverage: 95%

### Known Issues Fixed
1. Category validation improved
2. File error handling enhanced
3. Input validation strengthened
4. User authentication added
5. Undo functionality implemented

## Tehniskā dokumentācija
### Klašu struktūra
- `Main.java` - Programmas sākumpunkts
- `UserInterfacePart1.java` - Lietotāja saskarnes pamata funkcijas
- `UserInterfacePart2.java` - Paplašinātās lietotāja saskarnes funkcijas
- `InventoryManager.java` - Produktu un kategoriju pārvaldība
- `FileManager.java` - Datu saglabāšana un ielāde
- `Category.java` - Kategoriju hierarhija
- `Product.java` - Produkta entitāte
- `User.java` - Lietotāju pārvaldība
- `Helper.java` - Validācijas palīgfunkcijas

### Datu plūsma
1. Lietotāja ievade → UI validācija
2. Biznesa loģikas apstrāde
3. Datu saglabāšana
4. Atbildes attēlošana

### Funkcijas
- Produktu CRUD operācijas
- Kategoriju pārvaldība ar hierarhiju
- Lietotāju lomas (Administrators/Parasts lietotājs)
- Datu eksports (CSV/JSON)
- Paplašināta meklēšana un filtrēšana
- Statistikas pārskati

### Datu formāti
- CSV fails produktiem: ID,ProductName,Category,Price,Quantity
- CSV fails kategorijām: CategoryName
- JSON eksports: Strukturēts produktu saraksts

## Zināmās problēmas un risinājumi
1. Ja programma neatrod datu failus, tie tiek automātiski izveidoti
2. Kategoriju nosaukumos atļauti tikai burti un apakšsvītras
3. Negatīvas vērtības cenām un daudzumiem tiek noraidītas

## Kļūdu ziņojumi
- "Nederīgs formāts": Nederīgs ievades formāts
- "Kategorija jau eksistē": Kategorija jau eksistē
- "Nav atrasts": Vienība nav atrasta


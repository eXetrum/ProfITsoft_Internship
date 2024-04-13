## Завдання Блоку 1: Java Core

Контекст

Оберіть предметну область з двома сутностями, одна з яких основна, відноситься до другорядної як багато-до-одного.
Наприклад, Студенти-Група, Книги-Автор, Замовлення-Клієнт, і т.і.
Основна сутність має кілька атрибутів. Наприклад, якщо це Книга, то у неї є назва, рік публікації, перелік жанрів і т.і.
Всі подальші завдання будуть на базі цих сутностей і повинні в кінці скластися в єдиний проект.

Завдання

Розробити консольну програму-скрипт, яка парсить перелік JSON-файлів основної сутності і формує статистику (загальну кількість) в розрізі одного з її атрибутів.
В якості параметрів запуску вона отримує шлях до папки, де зберігаються JSON-файли (їх там може бути декілька) і назву атрибута, по якому формувати статистику.
Програма має підтримувати роботу з кількома атрибутами, а користувач буде вказувати один з них.
Один з атрибутів має бути текстовим і мати кілька значень (категорії через кому, хеш-теги і т.і.).
В якості результату роботи, програма формує XML-файл зі статистикою, відсортованою по кількості від більшого до меншого. Назва файлу з результатами повинна бути statistics_by_{attribute}.xml.
  
Приклад
Наприклад, якщо наша предметна область про Книги, то формат може бути таким.
  
```json
[
  {
    "title": "1984",
    "author": "George Orwell",
    "year_published": 1949,
    "genre": "Dystopian, Political Fiction"
  },
  {
    "title": "Pride and Prejudice",
    "author": "Jane Austen",
    "year_published": 1813,
    "genre": "Romance, Satire"
  },
  {
    "title": "Romeo and Juliet",
    "author": "William Shakespeare",
    "year_published": 1597,
    "genre": "Romance, Tragedy"
  }
]
```  
Для такого файлу, скрипт міг би формувати статистику по атрибутах author, year_published, genre.
Якщо користувач запросить формування статистики по genre, то файл з результатами statistics_by_genre.xml міг би мати такий контент.
```xml
<statistics>
  <item>
    <value>Romance</value>
    <count>2</count>
  </item>
  <item>
    <value>Dystopian</value>
    <count>1</count>
  </item>
  ...
</statistics>
```  


## Сборка

```shell
mvn compile assembly:single
```  
или  
```shell
assembly.bat
```  
  
## Запуск
```shell
java -jar target\BooksAnalyzer.jar <json folder path> <attribute name>
	<attribute name>: title, subject, author, publish_year
```

## Тестирование
```shell
mvn test
```

## Thread pool size
Average time - mean of five runs with identical params
```console
title
```
<table>
    <thead>
        <tr>
            <th>Num of threads</th>
            <th>Average time (ms)</th>
        </tr>
    </thead>
    <tbody align="center">
        <tr><td>1</td><td>403</td></tr>
        <tr><td>2</td><td>370</td></tr>
        <tr><td>4</td><td>353</td></tr>
        <tr><td>8</td><td>349</td></tr>
        <tr><td>16</td><td>373</td></tr>
        <tr><td>32</td><td>429</td></tr>
    </tbody>
</table>  

```console
publishYear
```
<table>
    <thead>
        <tr>
            <th>Num of threads</th>
            <th>Average time (ms)</th>
        </tr>
    </thead>
    <tbody align="center">
        <tr><td>1</td><td>331</td></tr>
        <tr><td>2</td><td>294</td></tr>
        <tr><td>4</td><td>289</td></tr>
        <tr><td>8</td><td>278</td></tr>
        <tr><td>16</td><td>295</td></tr>
        <tr><td>32</td><td>332</td></tr>
    </tbody>
</table>

```console
authors
```
<table>
    <thead>
        <tr>
            <th>Num of threads</th>
            <th>Average time (ms)</th>
        </tr>
    </thead>
    <tbody align="center">
        <tr><td>1</td><td>389</td></tr>
        <tr><td>2</td><td>352</td></tr>
        <tr><td>4</td><td>347</td></tr>
        <tr><td>8</td><td>346</td></tr>
        <tr><td>16</td><td>361</td></tr>
        <tr><td>32</td><td>367</td></tr>
    </tbody>
</table>

```console
subjects
```
<table>
    <thead>
        <tr>
            <th>Num of threads</th>
            <th>Average time (ms)</th>
        </tr>
    </thead>
    <tbody align="center">
        <tr><td>1</td><td>564</td></tr>
        <tr><td>2</td><td>520</td></tr>
        <tr><td>4</td><td>497</td></tr>
        <tr><td>8</td><td>505</td></tr>
        <tr><td>16</td><td>508</td></tr>
        <tr><td>32</td><td>579</td></tr>
    </tbody>
</table>

#### Thread pool of size in range [4; 8] is best choice for any attribute name 
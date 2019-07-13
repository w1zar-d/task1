import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class task1 {
    public static void main(String[] args) {
        List<Long> phoneNumberList = new ArrayList<>(); // Список всех номеров

        // Список всех стран, где ключ - код страны (без плюсов и остального),
        // а результат - мапа, где...
        // Ключ - номер региона, значение - лист массива строк, в котором..
        // 0 - Шаблон номера (с иксами всякими)
        // 1 - Название оператора (строка)
        // 2 - Имя человека, которому принадлежит номер (тоже строка)
        HashMap<Integer, HashMap<Integer, List<String[]>>> countryMap = new HashMap<>();

        try {
            FileInputStream fileInputStream = new FileInputStream("./input.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            FileWriter writer = new FileWriter("output.txt", false);
            String string;
            int codeOfEvent = 0; // Код события (телефонные номера, шаблоны и так далее)
            int count = 0; //  Общее число чего-то (телефонных номеров, шаблонов номеров)
            int countLocal = 0; // Число для перебора массива и сопоставления с count (нынешний по счёту номер телефона)

            while ((string = bufferedReader.readLine()) != null) {
                if (codeOfEvent == 0) {
                    // Если первое событие (кол-во строк с номерами)
                    count = Integer.parseInt(string);
                    codeOfEvent++;
                } else if (codeOfEvent == 1) {
                    // Если второе событие (список номеров)
                    if (countLocal == count) {
                        // Если все номера обработаны - получаем количество шаблонов
                        codeOfEvent++;
                        count = Integer.parseInt(string);
                        countLocal = 0;
                    } else {
                        phoneNumberList.add(Long.parseLong(string.replaceAll("\\D+", "")));
                        countLocal++;
                    }
                } else {
                    // Если обрабатывается список шаблонов (или происходит неведомая ерунда)
                    // Pattern format +1 (923) 3XXXXX - OPERATOR PERSON

                    if (countLocal == count) {
                        break;
                    } else {
                        String[] patternString = string.split(" ");
                        int countryCode = Integer.parseInt(patternString[0].replaceAll("\\D+", ""));
                        int operatorCode = Integer.parseInt(patternString[1].replaceAll("\\D+", ""));
                        String pattern = patternString[2];
                        String operatorName = patternString[4];
                        String personName = patternString[5];

                        HashMap<Integer, List<String[]>> operatorMap;
                        List<String[]> paramsList;

                        if (countryMap.get(countryCode) == null) {
                            operatorMap = new HashMap<>();
                            countryMap.put(countryCode, operatorMap);
                        } else {
                            operatorMap = countryMap.get(countryCode);
                        }

                        if (operatorMap.get(operatorCode) == null) {
                            paramsList = new ArrayList<>();
                            operatorMap.put(operatorCode, paramsList);
                        } else {
                            paramsList = operatorMap.get(operatorCode);
                        }

                        // Проверили наличие страны и региона, получили объекты и создали их если не было
                        // Теперь важно не забыть, про...
                        // 0 - Шаблон номера (с иксами всякими)
                        // 1 - Название оператора (строка)
                        // 2 - Имя человека, которому принадлежит номер (тоже строка)

                        String[] params = {pattern, operatorName, personName};
                        paramsList.add(params);

                        // По идее, все объекты теперь связаны и изменения в листах
                        // должны сразу вноситься вверх по иерархии в мапы (объект-то один)
                    }
                }
            }

            // Теперь в `phoneNumberList` лежат все номера телефонов, а в `countryMap` представление стран
            // Ну и нужно их обработать

            count = 0;
            for (Long number : phoneNumberList) {
                // Тут происходит обработка номеров, по одному номеру за цикл
                // Важно помнить, что длина кода страны может быть от одной до трёх цифр
                // Можем пройтись по всем странам, сформировав список тех, которые нам подходят
                // Допустим, для номера 12345
                // Сначала попытаться найти страну с кодом 1, потом с кодом 12, и с кодом 123
                // Но эти страны нужно где-то хранить, поэтому создаём локальную мапу, идентичную мапе стран,
                // но с проверенными значениями

                HashMap<Integer, HashMap> localCountryMap = new HashMap<>();

                // Теперь ищем соответствия
                // Чтобы искать было удобнее - представим номер телефона в виде строки
                String phoneNumberAsString = "" + number;
                // Ну и нужно где-то хранить код страны. Для этого тоже создадим строку и заполним её циклом
                String someString;

                for (int i = 0; i < 3; i++) {
                    someString = "";
                    for (int k = 0; k <= i; k++) {
                        someString += phoneNumberAsString.charAt(k);
                    }
                    HashMap<Integer, List<String[]>> operatorMap = countryMap.get(Integer.parseInt(someString));

                    // Получили мапу страны (если она есть). Так вот если она есть, то вносим в локальную мапу
                    if (operatorMap != null) localCountryMap.put(Integer.parseInt(someString), operatorMap);
                }

                // Теперь у нас есть карта с совпадениями
                // Проидём циклом по результирующему набору
                // Ключом будет - код страны, значением - мапа со всеми операторами страны
                // Схема похожа на схему выше. Но теперь важно помнить что код оператора это
                // код от двух до четырёх цифр.
                // После чего проверим количество совпадений по шаблону и если есть совпадения- добавим
                // в finalCountryMap
                //
                // Добавление будет происходить в два этапа
                // 1 этап: подсчёт количества символов в шаблоне. Если совпадает с оставшимися
                // цифрами - даётся один балл в счётчик и идёт дальше. Иначе - переход к следующему шаблону
                // 2 этап: подсчёт количества совпадающих цифр в шаблоне. За каждое совпадение плюс 1 балл в счётчик
                // В конце каждого цикла второго этапа проверка счётчика. Если значение баллов равно текущему или
                // больше - единственный элемент листа заменяется на элемент текущей итерации

                List<String> equalsList = new ArrayList<>(); // Лист с единственным значением - сформированной картой страны
                int counter = 0; // Счётчик совпадений (не может быть равен нулю. 0 - номер не совпадает даже по количеству цифр)
                String availableOperator;


                for (Map.Entry entry : localCountryMap.entrySet()) {
                    someString = String.valueOf((int)entry.getKey()); // код страны
                    HashMap<Integer, List<String[]>> operatorMap = countryMap.get(Integer.parseInt(someString));

                    // Теперь у нас n-ая по счёту страна. Нужно пройти по всем совпадениям
                    // операторов этой страны и занести их в finalMap

                    for (int i = 1; i < 4; i++) {
                        // Сначала получаем 0 и 1 символы, потом 0,1 и 2 символы
                        // и в конце 0,1,2 и 3 символы строки из строки номера
                        availableOperator = ""; // строка для конкатенации кода оператора

                        for (int k = 0; k <= i; k++) {
                            availableOperator += phoneNumberAsString.charAt((k + someString.length()));
                        }
                        // Допустим, выше получили строку 12
                        // нужно её проверить на наличие в стране someString

                        List<String[]> patternList = operatorMap.get(Integer.parseInt(availableOperator));

                        if (patternList != null) {
                            // Создадим временную мапу для доступных паттернов и вставим её ниже
                            HashMap<Integer, List<String[]>> map = new HashMap<>();
                            map.put(Integer.parseInt(availableOperator), patternList);
                            //finalCountryMap.put(Integer.parseInt(someString), map);


                            // Теперь есть лист паттернов, номер текущей страны, код оператора
                            // осталось лишь проверить совпадения по описанию выше
                            for (String[] pattern : patternList) {
                                // Проверяем каждый шаблон оператора
                                String localNumber = ""; // Номер без кода страны и оператора
                                for (int k = 0; k < phoneNumberAsString.length(); k++) {
                                    // Формируем строку из чистого номера - без кода страны и оператора
                                    if (k >= (someString.length() + availableOperator.length())){
                                        localNumber += phoneNumberAsString.charAt(k);
                                    }
                                }
                                if (pattern[0].length() == localNumber.length()) {
                                    int localCounter = 1; // Локальный счётчик совпадений

                                    // Дальше нужно получить все цифры строки. Так как в условии сказано,
                                    // что после Х не может идти цифра - траим парсинг и если получается -
                                    // заносим цифру в строку. Иначе - останавливаем получение маски

                                    String symbols = "";

                                    for (int k = 0; k < pattern[0].length(); k++) {
                                        try {
                                            Integer.parseInt(String.valueOf(pattern[0].charAt(k)));
                                            symbols += pattern[0].charAt(k);
                                        } catch (NumberFormatException e) {
                                            k = pattern[0].length() + 1;
                                        }
                                    }

                                    // После цикла выше - строка цифр может быть пустой (если была маска ХХ)
                                    // или наполнена цифрами, соответственно. Нужно теперь пройтись по каждой
                                    // цифре строки шаблона сравнить её с каждой цифрой строки номера
                                    // Каждое совпадение увеличивает счётчик

                                    for (int k = 0; k < symbols.length(); k++) {
                                        if (Integer.parseInt(String.valueOf(localNumber.charAt(k))) == Integer.parseInt(String.valueOf(symbols.charAt(k)))) localCounter++;
                                    }

                                    // Проверили совпадения. Теперь сопоставляем с количеством совпадений глобального счётчика
                                    // если в глобальном счётчике столько же совпадений сколько и в локальном или больше -
                                    // записываем в лист мап на индекс 0

                                    if (localCounter >= counter) {
                                        counter = localCounter;
                                        equalsList.clear();
                                        // 1 234 5689 country person

                                        equalsList.add(someString); // Добавили код страны
                                        equalsList.add(availableOperator); // Добавили оператора
                                        equalsList.add(localNumber); // Добавили номер телефона
                                        equalsList.add(pattern[1]); // Добавили имя оператора
                                        equalsList.add(pattern[2]); // Добавили Имя человека
                                    }
                                }
                            }
                        }
                    }
                }

                String result = ((count == 0) ? "" : "\r\n") + "+" + equalsList.get(0) + " ("+ equalsList.get(1) + ") " + equalsList.get(2) + " - " + equalsList.get(3) + " " + equalsList.get(4);
                count++;
                writer.write(result);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

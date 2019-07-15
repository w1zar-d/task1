import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class test1 {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new
                            FileInputStream("./input.txt"))); // Читаем файл
            boolean isFirst = true; // Код обрабатываемого события (0 - количество слов, 1 - список слов)
            String patternWord;
            String firstPar;
            String secondPar;
            int strLength;
            HashMap<String, Integer> uniq = new HashMap<>();

            HashMap<String, Integer> pars = new HashMap<>();
            String string;

            while ((string = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    strLength = (string.length() - 3); // Вычитаем 3 потому что обрабатываем по 4 символа за раз
                    for (int i = 0; i < strLength; i++) {
                        patternWord = string.substring(i, i + 4); // Тут будем хранить часть слова из 4 символов, которое обрабатываем в данный момент
                        firstPar = patternWord.substring(0, 3); // Тут будем хранить первые три символа (начало ребра)
                        secondPar = patternWord.substring(1, 4); // Тут будем хранить вторые три символа (конец ребра)

                        // Теперь есть две пары символов: S₁: n, n+1, n+2; S₂: n+1, n+2, n+3
                        // Нужно:
                        // 1. Проверить два ребра на наличие в общей карте и если их нет, то добавить
                        // иначе - прибавить 1 к счётчику
                        // 2. Проверить в карте наличие уникальных рёбер firstPar и secondPar. Если нет,
                        // то добавить. Иначе - проигнорировать


                        // 1
                        pars.merge(firstPar + secondPar, 1, Integer::sum);

                        // 2
                        uniq.putIfAbsent(firstPar, 1);
                        uniq.putIfAbsent(secondPar, 1);
                    }
                }
            }
            StringBuffer result = new StringBuffer();
            // Выводим количество уникальных рёбер и общее количество пар во второй строке
            result.append(uniq.size()).append("\r\n").append(pars.size());

            // Циклом заносим все значения из мапы
            pars.forEach((a, b) -> result.append("\r\n").append(a, 0, 3).append(" ").append(a, 3, 6).append(" ").append(b));

            // Выводим результат
            System.out.println(result);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}

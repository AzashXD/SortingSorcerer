import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String language = getLanguage();
        displayValueWarning(language);
        ArrayList<Integer> list = getList(language);
        printOrSaveList(list, language);
        String order = getSortingOrder(language);
        String sortingMethod;
        do {
            sortingMethod = getSortingMethod(language);
        } while (!confirmSortMethod(language, sortingMethod));
        ArrayList<Integer> sortedList = sortList(list, sortingMethod, order, language);
        String outputPreference = getOutputPreference(language);

        switch (outputPreference) {
            case "Display":
                System.out.println(sortedList.toString());
                break;
            case "Save":
                String layoutPreference = getLayoutPreference(language);
                String path = "";
                boolean isSaved = false;
                while (!isSaved) {
                    path = getPath(language);
                    isSaved = saveToFile(sortedList, layoutPreference, path, language);
                }
                break;
            case "None":
                break;
        }
    }






    private static String getLanguage() {
        System.out.println("Выберите язык / Choose language (рус/eng):");
        String language = scanner.nextLine().toLowerCase();

        while (!language.equals("ru") && !language.equals("en") && !language.equals("рус") && !language.equals("eng")) {
            System.out.println("Некорректный ввод, попробуйте снова / Incorrect input, try again (ru/рус/en/eng):");
            language = scanner.nextLine().toLowerCase();
        }

        if (language.equals("рус")) {
            language = "ru";
        } else if (language.equals("eng")) {
            language = "en";
        }
        return language;
    }

    private static void displayValueWarning(String language) {
        String message = language.equals("ru") ?
                """


                        Важно: значения любых элементов в списке должны быть целыми числами, применимыми к формату int.
                        Ограничения: минимальное значение -2147482648, максимальное значение 2147482647.

                        """ :
                """


                        Note: values of any elements in the list should be integer numbers applicable to int format.
                        Limits: minimum value -2147482648, maximum value 2147482647.

                        """;

        System.out.println(message);
    }

    private static ArrayList<Integer> getList(String language) {
        System.out.println(language.equals("ru") ? "Хотите ли вы ввести список чисел или сгенерировать автоматически? (1 - ввести, 2 - сгенерировать)" : "Would you like to input the list of numbers or generate it automatically? (1 - input, 2 - generate)");
        int listType;
        while ((listType = getIntegerInput(language)) != 1 && listType != 2) {
            System.out.println(language.equals("ru") ? "Некорректный ввод, попробуйте снова (1 - ввести, 2 - сгенерировать)" : "Incorrect input, try again (1 - input, 2 - generate)");
        }
        return (listType == 1) ? inputList(language) : generateList(language);
    }

    private static ArrayList<Integer> generateList(String language) {
        int size;
        do {
            System.out.println(language.equals("ru") ? "Введите количество элементов в списке:" : "Enter the number of elements in the list:");
            size = getIntegerInput(language);

            if(size < 2) {
                System.out.println(language.equals("ru") ? "Список должен содержать минимум 2 элемента. Попробуйте снова." : "The list should contain at least 2 elements. Please try again.");
            }
        } while(size < 2);

        long minVal;
        long maxVal;
        do {
            System.out.println(language.equals("ru") ? "Введите минимальное значение элемента:" : "Enter the minimum value of the element:");
            minVal = getLongInput(language);

            System.out.println(language.equals("ru") ? "Введите максимальное значение элемента:" : "Enter the maximum value of the element:");
            maxVal = getLongInput(language);

            if(minVal >= maxVal) {
                System.out.println(language.equals("ru") ? "Минимальное значение должно быть меньше максимального. Попробуйте снова." : "The minimum value should be less than the maximum. Please try again.");
            } else if ((maxVal - minVal) > Integer.MAX_VALUE) {
                System.out.println(language.equals("ru") ? "Разница между максимальным и минимальным значениями не должна превышать " + Integer.MAX_VALUE + ". Попробуйте снова." : "The difference between the maximum and minimum values should not exceed " + Integer.MAX_VALUE + ". Please try again.");
                minVal = maxVal = 0; // reset values to prompt new input
            }

        } while(minVal >= maxVal);

        Random random = new Random();
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            list.add((int)(random.nextInt((int)(maxVal - minVal) + 1) + minVal));
        }
        return list;
    }

    private static void printOrSaveList(ArrayList<Integer> list, String language) {
        String outputPreference;
        do {
            System.out.println(language.equals("ru") ? "Как вы хотите отобразить список? Введите соответствующий номер:" :
                    "How do you want to display the list? Enter the corresponding number:");
            if (language.equals("ru")) {
                System.out.println("1. Вывести на экран\n2. Сохранить как текстовый файл\n3. Не выводить");
            } else {
                System.out.println("1. Display on screen\n2. Save as text file\n3. Do not display");
            }

            int outputPreferenceNumber = getIntegerInput(language);
            switch (outputPreferenceNumber) {
                case 1:
                    outputPreference = "Display";
                    System.out.println(list.toString());
                    break;
                case 2:
                    outputPreference = "Save";
                    String layout = getLayoutPreference(language);  // Function to be implemented
                    String path = getPath(language);  // Function to be implemented
                    saveToFile(list, layout, path, language);  // Function to be implemented
                    break;
                case 3:
                    outputPreference = "None";
                    break;
                default:
                    outputPreference = "";
                    System.out.println(language.equals("ru") ? "Некорректный ввод, попробуйте снова." : "Invalid input, please try again.");
                    break;
            }
        } while (outputPreference.equals(""));
    }

    private static ArrayList<Integer> inputList(String language) {
        System.out.println(language.equals("ru") ?
                "Введите числа через пробел или предоставьте путь к .txt файлу.\n" +
                        "Файл должен содержать целые числа, разделенные пробелами или переносами строк,\n" +
                        "количество чисел должно быть больше одного, и числа не должны быть все одинаковыми." :
                "Enter the numbers separated by a space or provide a path to a .txt file.\n" +
                        "The file should contain integers separated by spaces or new lines,\n" +
                        "the number count should be more than one, and the numbers should not all be the same.");

        ArrayList<Integer> list = new ArrayList<>();
        String inputLine = scanner.nextLine();

        if(inputLine.endsWith(".txt")) {
            Path path = Paths.get(inputLine);
            try (Stream<String> lines = Files.lines(path)) {
                list = lines.flatMap(line -> Stream.of(line.split(" ")))
                        .map(Integer::valueOf)
                        .collect(Collectors.toCollection(ArrayList::new));
            } catch (IOException | NumberFormatException e) {
                System.out.println(language.equals("ru") ? "Произошла ошибка при чтении файла или файл содержит некорректные данные, попробуйте снова." : "An error occurred while reading the file or the file contains invalid data, please try again.");
                return inputList(language);
            }
        } else {
            String[] input = inputLine.split(" ");
            for (String number : input) {
                try {
                    list.add(Integer.parseInt(number));
                } catch (NumberFormatException e) {
                    System.out.println(language.equals("ru") ? "Одно или несколько введенных чисел некорректны, попробуйте снова, вводя только целые числа." : "One or more of the entered numbers are invalid, please try again entering only integers.");
                    return inputList(language);
                }
            }
        }

        if(list.size() == 1){
            System.out.println(language.equals("ru") ? "Введено только одно число. Пожалуйста, введите список чисел (более одного числа)." : "Only one number has been entered. Please enter a list of numbers (more than one number).");
            return inputList(language);
        }

        if(new HashSet<>(list).size() == 1){
            System.out.println(language.equals("ru") ? "Все введенные числа одинаковы. Введите разные числа для сортировки." : "All entered numbers are the same. Please enter different numbers for sorting.");
            return inputList(language);
        }

        return list;
    }

    private static int getIntegerInput(String language) {
        Scanner scanner = new Scanner(System.in);
        int number;
        while (true) {
            String input = scanner.nextLine();
            try {
                number = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println(language.equals("ru") ? "Некорректный ввод, попробуйте снова." : "Invalid input, please try again.");
            }
        }
        return number;
    }

    private static String getSortingOrder(String language) {
        System.out.println(language.equals("ru") ? "Как вы хотите отсортировать список? (1 - возрастание, 2 - убывание)" : "How do you want to sort the list? (1 - ascending, 2 - descending)");
        int order;
        while ((order = getIntegerInput(language)) != 1 && order != 2) {
            System.out.println(language.equals("ru") ? "Некорректный ввод, попробуйте снова (1 - возрастание, 2 - убывание)" : "Incorrect input, try again (1 - ascending, 2 - descending)");
        }
        return (order == 1) ? "asc" : "desc";
    }

    private static String getSortingMethod(String language) {
        String sortMethod;
        do {
            System.out.println(language.equals("ru") ? "Выберите метод сортировки, введя соответствующий номер:" : "Choose a sorting method by entering the corresponding number:");
            if (language.equals("ru")) {
                System.out.println("1. Пузырьковая сортировка (Bubble sort)\n2. Сортировка выбором (Selection sort)\n3. Сортировка вставками (Insertion sort)\n4. Быстрая сортировка (Quick sort)\n5. Сортировка слиянием (Merge sort)\n6. Сортировка Шелла (Shell sort)\n7. Сортировка кучей (Heap sort)");
            } else {
                System.out.println("1. Bubble sort\n2. Selection sort\n3. Insertion sort\n4. Quick sort\n5. Merge sort\n6. Shell sort\n7. Heap sort");
            }
            int sortMethodNumber = getIntegerInput(language);
            switch (sortMethodNumber) {
                case 1:
                    sortMethod = "Bubble sort";
                    break;
                case 2:
                    sortMethod = "Selection sort";
                    break;
                case 3:
                    sortMethod = "Insertion sort";
                    break;
                case 4:
                    sortMethod = "Quick sort";
                    break;
                case 5:
                    sortMethod = "Merge sort";
                    break;
                case 6:
                    sortMethod = "Shell sort";
                    break;
                case 7:
                    sortMethod = "Heap sort";
                    break;
                default:
                    sortMethod = "";
                    System.out.println(language.equals("ru") ? "Некорректный выбор, попробуйте снова." : "Invalid choice, please try again.");
                    break;
            }
        } while (sortMethod.equals(""));

        return sortMethod;
    }

    private static ArrayList<Integer> sortList(ArrayList<Integer> list, String method, String order, String language) {
        long startTime = System.nanoTime();
        switch (method) {
            case "Bubble sort":
            case "Пузырьковая сортировка":
                list = bubbleSort(list, order);
                break;
            case "Selection sort":
            case "Сортировка выбором":
                list = selectionSort(list, order);
                break;
            case "Quick sort":
            case "Быстрая сортировка":
                list = quickSort(list, order);
                break;
            case "Insertion sort":
            case "Сортировка вставками":
                list = insertionSort(list, order);
                break;
            case "Merge sort":
            case "Сортировка слиянием":
                list = mergeSort(list, order);
                break;
            case "Shell sort":
            case "Сортировка Шелла":
                list = shellSort(list, order);
                break;
            case "Heap sort":
            case "Сортировка кучей":
                list = heapSort(list, order);
                break;

        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double seconds = (double)duration / 1_000_000_000.0;
        String formattedSeconds = String.format("%.3f", seconds);
        String outputString = language.equals("ru") ? "Затраченное время: " + duration + " наносекунд" : "Time taken: " + duration + " nanoseconds";
        if(seconds > 0.0005) {
            outputString += " ~ " + formattedSeconds + (language.equals("ru") ? " секунды" : " seconds");
        }
        System.out.println(outputString);
        return list;
    }

    private static boolean confirmSortMethod(String language, String sortMethod) {
        String sortMethodDescription = getSortMethodDescription(language, sortMethod);
        System.out.println(sortMethodDescription);
        String answer;
        do {
            System.out.println(language.equals("ru") ? "Вы уверены, что хотите использовать этот метод сортировки? (да/нет):" : "Are you sure you want to use this sorting method? (yes/no):");
            answer = scanner.nextLine().trim().toLowerCase();
        } while (!answer.equals("yes") && !answer.equals("no") && !answer.equals("да") && !answer.equals("нет"));

        return answer.equals("yes") || answer.equals("да");
    }

    private static String getSortMethodDescription(String language, String sortMethod) {
        String description = "";
        switch (sortMethod) {
            case "Bubble sort":
                description = language.equals("ru") ?
                        "Пузырьковая сортировка:\n" +
                                "Плюсы:\n" +
                                "1. Простота реализации.\n" +
                                "2. Не требует дополнительной памяти.\n" +
                                "Минусы:\n" +
                                "1. Низкая эффективность для больших массивов.\n" +
                                "2. Медленная скорость сортировки.\n" +
                                "Принцип работы: Перебираются соседние элементы массива. Если они стоят в неправильном порядке, их меняют местами."
                        :
                        "Bubble sort:\n" +
                                "Pros:\n" +
                                "1. Easy to implement.\n" +
                                "2. Doesn't require additional memory.\n" +
                                "Cons:\n" +
                                "1. Low efficiency for large arrays.\n" +
                                "2. Slow sorting speed.\n" +
                                "Working principle: The algorithm goes through the array and swaps adjacent elements if they are in the wrong order.";
                break;
            case "Selection sort":
                description = language.equals("ru") ?
                        "Сортировка выбором:\n" +
                                "Плюсы:\n" +
                                "1. Простота реализации.\n" +
                                "2. Минимальное количество обменов элементов.\n" +
                                "Минусы:\n" +
                                "1. Низкая эффективность для больших массивов.\n" +
                                "2. Относительно медленная скорость сортировки.\n" +
                                "Принцип работы: В каждом проходе алгоритм находит минимальный элемент и меняет его местами с первым ненулевым."
                        :
                        "Selection sort:\n" +
                                "Pros:\n" +
                                "1. Easy to implement.\n" +
                                "2. Minimal number of element swaps.\n" +
                                "Cons:\n" +
                                "1. Low efficiency for large arrays.\n" +
                                "2. Relatively slow sorting speed.\n" +
                                "Working principle: On each pass, the algorithm finds the minimum element and swaps it with the first non-zero one.";
                break;
            case "Insertion sort":
                description = language.equals("ru") ?
                        "Сортировка вставками:\n" +
                                "Плюсы:\n" +
                                "1. Простота реализации.\n" +
                                "2. Хорошо работает для небольших массивов или для массивов, которые уже частично отсортированы.\n" +
                                "Минусы:\n" +
                                "1. Неэффективна для больших массивов.\n" +
                                "2. Может быть медленной.\n" +
                                "Принцип работы: В каждом проходе алгоритм берет следующий элемент и вставляет его на правильное место в уже отсортированной части массива."
                        :
                        "Insertion sort:\n" +
                                "Pros:\n" +
                                "1. Simple to implement.\n" +
                                "2. Works well for small arrays or arrays that are partially sorted.\n" +
                                "Cons:\n" +
                                "1. Inefficient for large arrays.\n" +
                                "2. Can be slow.\n" +
                                "Working principle: On each pass, the algorithm takes the next element and inserts it into the correct place in the already sorted part of the array.";
                break;
            case "Quick sort":
                description = language.equals("ru") ?
                        "Быстрая сортировка:\n" +
                                "Плюсы:\n" +
                                "1. Один из самых быстрых алгоритмов сортировки для больших массивов.\n" +
                                "2. Сортирует на месте, не требуя дополнительной памяти.\n" +
                                "Минусы:\n" +
                                "1. Скорость сортировки зависит от выбора опорного элемента.\n" +
                                "2. Сложнее в реализации, чем некоторые другие алгоритмы.\n" +
                                "Принцип работы: Разделение и завоевание. Массив разделяется на две части по опорному элементу, затем эти части сортируются отдельно."
                        :
                        "Quick sort:\n" +
                                "Pros:\n" +
                                "1. One of the fastest sorting algorithms for large arrays.\n" +
                                "2. Sorts in place, requiring no extra memory.\n" +
                                "Cons:\n" +
                                "1. The speed of sorting depends on the choice of the pivot element.\n" +
                                "2. More complex to implement than some other algorithms.\n" +
                                "Working principle: Divide and conquer. The array is split into two parts around a pivot element, then these parts are sorted separately.";
                break;
            case "Merge sort":
                description = language.equals("ru") ?
                        "Сортировка слиянием:\n" +
                                "Плюсы:\n" +
                                "1. Эффективна для больших массивов.\n" +
                                "2. Стабильна (не меняет порядок равных элементов).\n" +
                                "Минусы:\n" +
                                "1. Требует дополнительной памяти.\n" +
                                "2. Сложнее в реализации, чем некоторые другие алгоритмы.\n" +
                                "Принцип работы: Разделение и завоевание. Массив разделяется на две части, которые затем сортируются отдельно и сливаются вместе."
                        :
                        "Merge sort:\n" +
                                "Pros:\n" +
                                "1. Efficient for large arrays.\n" +
                                "2. Stable (does not change the order of equal elements).\n" +
                                "Cons:\n" +
                                "1. Requires additional memory.\n" +
                                "2. More complex to implement than some other algorithms.\n" +
                                "Working principle: Divide and conquer. The array is split into two parts, which are then sorted separately and merged together.";
                break;
            case "Shell sort":
                description = language.equals("ru") ?
                        "Сортировка Шелла:\n" +
                                "Плюсы:\n" +
                                "1. Эффективнее, чем простые алгоритмы сортировки для больших массивов.\n" +
                                "2. Сортирует на месте, не требуя дополнительной памяти.\n" +
                                "Минусы:\n" +
                                "1. Сложность реализации.\n" +
                                "2. Скорость сортировки зависит от выбора шага.\n" +
                                "Принцип работы: Улучшенная версия сортировки вставками, которая сначала сортирует элементы с определенным шагом, а затем уменьшает шаг."
                        :
                        "Shell sort:\n" +
                                "Pros:\n" +
                                "1. More efficient than simple sorting algorithms for large arrays.\n" +
                                "2. Sorts in place, requiring no extra memory.\n" +
                                "Cons:\n" +
                                "1. Complexity of implementation.\n" +
                                "2. The sorting speed depends on the choice of gap.\n" +
                                "Working principle: An improved version of insertion sort that first sorts elements with a certain gap, then reduces the gap.";
                break;
            case "Heap sort":
                description = language.equals("ru") ?
                        "Сортировка кучей:\n" +
                                "Плюсы:\n" +
                                "1. Высокая эффективность даже для больших массивов.\n" +
                                "2. Сортирует на месте, не требуя дополнительной памяти.\n" +
                                "Минусы:\n" +
                                "1. Сложная в реализации.\n" +
                                "2. Неустойчива: равные элементы могут менять свой порядок в процессе сортировки.\n" +
                                "Принцип работы: Превращает массив в кучу (специальное двоичное дерево), затем извлекает максимальные элементы один за другим."
                        :
                        "Heap sort:\n" +
                                "Pros:\n" +
                                "1. High efficiency even for large arrays.\n" +
                                "2. Sorts in place, requiring no additional memory.\n" +
                                "Cons:\n" +
                                "1. Complex to implement.\n" +
                                "2. Unstable: equal elements can change their order during sorting.\n" +
                                "Working principle: It turns the array into a heap (a special binary tree), then extracts the maximum elements one by one.";
                break;
        }
        return description;
    }

    private static ArrayList<Integer> bubbleSort(ArrayList<Integer> list, String order) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if ((order.equals("asc") && list.get(j) > list.get(j + 1)) ||
                        (order.equals("desc") && list.get(j) < list.get(j + 1))) {
                    int temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        return list;
    }

    public static ArrayList<Integer> quickSort(ArrayList<Integer> list, String order) {
        if (list.size() <= 1) {
            return list; // Already sorted
        }

        ArrayList<Integer> sorted = new ArrayList<>();
        ArrayList<Integer> lesser = new ArrayList<>();
        ArrayList<Integer> greater = new ArrayList<>();
        Integer pivot = list.get(list.size() - 1); // Use last element as pivot
        for (int i = 0; i < list.size() - 1; i++) {
            if ((order.equals("asc") && list.get(i).compareTo(pivot) < 0) || (order.equals("desc") && list.get(i).compareTo(pivot) > 0)) {
                lesser.add(list.get(i));
            } else {
                greater.add(list.get(i));
            }
        }

        sorted.addAll(quickSort(lesser, order));
        sorted.add(pivot);
        sorted.addAll(quickSort(greater, order));
        return sorted;
    }

    private static ArrayList<Integer> selectionSort(ArrayList<Integer> list, String order) {
        int n = list.size();

        for (int i = 0; i < n-1; i++) {
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (order.equals("asc") ? list.get(j) < list.get(min_idx) : list.get(j) > list.get(min_idx))
                    min_idx = j;

            int temp = list.get(min_idx);
            list.set(min_idx, list.get(i));
            list.set(i, temp);
        }

        return list;
    }

    private static ArrayList<Integer> insertionSort(ArrayList<Integer> list, String order) {
        int size = list.size();
        for(int i = 1; i < size; i++) {
            int key = list.get(i);
            int j = i - 1;

            if (order.equals("asc") || order.equals("По возрастанию")) {
                while (j >= 0 && list.get(j) > key) {
                    list.set(j + 1, list.get(j));
                    j = j - 1;
                }
            } else {
                while (j >= 0 && list.get(j) < key) {
                    list.set(j + 1, list.get(j));
                    j = j - 1;
                }
            }
            list.set(j + 1, key);
        }
        return list;
    }

    private static ArrayList<Integer> mergeSort(ArrayList<Integer> list, String order) {
        if (list.size() <= 1) {
            return list;
        }

        int middle = list.size() / 2;
        ArrayList<Integer> left = new ArrayList<>(list.subList(0, middle));
        ArrayList<Integer> right = new ArrayList<>(list.subList(middle, list.size()));

        left = mergeSort(left, order);
        right = mergeSort(right, order);

        return merge(left, right, order);
    }

    private static ArrayList<Integer> merge(ArrayList<Integer> left, ArrayList<Integer> right, String order) {
        ArrayList<Integer> result = new ArrayList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            if (order.equals("asc")) {
                if (left.get(0) <= right.get(0)) {
                    result.add(left.remove(0));
                } else {
                    result.add(right.remove(0));
                }
            } else {
                if (left.get(0) >= right.get(0)) {
                    result.add(left.remove(0));
                } else {
                    result.add(right.remove(0));
                }
            }
        }

        while (!left.isEmpty()) {
            result.add(left.remove(0));
        }

        while (!right.isEmpty()) {
            result.add(right.remove(0));
        }

        return result;
    }

    private static ArrayList<Integer> shellSort(ArrayList<Integer> list, String order) {
        int n = list.size();
        for (int gap = n/2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = list.get(i);
                int j;
                if (order.equals("asc")) {
                    for (j = i; j >= gap && list.get(j - gap) > temp; j -= gap) {
                        list.set(j, list.get(j - gap));
                    }
                } else {
                    for (j = i; j >= gap && list.get(j - gap) < temp; j -= gap) {
                        list.set(j, list.get(j - gap));
                    }
                }
                list.set(j, temp);
            }
        }
        return list;
    }

    private static void heapify(ArrayList<Integer> list, int n, int i, String order) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (order.equals("asc")) {
            if (left < n && list.get(left) > list.get(largest)) {
                largest = left;
            }

            if (right < n && list.get(right) > list.get(largest)) {
                largest = right;
            }
        } else {
            if (left < n && list.get(left) < list.get(largest)) {
                largest = left;
            }

            if (right < n && list.get(right) < list.get(largest)) {
                largest = right;
            }
        }

        if (largest != i) {
            int swap = list.get(i);
            list.set(i, list.get(largest));
            list.set(largest, swap);

            heapify(list, n, largest, order);
        }
    }

    private static ArrayList<Integer> heapSort(ArrayList<Integer> list, String order) {
        int n = list.size();

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, order);
        }

        for (int i = n - 1; i >= 0; i--) {
            int temp = list.get(0);
            list.set(0, list.get(i));
            list.set(i, temp);

            heapify(list, i, 0, order);
        }
        return list;
    }

    private static String getOutputPreference(String language) {
        String outputPreference;
        do {
            System.out.println(language.equals("ru") ? "Как вы хотите отобразить отсортированный список? Введите соответствующий номер:" :
                    "How do you want to display the sorted list? Enter the corresponding number:");
            if (language.equals("ru")) {
                System.out.println("1. Вывести на экран\n2. Сохранить как текстовый файл\n3. Не выводить");
            } else {
                System.out.println("1. Display on screen\n2. Save as text file\n3. Do not display");
            }

            int outputPreferenceNumber = getIntegerInput(language);
            switch (outputPreferenceNumber) {
                case 1:
                    outputPreference = "Display";
                    break;
                case 2:
                    outputPreference = "Save";
                    break;
                case 3:
                    outputPreference = "None";
                    break;
                default:
                    outputPreference = "";
                    System.out.println(language.equals("ru") ? "Некорректный выбор, попробуйте снова." : "Invalid choice, please try again.");
                    break;
            }
        } while (outputPreference.equals(""));

        return outputPreference;
    }

    private static String getLayoutPreference(String language) {
        String layoutPreference;
        do {
            System.out.println(language.equals("ru") ? "Вы хотите сохранить каждый элемент списка в отдельной строке или все элементы в одной строке? Введите соответствующий номер:" :
                    "Do you want to save each list item on a separate line or all items on one line? Enter the corresponding number:");
            if (language.equals("ru")) {
                System.out.println("1. В столбик\n2. В строчку");
            } else {
                System.out.println("1. Column\n2. Row");
            }

            int layoutPreferenceNumber = getIntegerInput(language);
            switch (layoutPreferenceNumber) {
                case 1:
                    layoutPreference = "Column";
                    break;
                case 2:
                    layoutPreference = "Row";
                    break;
                default:
                    layoutPreference = "";
                    System.out.println(language.equals("ru") ? "Некорректный выбор, попробуйте снова." : "Invalid choice, please try again.");
                    break;
            }
        } while (layoutPreference.equals(""));

        return layoutPreference;
    }

    private static String getPath(String language) {
        String path = "";
        do {
            System.out.println(language.equals("ru") ? "Введите полный путь к файлу, в который вы хотите сохранить список. Например, C:\\Users\\YourUsername\\Desktop\\sortedList.txt" :
                    "Enter the full path to the file where you want to save the list. For example, C:\\Users\\YourUsername\\Desktop\\sortedList.txt");
            path = scanner.nextLine().trim();

            if (!path.endsWith(".txt")) {
                System.out.println(language.equals("ru") ? "Пожалуйста, укажите путь к текстовому файлу (.txt)." : "Please specify a path to a text file (.txt).");
                continue;
            }

            try {
                Paths.get(path);
            } catch (InvalidPathException e) {
                System.out.println(language.equals("ru") ? "Введен некорректный путь. Пожалуйста, проверьте его и попробуйте снова." : "Invalid path entered. Please check it and try again.");
                continue;
            }

            return path;
        } while (true);
    }

    private static boolean saveToFile(ArrayList<Integer> list, String layout, String path, String language) {
        while (true) {
            try (PrintWriter writer = new PrintWriter(path)) {
                if (layout.equals("Column")) {
                    for (int number : list) {
                        writer.println(number);
                    }
                } else {
                    writer.print(list.toString());
                }
                return true;
            } catch (FileNotFoundException e) {
                System.out.println(language.equals("ru") ? "Не удалось сохранить файл. Проверьте путь к файлу и попробуйте снова." : "Unable to save the file. Please check the file path and try again.");
                path = getPath(language);
            }
        }
    }

    private static long getLongInput(String language) {
        Scanner scanner = new Scanner(System.in);
        long number;
        while (true) {
            String input = scanner.nextLine();
            try {
                number = Long.parseLong(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println(language.equals("ru") ? "Некорректный ввод, попробуйте снова." : "Invalid input, please try again.");
            }
        }
        return number;
    }
}


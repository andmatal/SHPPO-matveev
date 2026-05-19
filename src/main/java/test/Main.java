/*
 * Работу выполнил: Матвеев Андрей Алексевич
 * Группа: 24-ивт-2
 * Вариант: 18
 */
package test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import test.builder.Test;
import test.builder.TestBuilder;
import test.concurrent.TestExecutorPool;
import test.concurrent.TestResult;
import test.config.SpringConfig;
import test.factory.QuestionFactory;
import test.model.Answer;
import test.model.Question;
import test.observer.TestObserver;
import test.strategy.ScoringStrategy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class Main {
    private static ApplicationContext applicationContext;
    private static final Scanner scanner = new Scanner(System.in);
    private static TestExecutorPool executorPool;
    public static void main(String[] args) {
        applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        printSeparator();
        System.out.println("   КОНСТРУКТОР ТЕСТОВ");
        System.out.println("   Spring Framework + Concurrency");
        printSeparator();
        boolean running = true;
        while (running) {
            System.out.println("\nГлавное меню:");
            System.out.println("  1. Создать новый тест и пройти его");
            System.out.println("  2. Демо-тест (одиночный)");
            System.out.println("  3. Многопоточное тестирование");
            System.out.println("  4. Демонстрация Spring Beans");
            System.out.println("  0. Выход");
            System.out.print("Ваш выбор: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    createAndRunTest();
                    break;
                case "2":
                    runDemoTest();
                    break;
                case "3":
                    runConcurrentDemo();
                    break;
                case "4":
                    demonstrateSpringBeans();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Неверный ввод, попробуйте ещё раз.");
            }
        }
        System.out.println("\nДо свидания!");
        System.exit(0);
    }
    private static void runConcurrentDemo() {
        printSeparator();
        System.out.println("  ДЕМОНСТРАЦИЯ МНОГОПОТОЧНОСТИ");
        printSeparator();
        QuestionFactory factory = applicationContext.getBean(QuestionFactory.class);
        ScoringStrategy strategy = applicationContext.getBean("strictStrategy", ScoringStrategy.class);
        TestBuilder builder = new TestBuilder()
                .withTitle("Многопоточный Тест")
                .withScoringStrategy(strategy)
                .addObserver(applicationContext.getBean(TestObserver.class))
                .addQuestion(factory.createSingleChoice(
                        "Java поддерживает многопоточность?",
                        Arrays.asList("Да", "Нет", "Отчасти"),
                        0
                ))
                .addQuestion(factory.createMultipleChoice(
                        "Выберите методы синхронизации:",
                        Arrays.asList("synchronized", "Lock", "Semaphore", "Условное представление"),
                        new HashSet<>(Arrays.asList(0, 1, 2))
                ))
                .addQuestion(factory.createText(
                        "Как называется пул потоков в Java?",
                        "executorservice"
                ));
        Test test = builder.build();
        System.out.print("\nВведите количество потоков в пуле (1-10): ");
        int threadCount = 4;
        try {
            threadCount = Math.max(1, Math.min(10, Integer.parseInt(scanner.nextLine().trim())));
        } catch (NumberFormatException e) {
            System.out.println("⚠ Неверный ввод, используется значение по умолчанию: 4");
        }
        System.out.print("Введите количество пользователей для тестирования (1-20): ");
        int userCount = 5;
        try {
            userCount = Math.max(1, Math.min(20, Integer.parseInt(scanner.nextLine().trim())));
        } catch (NumberFormatException e) {
            System.out.println("⚠ Неверный ввод, используется значение по умолчанию: 5");
        }
        executorPool = new TestExecutorPool(threadCount, userCount);
        executorPool.start();
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Запуск " + userCount + " тестов в пуле из " + threadCount + " потоков");
        System.out.println("=".repeat(70));
        Map<Integer, Answer> answers = new ConcurrentHashMap<>();
        answers.put(0, Answer.ofSingle(0));
        answers.put(1, Answer.ofMulti(new HashSet<>(Arrays.asList(0, 1, 2))));
        answers.put(2, Answer.ofText("executorservice"));
        for (int i = 1; i <= userCount; i++) {
            String userId = "Пользователь_" + i;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            executorPool.submitTest(userId, test, answers);
        }
        System.out.println("\n[MAIN] Ожидание завершения всех тестов...");
        executorPool.waitForCompletion();
        System.out.println(executorPool.getStatistics());
        System.out.println("\nДЕТАЛЬНЫЕ РЕЗУЛЬТАТЫ:");
        System.out.println("=".repeat(70));
        for (TestResult result : executorPool.getAllResults()) {
            System.out.println(result);
        }
        System.out.println("=".repeat(70));
        executorPool.shutdown(30);
    }
    private static void createAndRunTest() {
        printSeparator();
        System.out.println("  СОЗДАНИЕ И ЗАПУСК ТЕСТА");
        printSeparator();
        System.out.print("Введите название теста: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) title = "Без названия";
        System.out.println("\nВыберите стратегию оценивания:");
        System.out.println("  1. Строгая");
        System.out.println("  2. Частичная");
        System.out.print("Ваш выбор (1/2): ");
        ScoringStrategy strategy;
        if (scanner.nextLine().trim().equals("2")) {
            strategy = applicationContext.getBean("partialStrategy", ScoringStrategy.class);
        } else {
            strategy = applicationContext.getBean("strictStrategy", ScoringStrategy.class);
        }
        QuestionFactory factory = applicationContext.getBean(QuestionFactory.class);
        TestBuilder builder = new TestBuilder()
                .withTitle(title)
                .withScoringStrategy(strategy)
                .addObserver(applicationContext.getBean(TestObserver.class));
        List<Question> addedQuestions = new ArrayList<>();
        boolean addingQuestions = true;
        while (addingQuestions) {
            System.out.println("\nДобавление вопроса #" + (addedQuestions.size() + 1) + ":");
            System.out.println("  1. Один верный ответ");
            System.out.println("  2. Несколько верных ответов");
            System.out.println("  3. Текстовый ответ");
            if (!addedQuestions.isEmpty()) System.out.println("  0. Закончить");
            System.out.print("Тип: ");
            String typeChoice = scanner.nextLine().trim();
            if (typeChoice.equals("0") && !addedQuestions.isEmpty()) { 
                addingQuestions = false; 
                continue; 
            }
            Question question = null;
            switch (typeChoice) {
                case "1":
                    question = createSingleChoiceQuestion(factory);
                    break;
                case "2":
                    question = createMultipleChoiceQuestion(factory);
                    break;
                case "3":
                    question = createTextQuestion(factory);
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
            if (question != null) {
                addedQuestions.add(question);
                builder.addQuestion(question);
                System.out.println("Вопрос добавлен! Всего: " + addedQuestions.size());
            }
        }
        Test test = builder.build();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Начинается тестирование: \"" + title + "\"");
        System.out.println("Всего вопросов: " + test.getQuestions().size());
        System.out.println("=".repeat(60));
        Map<Integer, Answer> answers = runQuestionsInteractive(test.getQuestions());
        double score = test.run(answers);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Ваш результат: " + String.format("%.1f", score * 100) + " баллов из 100");
        System.out.println("=".repeat(60));
    }
    private static void runDemoTest() {
        printSeparator();
        System.out.println("  ДЕМО-ТЕСТ");
        printSeparator();
        QuestionFactory factory = applicationContext.getBean(QuestionFactory.class);
        ScoringStrategy strategy = applicationContext.getBean("strictStrategy", ScoringStrategy.class);
        TestBuilder builder = new TestBuilder()
                .withTitle("Демонстрационный тест")
                .withScoringStrategy(strategy)
                .addObserver(applicationContext.getBean(TestObserver.class))
                .addQuestion(factory.createSingleChoice(
                        "Что такое Spring?",
                        Arrays.asList("Фреймворк", "Сезон", "Родник", "Пружина"),
                        0
                ))
                .addQuestion(factory.createMultipleChoice(
                        "Выберите парадигмы программирования:",
                        Arrays.asList("ООП", "Функциональное", "Логическое", "Императивное"),
                        new HashSet<>(Arrays.asList(0, 1, 3))
                ))
                .addQuestion(factory.createText(
                        "Как называется паттерн Singleton?",
                        "синглтон"
                ));
        Test test = builder.build();
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(0, Answer.ofSingle(0));
        answers.put(1, Answer.ofMulti(new HashSet<>(Arrays.asList(0, 1, 3))));
        answers.put(2, Answer.ofText("синглтон"));
        System.out.println("\n" + "=".repeat(60));
        double score = test.run(answers);
        System.out.println("\nВаш результат: " + String.format("%.1f", score * 100) + " баллов");
        System.out.println("=".repeat(60));
    }
    private static void demonstrateSpringBeans() {
        printSeparator();
        System.out.println("  ДЕМОНСТРАЦИЯ SPRING (IoC/DI + AOP)");
        printSeparator();
        QuestionFactory factory = applicationContext.getBean(QuestionFactory.class);
        ScoringStrategy strictStrategy = applicationContext.getBean("strictStrategy", ScoringStrategy.class);
        ScoringStrategy partialStrategy = applicationContext.getBean("partialStrategy", ScoringStrategy.class);
        TestObserver observer = applicationContext.getBean(TestObserver.class);
        System.out.println("\n✓ Полученные из контейнера Bean-ы:");
        System.out.println("  - QuestionFactory: " + factory.getClass().getSimpleName());
        System.out.println("  - StrictScoringStrategy: " + strictStrategy.getStrategyName());
        System.out.println("  - PartialScoringStrategy: " + partialStrategy.getStrategyName());
        System.out.println("  - TestObserver: " + observer.getClass().getSimpleName());
        System.out.println("\n⚙ IoC контейнер управляет всеми компонентами");
        System.out.println("✓ AOP аспекты логируют вызовы методов и обработку ошибок");
    }
    private static Map<Integer, Answer> runQuestionsInteractive(List<Question> questions) {
        Map<Integer, Answer> answers = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println("\nВопрос " + (i + 1) + ": " + q.getText());
            if (q.getType().equals("SINGLE_CHOICE") || q.getType().equals("MULTIPLE_CHOICE")) {
                List<String> options = q.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    System.out.println("  " + (j + 1) + ". " + options.get(j));
                }
                if (q.getType().equals("SINGLE_CHOICE")) {
                    System.out.print("Ваш ответ (номер): ");
                    answers.put(i, Answer.ofSingle(Integer.parseInt(scanner.nextLine().trim()) - 1));
                } else {
                    System.out.println("Введите номера ответов через запятую:");
                    String input = scanner.nextLine().trim();
                    Set<Integer> indices = new HashSet<>();
                    for (String idx : input.split(",")) {
                        indices.add(Integer.parseInt(idx.trim()) - 1);
                    }
                    answers.put(i, Answer.ofMulti(indices));
                }
            } else {
                System.out.print("Ваш ответ: ");
                answers.put(i, Answer.ofText(scanner.nextLine().trim()));
            }
        }
        return answers;
    }
    private static Question createSingleChoiceQuestion(QuestionFactory factory) {
        System.out.print("Текст вопроса: ");
        String text = scanner.nextLine().trim();
        List<String> options = new ArrayList<>();
        System.out.print("Количество ответов: ");
        int count = 2;
        try {
            count = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("⚠ Неверный ввод, используется значение по умолчанию: 2");
        }
        for (int i = 0; i < count; i++) {
            System.out.print("  Вариант " + (i + 1) + ": ");
            options.add(scanner.nextLine().trim());
        }
        System.out.print("Индекс верного ответа: ");
        int correctIndex = 0;
        try {
            correctIndex = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("⚠ Неверный ввод, используется значение по умолчанию: 0");
        }
        return factory.createSingleChoice(text, options, correctIndex);
    }
    private static Question createMultipleChoiceQuestion(QuestionFactory factory) {
        System.out.print("Текст вопроса: ");
        String text = scanner.nextLine().trim();
        List<String> options = new ArrayList<>();
        System.out.print("Количество ответов: ");
        int count = 2;
        try {
            count = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("⚠ Неверный ввод, используется значение по умолчанию: 2");
        }
        for (int i = 0; i < count; i++) {
            System.out.print("  Вариант " + (i + 1) + ": ");
            options.add(scanner.nextLine().trim());
        }
        System.out.println("Введите индексы верных ответов через запятую:");
        String input = scanner.nextLine().trim();
        Set<Integer> correctIndices = new HashSet<>();
        for (String idx : input.split(",")) {
            try {
                correctIndices.add(Integer.parseInt(idx.trim()));
            } catch (NumberFormatException e) {
                System.out.println("⚠ '" + idx.trim() + "' не число, пропускаю");
            }
        }
        if (correctIndices.isEmpty()) {
            correctIndices.add(0);
            System.out.println("⚠ Добавлен индекс по умолчанию: 0");
        }
        return factory.createMultipleChoice(text, options, correctIndices);
    }
    private static Question createTextQuestion(QuestionFactory factory) {
        System.out.print("Текст вопроса: ");
        String text = scanner.nextLine().trim();
        System.out.print("Верный ответ: ");
        String answer = scanner.nextLine().trim();
        return factory.createText(text, answer);
    }
    private static void printSeparator() {
        System.out.println("\n" + "=".repeat(70));
    }
}

